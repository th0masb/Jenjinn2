/**
 *
 */
package com.github.maumay.jenjinn.pieces;

import static com.github.maumay.jenjinn.pieces.Piece.BLACK_BISHOP;
import static com.github.maumay.jenjinn.pieces.Piece.BLACK_QUEEN;
import static com.github.maumay.jenjinn.pieces.Piece.BLACK_ROOK;
import static com.github.maumay.jenjinn.pieces.Piece.WHITE_BISHOP;
import static com.github.maumay.jenjinn.pieces.Piece.WHITE_QUEEN;
import static com.github.maumay.jenjinn.pieces.Piece.WHITE_ROOK;

import com.github.maumay.jenjinn.base.Side;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 *
 */
public final class ChessPieces
{
	private ChessPieces()
	{
	}

	public static final Vec<Piece> ALL = Vec.of(Piece.values());
	public static final Vec<Piece> WHITE = ALL.take(6);
	public static final Vec<Piece> BLACK = ALL.skip(6);

	private static final Vec<Piece> WHITE_PINNING_PIECES = Vec.of(WHITE_QUEEN, WHITE_ROOK,
			WHITE_BISHOP);
	private static final Vec<Piece> BLACK_PINNING_PIECES = Vec.of(BLACK_QUEEN, BLACK_ROOK,
			BLACK_BISHOP);

	public static Vec<Piece> whitePinners()
	{
		return WHITE_PINNING_PIECES;
	}

	public static Vec<Piece> blackPinners()
	{
		return BLACK_PINNING_PIECES;
	}

	public static Vec<Piece> pinnersOn(Side side)
	{
		return side.isWhite() ? WHITE_PINNING_PIECES : BLACK_PINNING_PIECES;
	}

	public static Vec<Piece> of(Side side)
	{
		return side.isWhite() ? WHITE : BLACK;
	}

	public static Piece fromIndex(int index)
	{
		return Piece.values()[index];
	}
}
