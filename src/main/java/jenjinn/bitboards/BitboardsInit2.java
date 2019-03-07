package jenjinn.bitboards;

import static java.lang.Math.max;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import jenjinn.base.Dir;
import jenjinn.base.Square;
import jenjinn.pieces.PieceMovementDirs;
import jflow.iterators.factories.Iter;
import jflow.iterators.factories.IterRange;
import jflow.iterators.misc.ArrayUtils;
import jflow.seq.Seq;

/**
 * Second of three utility classes containing only static methods to initialise
 * the constants in the BBDB class. This class initialises everything we need to
 * generate the magic move databases for sliding pieces (and for the pawn first
 * moves).
 *
 * @author TB
 * @date 23 Jan 2017
 */
final class BitboardsInit2
{
	static long[][] generateAllBishopOccupancyVariations()
	{
		return Square.ALL.flow()
				.map(square -> calculateOccupancyVariations(square, PieceMovementDirs.BISHOP))
				.toList()
				.toArray(new long[64][]);
	}

	static long[][] generateAllRookOccupancyVariations()
	{
		return Square.ALL.flow()
				.map(square -> calculateOccupancyVariations(square, PieceMovementDirs.ROOK))
				.toList()
				.toArray(new long[64][]);
	}

	static long[] calculateOccupancyVariations(Square startSq, Seq<Dir> movementDirections)
	{
		List<Square> relevantSquares = new ArrayList<>();
		for (Dir dir : movementDirections) {
			int numOfSqsLeft = startSq.getNumberOfSquaresLeft(dir);
			relevantSquares.addAll(startSq.getAllSquares(asList(dir), max(numOfSqsLeft - 1, 0)).toList());
		}
		return foldedPowerset(ArrayUtils.longMap(s -> s.bitboard, relevantSquares));
	}
	
	static long[] foldedPowerset(long[] src)
	{
		if (src.length == 0) 
			return new long[] {0L};
		else {
			long head = src[0];
			long[] recursed = foldedPowerset(ArrayUtils.drop(1, src));
			return Iter.overLongs(recursed)
					.append(Iter.overLongs(recursed).map(x -> x | head))
					.toArray();
		}
	}

	static long[] generateRookOccupancyMasks()
	{
		long[][] rov = generateAllRookOccupancyVariations();
		return IterRange.to(64).mapToLong(i -> rov[i][rov[i].length - 1]).toArray();
	}

	static long[] generateBishopOccupancyMasks()
	{
		long[][] bov = generateAllBishopOccupancyVariations();
		return IterRange.to(64).mapToLong(i -> bov[i][bov[i].length - 1]).toArray();
	}

	static int[] generateRookMagicBitshifts()
	{
		return Iter.overLongs(generateRookOccupancyMasks())
				.mapToInt(x -> 64 - Long.bitCount(x))
				.toArray();
	}

	static int[] generateBishopMagicBitshifts()
	{
		return Iter.overLongs(generateBishopOccupancyMasks())
				.mapToInt(x -> 64 - Long.bitCount(x))
				.toArray();
	}
}
