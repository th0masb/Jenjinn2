package com.github.maumay.jenjinn.bitboards;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.github.maumay.jenjinn.base.Dir;
import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.pieces.PieceMovementDirs;
import com.github.maumay.jflow.iterators.Iter;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 * @date 18/04/18
 */
final class BitboardsInit3
{
	static long[][] generateRookMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(BitboardsImpl.ROOK_OCCUPANCY_VARIATIONS,
				BitboardsImpl.ROOK_MAGIC_NUMBERS, BitboardsImpl.ROOK_MAGIC_BITSHIFTS,
				PieceMovementDirs.ROOK);
	}

	static long[][] generateBishopMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(BitboardsImpl.BISHOP_OCCUPANCY_VARIATIONS,
				BitboardsImpl.BISHOP_MAGIC_NUMBERS, BitboardsImpl.BISHOP_MAGIC_BITSHIFTS,
				PieceMovementDirs.BISHOP);
	}

	static long[][] generateMagicMoveDatabase(long[][] occupancyVariations,
			long[] magicNumbers, int[] magicBitshifts, Vec<Dir> moveDirs)
	{
		long[][] magicMoveDatabase = new long[64][];
		for (byte i = 0; i < 64; i++) {
			long[] singleSquareOccupancyVariations = occupancyVariations[i];
			long magicNumber = magicNumbers[i];
			int bitShift = magicBitshifts[i];
			long[] singleSquareDatabase = new long[singleSquareOccupancyVariations.length];

			for (long occVar : singleSquareOccupancyVariations) {
				int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
				singleSquareDatabase[magicIndex] = findControlSetFromOccupancyVariation(
						Square.of(i), occVar, moveDirs);
			}
			magicMoveDatabase[i] = singleSquareDatabase;
		}
		return magicMoveDatabase;
	}

	static long findControlSetFromOccupancyVariation(Square startSq, long occVar,
			Vec<Dir> movementDirections)
	{
		return movementDirections.iter()
				.map(direction -> startSq.getAllSquares(direction, 8).toList())
				.map(squares -> takeUntil(sq -> Bitboard.intersects(occVar, sq.bitboard),
						squares))
				.flatMap(Iter::over).collect(Bitboard::fold);
	}

	/**
	 * Copies all elements of the input List in order up to and including the first
	 * element for which the predicate fails to be true (or the whole list if the
	 * predicate is true for all elements).
	 */
	static <E> List<E> takeUntil(Predicate<? super E> stopCondition, List<? extends E> xs)
	{
		List<E> taken = new ArrayList<>();
		for (E x : xs) {
			taken.add(x);
			if (stopCondition.test(x)) {
				break;
			}
		}
		return taken;
	}
}
