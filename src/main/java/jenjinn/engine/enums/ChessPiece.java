/**
 * 
 */
package jenjinn.engine.enums;

import static io.xyz.chains.utilities.CollectionUtil.drop;
import static io.xyz.chains.utilities.CollectionUtil.take;

import java.util.List;

import com.google.common.collect.ImmutableList;

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
	
	public boolean isPawn()
	{
		return ordinal() % 6 == 0;
	}
	
	public boolean isWhite()
	{
		return ordinal() < 6;
	}
	
	private static final List<ChessPiece> ALL_PIECES = ImmutableList.copyOf(values());
	private static final List<ChessPiece> WHITE_PIECES = take(6, ALL_PIECES), BLACK_PIECES = drop(6, ALL_PIECES);
	
	public static List<ChessPiece> allPieces()
	{
		return ALL_PIECES;
	}
	
	public static List<ChessPiece> whitePieces()
	{
		return WHITE_PIECES;
	}
	
	public static List<ChessPiece> blackPieces()
	{
		return BLACK_PIECES;
	}
	
	public static ChessPiece fromIndex(final int index)
	{
		return values()[index];
	}
}
