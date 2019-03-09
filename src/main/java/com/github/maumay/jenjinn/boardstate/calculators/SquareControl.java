/**
 *
 */
package com.github.maumay.jenjinn.boardstate.calculators;

import static com.github.maumay.jenjinn.bitboards.Bitboard.intersects;
import static com.github.maumay.jenjinn.bitboards.Bitboards.file;
import static com.github.maumay.jenjinn.bitboards.Bitboards.rank;

import com.github.maumay.jenjinn.base.Side;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.DetailedPieceLocations;
import com.github.maumay.jenjinn.pieces.ChessPieces;
import com.github.maumay.jenjinn.pieces.Piece;

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
