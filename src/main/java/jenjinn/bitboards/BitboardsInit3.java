package jenjinn.bitboards;

import static jenjinn.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.bitboards.BitboardUtils.bitwiseOr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jenjinn.base.Dir;
import jenjinn.base.Square;
import jenjinn.pieces.PieceMovementDirs;
import jflow.iterators.factories.Iter;
import jflow.seq.Seq;

/**
 * @author ThomasB
 * @date 18/04/18
 */
final class BitboardsInit3
{
	static long[][] generateRookMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(
				BitboardsImpl.ROOK_OCCUPANCY_VARIATIONS,
				BitboardsImpl.ROOK_MAGIC_NUMBERS,
				BitboardsImpl.ROOK_MAGIC_BITSHIFTS,
				PieceMovementDirs.ROOK);
	}

	static long[][] generateBishopMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(
				BitboardsImpl.BISHOP_OCCUPANCY_VARIATIONS,
				BitboardsImpl.BISHOP_MAGIC_NUMBERS,
				BitboardsImpl.BISHOP_MAGIC_BITSHIFTS,
				PieceMovementDirs.BISHOP);
	}

	static long[][] generateMagicMoveDatabase(long[][] occupancyVariations, long[] magicNumbers, 
			int[] magicBitshifts, Seq<Dir> moveDirs)
	{
		long[][] magicMoveDatabase = new long[64][];
		for (byte i = 0; i < 64; i++) {
			long[] singleSquareOccupancyVariations = occupancyVariations[i];
			long magicNumber = magicNumbers[i];
			int bitShift = magicBitshifts[i];
			long[] singleSquareDatabase = new long[singleSquareOccupancyVariations.length];

			for (long occVar : singleSquareOccupancyVariations) {
				int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
				singleSquareDatabase[magicIndex] = findControlSetFromOccupancyVariation(Square.of(i), occVar, moveDirs);
			}
			magicMoveDatabase[i] = singleSquareDatabase;
		}
		return magicMoveDatabase;
	}

	static long findControlSetFromOccupancyVariation(Square startSq, long occVar, Seq<Dir> movementDirections)
	{
		return bitwiseOr(movementDirections.flow()
				.map(direction -> startSq.getAllSquares(direction, 8).toList())
				.map(squares -> takeUntil(sq -> bitboardsIntersect(occVar, sq.bitboard), squares))
				.flatMap(Iter::over));
	}

	/**
	 * Copies all elements of the input List in order up to and including the first element for which
	 * the predicate fails to be true (or the whole list if the predicate is true for all elements).
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
