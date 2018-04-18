/**
 * 
 */
package jenjinn.engine.enums;

/**
 * @author ThomasB
 */
public enum ChessPiece
{
	// DON'T CHANGE ORDER 
	WHITE_PAWN, 
	WHITE_KNIGHT,
	WHITE_BISHOP,
	WHITE_ROOK,
	WHITE_QUEEN,
	WHITE_KING,
	
	BLACK_PAWN,
	BLACK_KNIGHT,
	BLACK_BISHOP,
	BLACK_ROOK,
	BLACK_QUEEN,
	BLACK_KING;
	
	public static ChessPiece fromIndex(final int index)
	{
		return values()[index];
	}
	
	public boolean isPawn()
	{
		return ordinal() % 6 == 0;
	}
	
	public boolean isWhite()
	{
		return ordinal() < 6;
	}
}
