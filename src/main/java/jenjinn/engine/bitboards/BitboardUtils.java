/**
 *
 */
package jenjinn.engine.bitboards;

import static java.lang.Long.bitCount;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.LongFlow;
import xawd.jflow.iterators.construction.Iterate;

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
		long result = 0L;
		for (final long arg : args) {
			result |= arg;
		}
		return result;
	}

	public static long bitwiseOr(final BoardSquare... args)
	{
		long result = 0L;
		for (final BoardSquare arg : args) {
			result |= arg.asBitboard();
		}
		return result;
	}

	public static long bitwiseOr(final List<BoardSquare> args)
	{
		return bitwiseOr(Iterate.over(args));
	}

	public static long bitwiseOr(final Flow<BoardSquare> args)
	{
		return bitwiseOr(args.mapToLong(BoardSquare::asBitboard));
	}

	public static long bitwiseOr(final LongFlow args)
	{
		return args.reduce(0L, (a, b) -> a | b);
	}

	public static long bitwiseXor(final long... args)
	{
		return bitwiseXor(Iterate.over(args));
	}

	public static long bitwiseXor(final LongFlow args)
	{
		return args.reduce(0L, (a, b) -> a ^ b);
	}

	public static int[] getSetBitIndices(final long bitboard)
	{
		final int cardinality = bitCount(bitboard);
		final int[] setBits = new int[cardinality];

		byte arrCount = 0;
		for (byte i = 0; i < 64 && arrCount < cardinality; i++) {
			if (bitboardsIntersect(1L << i, bitboard)) {
				setBits[arrCount++] = i;
			}
		}

		return setBits;
	}
}
