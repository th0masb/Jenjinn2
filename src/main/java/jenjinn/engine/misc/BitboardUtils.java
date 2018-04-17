/**
 * 
 */
package jenjinn.engine.misc;

import static io.xyz.chains.utilities.MapUtil.longMap;
import static java.lang.Long.bitCount;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;

/**
 * @author t
 */
public final class BitboardUtils 
{
	private BitboardUtils() {}
	
	public static boolean bitboardsIntersect(final long bitboardA, final long bitboardB)
	{
		return (bitboardA & bitboardB) != 0;
	}
	
	public static long bitwiseOr(final long... args)
	{
		long ans = 0L;
		for (final long arg : args) {
			ans |= arg;
		}
		return ans;
	}
	
	public static long bitwiseOr(final BoardSquare... args)
	{
		return bitwiseOr(longMap(BoardSquare::asBitboard, args));
	}
	
	public static long bitwiseOr(final List<BoardSquare> args)
	{
		return bitwiseOr(longMap(BoardSquare::asBitboard, args));
	}

	public static long bitwiseXor(final long... args)
	{
		long ans = 0L;
		for (final long arg : args) {
			ans ^= arg;
		}
		return ans;
	}
	
	public static int[] getSetBitIndices(long bitboard)
	{
		final int cardinality = bitCount(bitboard);
		final int[] setBits = new int[cardinality];
		int arrCounter = 0, loopCounter = 0;

		while (bitboard != 0) {
			if ((1 & bitboard) != 0) {
				setBits[arrCounter++] = loopCounter;
			}
			loopCounter++;
			bitboard >>>= 1;
		}
		return setBits;
	}
}
