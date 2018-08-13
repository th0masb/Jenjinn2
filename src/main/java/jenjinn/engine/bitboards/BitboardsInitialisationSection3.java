package jenjinn.engine.bitboards;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Direction;
import jenjinn.engine.pieces.PieceMovementDirections;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 * @date 18/04/18
 */
final class BitboardsInitialisationSection3
{
	static long[][] generateRookMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(
				BitboardsImpl.ROOK_OCCUPANCY_VARIATIONS,
				BitboardsImpl.ROOK_MAGIC_NUMBERS,
				BitboardsImpl.ROOK_MAGIC_BITSHIFTS,
				PieceMovementDirections.ROOK);
	}

	static long[][] generateBishopMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(
				BitboardsImpl.BISHOP_OCCUPANCY_VARIATIONS,
				BitboardsImpl.BISHOP_MAGIC_NUMBERS,
				BitboardsImpl.BISHOP_MAGIC_BITSHIFTS,
				PieceMovementDirections.BISHOP);
	}

	static long[][] generateMagicMoveDatabase(long[][] occupancyVariations, long[] magicNumbers, int[] magicBitshifts, List<Direction> movementDirections)
	{
		long[][] magicMoveDatabase = new long[64][];
		for (byte i = 0; i < 64; i++) {
			long[] singleSquareOccupancyVariations = occupancyVariations[i];
			long magicNumber = magicNumbers[i];
			int bitShift = magicBitshifts[i];
			long[] singleSquareMagicMoveDatabase = new long[singleSquareOccupancyVariations.length];

			for (long occVar : singleSquareOccupancyVariations) {
				int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
				singleSquareMagicMoveDatabase[magicIndex] = findControlSetFromOccupancyVariation(BoardSquare.of(i), occVar, movementDirections);
			}
			magicMoveDatabase[i] = singleSquareMagicMoveDatabase;
		}
		return magicMoveDatabase;
	}

	static long findControlSetFromOccupancyVariation(BoardSquare startSq, long occVar, List<Direction> movementDirections)
	{
		return bitwiseOr(Iterate.over(movementDirections)
				.map(direction -> startSq.getAllSquaresInDirections(direction, 8))
				.map(squares -> BitboardsInitialisationSection3.takeUntil(square -> bitboardsIntersect(occVar, square.asBitboard()), squares))
				.flatten(Iterate::over));
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
