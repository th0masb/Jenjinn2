/**
 *
 */
package jenjinn.engine.boardstate.propertycalculators;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static xawd.jflow.utilities.CollectionUtil.tail;

import java.util.Collections;
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
import jenjinn.engine.enums.Side;
import jenjinn.engine.moves.CastleMove;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.StandardMove;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author ThomasB
 */
public final class LegalMoveCalculator {

	private LegalMoveCalculator() {}

	public static List<ChessMove> getAvailableMoves(final BoardState state)
	{
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long whiteLocs = pieceLocs.getWhiteLocations(), blackLocs = pieceLocs.getBlackLocations();

		throw new RuntimeException();
	}

	static List<CastleMove> getCastleMoves(final BoardState state, final long passiveControl)
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
			return legalAvailableRights.map(MoveCache::getMove).toList();
		}
		else {
			return Collections.emptyList();
		}
	}

	static List<StandardMove> getStandardMoves(final BoardState state, final long passiveControl)
	{
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		final List<ChessPiece> activePieces = ChessPieces.ofSide(state.getActiveSide());

		final Flow<StandardMove> allMoves = Iterate.over(activePieces)
				.take(5)
				.flatten(piece -> pieceLocs.iterateLocs(piece).flatten(loc -> bitboard2moves(loc, piece.getMoves(loc, white, black))));

		final ChessPiece activeKing = tail(activePieces);
		final BoardSquare kingLoc = pieceLocs.iterateLocs(activeKing).next();
		final Flow<StandardMove> kingMoves = bitboard2moves(kingLoc, activeKing.getMoves(kingLoc, white, black) & ~passiveControl);

		return allMoves.append(kingMoves).toList();
	}

	static Flow<StandardMove> bitboard2moves(final BoardSquare source, final long bitboard)
	{
		return BitboardIterator.from(bitboard).map(target -> MoveCache.getMove(source, target));
	}
}