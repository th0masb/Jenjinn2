/**
 *
 */
package jenjinn.engine.boardstate.propertycalculators;

import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static xawd.jflow.utilities.CollectionUtil.tail;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnpassantMove;
import jenjinn.engine.moves.PromotionMove;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.construction.EmptyIteration;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author ThomasB
 */
public final class MoveCalculator {

	private MoveCalculator() {}

	public static List<ChessMove> getAvailableMoves(final BoardState state)
	{
		final long passiveControl = SquareControl.calculate(state, state.getActiveSide().otherSide());

		return getCastleMoves(state, passiveControl)
				.append(getEnpassantMoves(state))
				.append(getStandardAndPromotionMoves(state, passiveControl))
				.toList();
	}

	static Flow<ChessMove> getEnpassantMoves(final BoardState state)
	{
		if (state.getEnPassantSquare() != null) {
			final Side activeSide = state.getActiveSide();
			final BoardSquare enpassantSquare = state.getEnPassantSquare();
			final long pawnLocs = state.getPieceLocations().locationOverviewOf(ChessPieces.pawn(activeSide));
			final List<Direction> inverseAttackDirections = activeSide.isWhite()? asList(Direction.SW, Direction.SE) : asList(Direction.NW, Direction.NE);
			return Iterate.over(inverseAttackDirections)
					.map(enpassantSquare::getNextSquareInDirection)
					.filter(sq -> sq != null && bitboardsIntersect(pawnLocs, sq.asBitboard()))
					.map(sq -> new EnpassantMove(sq, enpassantSquare));
		}
		else {
			return EmptyIteration.ofObjects();
		}
	}

	static Flow<ChessMove> getCastleMoves(final BoardState state, final long passiveControl)
	{
		if (state.getCastlingStatus().getStatusFor(state.getActiveSide()) == null) {
			final Side activeSide = state.getActiveSide();
			final Predicate<CastleZone> sideFilter = activeSide.isWhite()? z -> z.isWhiteZone() : z -> !z.isWhiteZone();
			final Set<CastleZone> allRights = state.getCastlingStatus().getCastlingRights();
			final Flow<CastleZone> availableRights = Iterate.over(allRights).filter(sideFilter);
			final long allPieces = state.getPieceLocations().getAllLocations();
			final Flow<CastleZone> legalAvailableRights = availableRights.filter(zone ->
			{
				final long reqClearArea = zone.getRequiredFreeSquares();
				final long kingLoc = state.getPieceLocations().locationOverviewOf(ChessPieces.king(activeSide));
				return !bitboardsIntersect(reqClearArea, allPieces)
						&& !bitboardsIntersect(passiveControl, kingLoc | reqClearArea);
			});
			return legalAvailableRights.map(MoveCache::getMove);
		}
		else {
			return EmptyIteration.ofObjects();
		}
	}

	static Flow<ChessMove> getStandardAndPromotionMoves(final BoardState state, final long passiveControl)
	{
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		final List<ChessPiece> activePieces = ChessPieces.ofSide(state.getActiveSide());

		final Flow<ChessMove> allMoves = Iterate.over(activePieces)
				.take(5)
				.flatten(piece -> pieceLocs.iterateLocs(piece).flatten(loc -> bitboard2moves(piece, loc, piece.getMoves(loc, white, black))));

		final ChessPiece activeKing = tail(activePieces);
		final BoardSquare kingLoc = pieceLocs.iterateLocs(activeKing).next();
		final Flow<ChessMove> kingMoves = bitboard2moves(activeKing, kingLoc, activeKing.getMoves(kingLoc, white, black) & ~passiveControl);

		return allMoves.append(kingMoves);
	}

	static Flow<ChessMove> bitboard2moves(final ChessPiece piece, final BoardSquare source, final long bitboard)
	{
		if (piece.isPawn() && onPenultimateRank(source, piece.getSide())) {
			return BitboardIterator.from(bitboard).map(target -> new PromotionMove(source, target));
		}
		else {
			return BitboardIterator.from(bitboard).map(target -> MoveCache.getMove(source, target));
		}
	}

	private static boolean onPenultimateRank(final BoardSquare query, final Side side)
	{
		return side.isWhite()? query.rank() == 6 : query.rank() == 1;
	}
}