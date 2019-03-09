/**
 *
 */
package jenjinn.boardstate.calculators;

import static jenjinn.bitboards.Bitboard.intersects;
import static jenjinn.bitboards.Bitboards.file;
import static jenjinn.bitboards.Bitboards.rank;

import jenjinn.base.Side;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.DetailedPieceLocations;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;

/**
 * @author ThomasB
 */
public final class SquareControl
{

	private SquareControl()
	{
	}

	public static long calculate(BoardState state, Side side)
	{
		return ChessPieces.of(side).iter().mapToLong(piece -> calculate(state, piece))
				.fold(0L, (a, b) -> a | b);
	}

	public static long calculate(BoardState state, Piece piece)
	{
		if (piece.isPawn()) {
			return calculatePawn(state, piece);
		} else {
			DetailedPieceLocations pieceLocs = state.getPieceLocations();
			long white = pieceLocs.getWhiteLocations(),
					black = pieceLocs.getBlackLocations();
			return pieceLocs.iterateLocs(piece)
					.mapToLong(loc -> piece.getSquaresOfControl(loc, white, black))
					.fold(0L, (a, b) -> a | b);
		}
	}

	private static long calculatePawn(BoardState state, Piece piece)
	{
		assert piece.isPawn();
		long pawnLocs = state.getPieceLocations().locationsOf(piece);
		assert !intersects(pawnLocs, rank(0)) && !intersects(pawnLocs, rank(7));
		long aFileRemover = ~file(7), hFileRemover = ~file(0);
		if (piece.isWhite()) {
			return ((pawnLocs & aFileRemover) << 9) | ((pawnLocs & hFileRemover) << 7);
		} else {
			return ((pawnLocs & aFileRemover) >>> 7) | ((pawnLocs & hFileRemover) >>> 9);
		}
	}
}
