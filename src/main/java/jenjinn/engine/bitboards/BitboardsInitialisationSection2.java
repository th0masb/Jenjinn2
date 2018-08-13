package jenjinn.engine.bitboards;

import static java.util.Arrays.asList;
import static xawd.jflow.utilities.CollectionUtil.last;
import static xawd.jflow.utilities.CollectionUtil.take;
import static xawd.jflow.utilities.MapUtil.longMap;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Direction;
import jenjinn.engine.pieces.PieceMovementDirections;
import xawd.jflow.iterators.factories.IterRange;
import xawd.jflow.iterators.factories.Iterate;

/**
 * Second of three utility classes containing only static methods to initialise
 * the constants in the BBDB class. This class initialises everything we need to
 * generate the magic move databases for sliding pieces (and for the pawn first
 * moves).
 *
 * @author TB
 * @date 23 Jan 2017
 */
final class BitboardsInitialisationSection2
{
	static long[][] generateAllBishopOccupancyVariations()
	{
		return BoardSquare.iterateAll()
				.map(square -> calculateOccupancyVariations(square, PieceMovementDirections.BISHOP))
				.toList()
				.toArray(new long[64][]);
	}

	static long[][] generateAllRookOccupancyVariations()
	{
		return BoardSquare.iterateAll()
				.map(square -> calculateOccupancyVariations(square, PieceMovementDirections.ROOK))
				.toList()
				.toArray(new long[64][]);
	}

	static long[] calculateOccupancyVariations(BoardSquare startSq, List<Direction> movementDirections)
	{
		List<BoardSquare> relevantSquares = new ArrayList<>();
		for (Direction dir : movementDirections)
		{
			int numOfSqsLeft = startSq.getNumberOfSquaresLeftInDirection(dir);
			relevantSquares.addAll(startSq.getAllSquaresInDirections(asList(dir), numOfSqsLeft - 1));
		}
		return BitboardsInitialisationSection2.bitwiseOrAllSetsInPowerset(longMap(BoardSquare::asBitboard, relevantSquares));
	}

	static long[] generateRookOccupancyMasks()
	{
		return IterRange.to(64).mapToLong(i -> last(BitboardsImpl.ROOK_OCCUPANCY_VARIATIONS[i])).toArray();
	}

	static long[] generateBishopOccupancyMasks()
	{
		return IterRange.to(64).mapToLong(i -> last(BitboardsImpl.BISHOP_OCCUPANCY_VARIATIONS[i])).toArray();
	}

	static int[] generateRookMagicBitshifts()
	{
		return Iterate.overLongs(BitboardsImpl.ROOK_OCCUPANCY_MASKS).mapToInt(x -> 64 - Long.bitCount(x)).toArray();
	}

	static int[] generateBishopMagicBitshifts()
	{
		return Iterate.overLongs(BitboardsImpl.BISHOP_OCCUPANCY_MASKS).mapToInt(x -> 64 - Long.bitCount(x)).toArray();
	}

	/**
	 * Recursive method to calculate and return all possible bitboards arising from
	 * performing bitwise | operation on each element of each subset of the powerset
	 * of the given array. The size of the returned array is 2^(array.length).
	 */
	static long[] bitwiseOrAllSetsInPowerset(long[] array)
	{
		int length = array.length;
		if (length == 0) {
			return new long[0];
		}
		else if (length == 1) {
			return new long[] { 0L, array[0] };
		}
		else {
			long[] ans = new long[(int) Math.pow(2.0, length)];
			long[] recursiveAns = bitwiseOrAllSetsInPowerset(take(length - 1, array));
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
}
