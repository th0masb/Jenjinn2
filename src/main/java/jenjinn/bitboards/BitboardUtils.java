/**
 *
 */
package jenjinn.bitboards;

import jenjinn.base.Square;
import jflow.iterators.Flow;

/**
 * @author t
 */
public final class BitboardUtils
{
	private BitboardUtils() {}

	public static boolean bitboardsIntersect(long bitboardA, long bitboardB)
	{
		return (bitboardA & bitboardB) != 0;
	}

	public static long bitwiseOr(long... args)
	{
		long result = 0L;
		for (long arg : args) {
			result |= arg;
		}
		return result;
	}

	public static long bitwiseOr(Iterable<Square> args)
	{
		long result = 0L;
		for (Square arg : args) {
			result |= arg.bitboard;
		}
		return result;
	}

	public static long bitwiseOr(Flow<Square> args)
	{
		return args.mapToLong(sq -> sq.bitboard).fold(0L, (a, b) -> a | b);
	}
}
