/**
 *
 */
package jenjinn.engine.base;

import static java.util.Arrays.asList;

import java.util.Map;

import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public enum DevelopmentPiece
{
	WHITE_KINGSIDE_KNIGHT(BoardSquare.G1),
	WHITE_KINGSIDE_BISHOP(BoardSquare.F1),
	WHITE_E_PAWN(BoardSquare.E2),
	WHITE_D_PAWN(BoardSquare.D2),
	WHITE_QUEENSIDE_BISHOP(BoardSquare.C1),
	WHITE_QUEENSIDE_KNIGHT(BoardSquare.B1),

	BLACK_KINGSIDE_KNIGHT(BoardSquare.G8),
	BLACK_KINGSIDE_BISHOP(BoardSquare.F8),
	BLACK_E_PAWN(BoardSquare.E7),
	BLACK_D_PAWN(BoardSquare.D7),
	BLACK_QUEENSIDE_BISHOP(BoardSquare.C8),
	BLACK_QUEENSIDE_KNIGHT(BoardSquare.B8);

	private final BoardSquare startSquare;

	private DevelopmentPiece(BoardSquare startSquare)
	{
		this.startSquare = startSquare;
	}

	private static final Map<BoardSquare, DevelopmentPiece> START_SQUARE_MAPPING =
			Iterate.over(asList(values())).toMap(x -> x.startSquare, x -> x);

	public static DevelopmentPiece fromStartSquare(BoardSquare startSquare)
	{
		return START_SQUARE_MAPPING.get(startSquare);
	}
}
