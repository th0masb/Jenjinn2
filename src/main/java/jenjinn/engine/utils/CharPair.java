/**
 *
 */
package jenjinn.engine.utils;

import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 *
 */
public final class CharPair
{
	public final char first, second;

	public CharPair(final char first, final char second)
	{
		this.first = first;
		this.second = second;
	}

	public static CharPair of(final char first, final char second)
	{
		return new CharPair(first, second);
	}

	public static CharPair from(final ChessPiece piece)
	{
		if (piece == ChessPiece.WHITE_KNIGHT) {
			return of('N', 'W');
		}
		else if (piece == ChessPiece.BLACK_KNIGHT) {
			return of('N', 'B');
		}
		else {
			final String[] split = piece.name().split("_");
			return new CharPair(split[1].charAt(0), split[0].charAt(0));
		}
	}

	public char[] toArray()
	{
		return new char[] {first, second};
	}
}
