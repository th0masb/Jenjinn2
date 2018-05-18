package jenjinn.engine.bitboards;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.misc.PieceMovementDirections;
import xawd.jflow.iterators.construction.Iterate;

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

	static long[][] generateMagicMoveDatabase(final long[][] occupancyVariations, final long[] magicNumbers, final int[] magicBitshifts, final List<Direction> movementDirections)
	{
		final long[][] magicMoveDatabase = new long[64][];
		for (byte i = 0; i < 64; i++) {
			final long[] singleSquareOccupancyVariations = occupancyVariations[i];
			final long magicNumber = magicNumbers[i];
			final int bitShift = magicBitshifts[i];
			final long[] singleSquareMagicMoveDatabase = new long[singleSquareOccupancyVariations.length];

			for (final long occVar : singleSquareOccupancyVariations) {
				final int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
				singleSquareMagicMoveDatabase[magicIndex] = findControlSetFromOccupancyVariation(BoardSquare.fromIndex(i), occVar, movementDirections);
			}
			magicMoveDatabase[i] = singleSquareMagicMoveDatabase;
		}
		return magicMoveDatabase;
	}

	static long findControlSetFromOccupancyVariation(final BoardSquare startSq, final long occVar, final List<Direction> movementDirections)
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
	static <E> List<E> takeUntil(final Predicate<? super E> stopCondition, final List<? extends E> xs)
	{
		final List<E> taken = new ArrayList<>();
		for (final E x : xs) {
			taken.add(x);
			if (stopCondition.test(x)) {
				break;
			}
		}
		return taken;
	}
}
