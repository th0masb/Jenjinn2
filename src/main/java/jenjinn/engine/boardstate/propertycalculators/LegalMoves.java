/**
 *
 */
package jenjinn.engine.boardstate.propertycalculators;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.PromotionMove;
import xawd.jflow.iterators.Flow;

/**
 * @author ThomasB
 */
public final class LegalMoves {

	private LegalMoves() {}

	public static List<ChessMove> forState(final BoardState state)
	{
		final Side active = state.getActiveSide(), passive = active.otherSide();
		final long passiveControl = SquareControl.calculate(state, passive);
		final long activeKingLoc = state.getPieceLocations().locationOverviewOf(ChessPieces.king(active));
		if (bitboardsIntersect(passiveControl, activeKingLoc)) {
			return getMovesOutOfCheck(state, passiveControl);
		}
		else {
			return getLegalMoves(state, passiveControl);
		}
	}

	private static List<ChessMove> getMovesOutOfCheck(final BoardState state, final long passiveControl)
	{
		final Side active = state.getActiveSide();
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final ChessPiece activeKing = ChessPieces.king(active);
		final BoardSquare activeKingLoc = pieceLocs.iterateLocs(activeKing).next();

		final List<ChessPiece> attackers = new ArrayList<>(2);
		final List<BoardSquare> attackSources = new ArrayList<>(2);

		final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations(), kloc = activeKingLoc.asBitboard();

		PIECE_LOOP:
		for (final ChessPiece potentialAttacker : ChessPieces.ofSide(active.otherSide())) {
			final Flow<BoardSquare> locs = pieceLocs.iterateLocs(potentialAttacker);
			while (locs.hasNext()) {
				final BoardSquare loc = locs.next();
				final long attacks = potentialAttacker.getSquaresOfControl(loc, white, black);
				if (bitboardsIntersect(attacks, kloc)) {
					attackers.add(potentialAttacker);
					attackSources.add(loc);
					if (attackers.size() == 2) {
						break PIECE_LOOP;
					}
				}
			}
		}

		// double check, only solved by king moving
		if (attackers.size() == 2) {
			final long availableMoves = activeKing.getMoves(activeKingLoc, white, black) & ~passiveControl;
			return bitboard2moves(activeKing, activeKingLoc, availableMoves).toList();
		}
		else {
			final long availableMoves = activeKing.getMoves(activeKingLoc, white, black) & ~passiveControl;
			final Flow<ChessMove> kingMoves = bitboard2moves(activeKing, activeKingLoc, availableMoves);

		}
		/*
		 *  First find list of pieces and their squares causing check, if one then move, take or block
		 *  is possible. If two then we have to try and move.
		 */
		throw new RuntimeException();
	}

	private static List<ChessMove> getLegalMoves(final BoardState state, final long passiveControl)
	{
		 /*
		  * We must take into account possible pins, Standard sliding moves induce 'cord' bitboards which
		  * can be used to check for pins.
		  */
		throw new RuntimeException();
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
