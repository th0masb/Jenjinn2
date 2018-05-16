package jenjinn.engine.bitboards;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.misc.PieceMovementDirections;
import xawd.jflow.construction.Iter;

/**
 * @author ThomasB
 * @date 18/04/18
 */
public class BitboardsInitialisationSection3
{
	public static long[][] generateRookMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(
				Bitboards.ROOK_OCCUPANCY_VARIATIONS,
				Bitboards.ROOK_MAGIC_NUMBERS,
				Bitboards.ROOK_MAGIC_BITSHIFTS,
				PieceMovementDirections.ROOK);
	}

	public static long[][] generateBishopMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(
				Bitboards.BISHOP_OCCUPANCY_VARIATIONS,
				Bitboards.BISHOP_MAGIC_NUMBERS,
				Bitboards.BISHOP_MAGIC_BITSHIFTS,
				PieceMovementDirections.BISHOP);
	}

	private static long[][] generateMagicMoveDatabase(final long[][] occupancyVariations, final long[] magicNumbers, final int[] magicBitshifts, final List<Direction> movementDirections)
	{
		final long[][] mmDatabase = new long[64][];
		for (byte i = 0; i < 64; i++) {
			final long[] singleSquareOccupancyVariations = occupancyVariations[i];
			final long magicNumber = magicNumbers[i];
			final int bitShift = magicBitshifts[i];
			final long[] singleSquareMmDatabase = new long[singleSquareOccupancyVariations.length];

			for (final long occVar : singleSquareOccupancyVariations) {
				final int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
				singleSquareMmDatabase[magicIndex] = findAttackSetFromOccupancyVariation(BoardSquare.fromIndex(i), occVar, movementDirections);
			}
			mmDatabase[i] = singleSquareMmDatabase;
		}
		return mmDatabase;
	}

	private static long findAttackSetFromOccupancyVariation(final BoardSquare startSq, final long occVar, final List<Direction> movementDirections)
	{
		return bitwiseOr(Iter.of(movementDirections)
				.map(direction -> startSq.getAllSquaresInDirections(movementDirections, 8))
				.map(squares -> takeUntil(square -> bitboardsIntersect(occVar, square.asBitboard()), squares))
				.flatten(Iter::of));
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
