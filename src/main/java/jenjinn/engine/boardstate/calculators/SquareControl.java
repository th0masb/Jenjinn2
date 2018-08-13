/**
 *
 */
package jenjinn.engine.boardstate.calculators;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.Bitboards.fileBitboard;
import static jenjinn.engine.bitboards.Bitboards.rankBitboard;

import jenjinn.engine.base.Side;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
public final class SquareControl {

	private SquareControl() {}

	public static long calculate(BoardState state, Side side)
	{
		return Iterate.over(ChessPieces.ofSide(side))
				.mapToLong(piece -> calculate(state, piece))
				.fold(0L, (a, b) -> a | b);
	}

	public static long calculate(BoardState state, ChessPiece piece)
	{
		if (piece.isPawn()) {
			return calculatePawn(state, piece);
		}
		else {
			DetailedPieceLocations pieceLocs = state.getPieceLocations();
			long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
			return pieceLocs.iterateLocs(piece)
					.mapToLong(loc -> piece.getSquaresOfControl(loc, white, black))
					.fold(0L, (a, b) -> a | b);
		}
	}

	private static long calculatePawn(BoardState state, ChessPiece piece)
	{
		assert piece.isPawn();
		long pawnLocs = state.getPieceLocations().locationsOf(piece);
		assert !bitboardsIntersect(pawnLocs, rankBitboard(0)) && !bitboardsIntersect(pawnLocs, rankBitboard(7));
		long aFileRemover = ~fileBitboard(7), hFileRemover = ~fileBitboard(0);
		if (piece.isWhite()) {
			return ((pawnLocs & aFileRemover) << 9) | ((pawnLocs & hFileRemover) << 7);
		}
		else {
			return ((pawnLocs & aFileRemover) >>> 7) | ((pawnLocs & hFileRemover) >>> 9);
		}
	}
}
