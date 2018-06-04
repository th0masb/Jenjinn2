/**
 *
 */
package jenjinn.engine.bitboards;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import xawd.jflow.iterators.Flow;

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

	public static long bitwiseOr(final List<BoardSquare> args)
	{
		long result = 0L;
		for (final BoardSquare arg : args) {
			result |= arg.asBitboard();
		}
		return result;
	}

	public static long bitwiseOr(final Flow<BoardSquare> args)
	{
		return args.mapToLong(BoardSquare::asBitboard).reduce(0L, (a, b) -> a | b);
	}

//	public static Flow<BoardSquare> iterateSelectedSquares(final long bitboard)
//	{
//		return new BitboardIterator(bitboard);
//	}
}
