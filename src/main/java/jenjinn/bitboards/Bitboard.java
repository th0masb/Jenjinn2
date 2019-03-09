/**
 *
 */
package jenjinn.bitboards;

import java.util.Iterator;

import jenjinn.base.Square;

/**
 * @author t
 */
public final class Bitboard
{
	private Bitboard()
	{
	}

	public static boolean intersects(long bitboardA, long bitboardB)
	{
		return (bitboardA & bitboardB) != 0;
	}

	public static long fold(long... args)
	{
		long result = 0L;
		for (long arg : args) {
			result |= arg;
		}
		return result;
	}

	public static long fold(Iterable<Square> args)
	{
		long result = 0L;
		for (Square arg : args) {
			result |= arg.bitboard;
		}
		return result;
	}

	public static long fold(Iterator<? extends Square> args)
	{
		long result = 0L;
		while (args.hasNext()) {
			result |= args.next().bitboard;
		}
		return result;
	}

	public static BitboardIterator iter(long bitboard)
	{
		return new BitboardIterator(bitboard);
	}
}
