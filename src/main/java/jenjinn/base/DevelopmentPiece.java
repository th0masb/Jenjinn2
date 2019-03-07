/**
 *
 */
package jenjinn.base;

import java.util.Map;

import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 *
 */
public enum DevelopmentPiece
{
	WHITE_KINGSIDE_KNIGHT(Square.G1), WHITE_KINGSIDE_BISHOP(Square.F1),
	WHITE_E_PAWN(Square.E2), WHITE_D_PAWN(Square.D2), WHITE_QUEENSIDE_BISHOP(Square.C1),
	WHITE_QUEENSIDE_KNIGHT(Square.B1),

	BLACK_KINGSIDE_KNIGHT(Square.G8), BLACK_KINGSIDE_BISHOP(Square.F8),
	BLACK_E_PAWN(Square.E7), BLACK_D_PAWN(Square.D7), BLACK_QUEENSIDE_BISHOP(Square.C8),
	BLACK_QUEENSIDE_KNIGHT(Square.B8);

	private final Square startSquare;

	private DevelopmentPiece(Square startSquare)
	{
		this.startSquare = startSquare;
	}

	private static final Map<Square, DevelopmentPiece> START_SQUARE_MAPPING = Vec
			.of(values()).toMap(x -> x.startSquare, x -> x);

	public static DevelopmentPiece fromStartSquare(Square startSquare)
	{
		return START_SQUARE_MAPPING.get(startSquare);
	}
}
