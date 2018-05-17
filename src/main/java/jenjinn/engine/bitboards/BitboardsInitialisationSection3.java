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
public class BitboardsInitialisationSection3
{
	public static long[][] generateRookMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(
				BitboardsImpl.ROOK_OCCUPANCY_VARIATIONS,
				BitboardsImpl.ROOK_MAGIC_NUMBERS,
				BitboardsImpl.ROOK_MAGIC_BITSHIFTS,
				PieceMovementDirections.ROOK);
	}

	public static long[][] generateBishopMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(
				BitboardsImpl.BISHOP_OCCUPANCY_VARIATIONS,
				BitboardsImpl.BISHOP_MAGIC_NUMBERS,
				BitboardsImpl.BISHOP_MAGIC_BITSHIFTS,
				PieceMovementDirections.BISHOP);
	}

	private static long[][] generateMagicMoveDatabase(final long[][] occupancyVariations, final long[] magicNumbers, final int[] magicBitshifts, final List<Direction> movementDirections)
	{
		final long[][] mmDatabase = new long[64][];
		for (byte i = 0; i < 64; i++) {
			final long[] singleSquareOccupancyVariations = occupancyVariations[i];
			final long magicNumber = magicNumbers[i];
			final int bitShift = magicBitshifts[i];
			final long[] singleSquareMagicMoveDatabase = new long[singleSquareOccupancyVariations.length];

			for (final long occVar : singleSquareOccupancyVariations) {
				final int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
				singleSquareMagicMoveDatabase[magicIndex] = findAttackSetFromOccupancyVariation(BoardSquare.fromIndex(i), occVar, movementDirections);
			}
			mmDatabase[i] = singleSquareMagicMoveDatabase;
		}
		return mmDatabase;
	}

	private static long findAttackSetFromOccupancyVariation(final BoardSquare startSq, final long occVar, final List<Direction> movementDirections)
	{
		return bitwiseOr(Iterate.over(movementDirections)
				.map(direction -> startSq.getAllSquaresInDirections(movementDirections, 8))
				.map(squares -> takeUntil(square -> bitboardsIntersect(occVar, square.asBitboard()), squares))
				.flatten(Iterate::over));
	}

	static <T> List<T> takeUntil(final Predicate<T> stopCondition, final Iterable<T> xs)
	{
		final List<T> taken = new ArrayList<>();
		for (final T x : xs) {
			taken.add(x);
			if (stopCondition.test(x)) {
				break;
			}
		}
		return taken;
	}
}
