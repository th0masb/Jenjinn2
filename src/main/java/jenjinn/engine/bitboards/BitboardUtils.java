/**
 *
 */
package jenjinn.engine.bitboards;

import static java.lang.Long.bitCount;

import java.util.Arrays;
import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import xawd.jflow.Flow;
import xawd.jflow.LongFlow;
import xawd.jflow.construction.Iter;

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
		return bitwiseOr(Iter.of(args));
	}

	public static long bitwiseOr(final BoardSquare... args)
	{
		return bitwiseOr(Arrays.asList(args));
	}

	public static long bitwiseOr(final List<BoardSquare> args)
	{
		return bitwiseOr(Iter.of(args));
	}

	public static long bitwiseOr(final Flow<BoardSquare> args)
	{
		return bitwiseOr(args.mapToLong(BoardSquare::asBitboard));
	}

	public static long bitwiseOr(LongFlow args)
	{
		return args.reduce(0L, (a, b) -> a | b);
	}

	public static long bitwiseXor(final long... args)
	{
		return bitwiseXor(Iter.of(args));
	}

	public static long bitwiseXor(LongFlow args)
	{
		return args.reduce(0L, (a, b) -> a ^ b);
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
