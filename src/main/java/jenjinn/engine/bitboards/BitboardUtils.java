/**
 *
 */
package jenjinn.engine.bitboards;

import jenjinn.engine.base.BoardSquare;
import xawd.jflow.iterators.Flow;

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

	public static long bitwiseOr(Iterable<BoardSquare> args)
	{
		long result = 0L;
		for (BoardSquare arg : args) {
			result |= arg.asBitboard();
		}
		return result;
	}

	public static long bitwiseOr(Flow<BoardSquare> args)
	{
		return args.mapToLong(BoardSquare::asBitboard).fold(0L, (a, b) -> a | b);
	}
}
