package jenjinn.engine.bitboards;

import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.MapUtil.longMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.misc.PieceMovementDirections;
import xawd.jflow.construction.Iter;
import xawd.jflow.construction.IterRange;

/**
 * Second of three utility classes containing only static methods to initialise
 * the constants in the BBDB class. This class initialises everything we need to
 * generate the magic move databases for sliding pieces (and for the pawn first
 * moves).
 *
 * @author TB
 * @date 23 Jan 2017
 */
public class BitboardsInitialisationSection2
{
	public static long[][] generateAllBishopOccupancyVariations()
	{
		return BoardSquare.iterate()
				.map(square -> calculateOccupancyVariations(square, PieceMovementDirections.BISHOP))
				.toList()
				.toArray(new long[64][]);
	}

	public static long[][] generateAllRookOccupancyVariations()
	{
		return BoardSquare.iterate()
				.map(square -> calculateOccupancyVariations(square, PieceMovementDirections.ROOK))
				.toList()
				.toArray(new long[64][]);
	}

	public static long[] calculateOccupancyVariations(final BoardSquare startSq, final List<Direction> movementDirections)
	{
		final List<BoardSquare> relevantSquares = new ArrayList<>();
		for (final Direction dir : movementDirections) {
			final int numOfSqsLeft = startSq.getNumberOfSquaresLeftInDirection(dir);
			relevantSquares.addAll(startSq.getAllSquaresInDirections(Arrays.asList(dir), numOfSqsLeft - 1));
		}
		return findAllPossibleOrCombos(longMap(BoardSquare::asBitboard, relevantSquares));
	}

	/**
	 * Recursive method to calculate and return all possible bitboards arising from
	 * performing bitwise | operation on each element of each subset of the powerset
	 * of the given array. The size of the returned array is 2^(array.length).
	 */
	private static long[] findAllPossibleOrCombos(final long[] array)
	{
		final int length = array.length;
		if (length == 1) {
			return new long[] { 0L, array[0] };
		}
		else {
			final long[] ans = new long[(int) Math.pow(2.0, length)];
			final long[] recursiveAns = findAllPossibleOrCombos(Iter.of(array).take(length - 1).toArray());
			int ansIndexCounter = 0;
			int recursiveAnsIndexCounter = 0;
			for (int j = 0; j < recursiveAns.length; j++) {
				for (long i = 0; i < 2; i++) {
					ans[ansIndexCounter] = recursiveAns[recursiveAnsIndexCounter] | (array[length - 1] * i);
					ansIndexCounter++;
				}
				recursiveAnsIndexCounter++;
			}
			return ans;
		}
	}

	public static long[] generateRookOccupancyMasks()
	{
		return IterRange.to(64).mapToLong(i -> tail(Bitboards.ROOK_OCCUPANCY_VARIATIONS[i])).toArray();
	}

	public static long[] generateBishopOccupancyMasks()
	{
		return IterRange.to(64).mapToLong(i -> tail(Bitboards.BISHOP_OCCUPANCY_VARIATIONS[i])).toArray();
	}

	public static int[] generateRookMagicBitshifts()
	{
		return Iter.of(Bitboards.ROOK_OCCUPANCY_MASKS).mapToInt(x -> 64 - Long.bitCount(x)).toArray();
	}

	public static int[] generateBishopMagicBitshifts()
	{
		return Iter.of(Bitboards.BISHOP_OCCUPANCY_MASKS).mapToInt(x -> 64 - Long.bitCount(x)).toArray();
	}
}
