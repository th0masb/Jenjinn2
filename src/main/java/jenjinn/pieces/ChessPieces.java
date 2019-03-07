/**
 *
 */
package jenjinn.pieces;

import static jenjinn.pieces.Piece.BLACK_BISHOP;
import static jenjinn.pieces.Piece.BLACK_QUEEN;
import static jenjinn.pieces.Piece.BLACK_ROOK;
import static jenjinn.pieces.Piece.WHITE_BISHOP;
import static jenjinn.pieces.Piece.WHITE_QUEEN;
import static jenjinn.pieces.Piece.WHITE_ROOK;

import jenjinn.base.Side;
import jflow.seq.Seq;

/**
 * @author ThomasB
 *
 */
public final class ChessPieces
{
	private ChessPieces() {}

	public static final Seq<Piece> ALL = Seq.of(Piece.values());
	public static final Seq<Piece> WHITE = ALL.take(6);
	public static final Seq<Piece> BLACK = ALL.drop(6);
	
	private static final Seq<Piece> WHITE_PINNING_PIECES = Seq.of(WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP);
	private static final Seq<Piece> BLACK_PINNING_PIECES = Seq.of(BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP);

	public static Seq<Piece> whitePinners()
	{
		return WHITE_PINNING_PIECES;
	}

	public static Seq<Piece> blackPinners()
	{
		return BLACK_PINNING_PIECES;
	}

	public static Seq<Piece> pinnersOn(Side side)
	{
		return side.isWhite()? WHITE_PINNING_PIECES : BLACK_PINNING_PIECES;
	}

	public static Seq<Piece> of(Side side)
	{
		return side.isWhite()? WHITE : BLACK;
	}

	public static Piece fromIndex(int index)
	{
		return Piece.values()[index];
	}
}
