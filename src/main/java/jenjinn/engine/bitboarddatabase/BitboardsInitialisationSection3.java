package jenjinn.engine.bitboarddatabase;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.misc.PieceMovementDirectionArrays;

/**
 * @author TB
 * @date 24 Jan 2017
 */
public class BitboardsInitialisationSection3
{
	public static long[][] generateRookMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(true);
	}

	public static long[][] generateBishopMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(false);
	}

	private static long[][] generateMagicMoveDatabase(final boolean isRook)
	{
		final long[][] mmDatabase = new long[64][];
		final long[][] allSquaresOccupancyVariations = isRook ? Bitboards.ROV : Bitboards.BOV;

		for (byte i = 0; i < 64; i++) {
			final long[] singleSquaresOccupancyVariations = allSquaresOccupancyVariations[i];
			final long magicNumber = isRook ? Bitboards.RMN[i] : Bitboards.BMN[i];
			final byte bitShift = isRook ? Bitboards.RMB[i] : Bitboards.BMB[i];
			final long[] singleSquareMmDatabase = new long[singleSquaresOccupancyVariations.length];

			for (final long occVar : singleSquaresOccupancyVariations) {
				final int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
				singleSquareMmDatabase[magicIndex] = findAttackSetFromOccupancyVariation(BoardSquare.fromIndex(i), occVar, isRook);
			}
			mmDatabase[i] = singleSquareMmDatabase;
		}

		return mmDatabase;
	}

	private static long findAttackSetFromOccupancyVariation(final BoardSquare startSq, final long occVar, final boolean isRook)
	{
		final List<BoardSquare> attackSquares = new ArrayList<>();
		final Direction[] movementDirections = isRook ? PieceMovementDirectionArrays.RD : PieceMovementDirectionArrays.BD;

		for (final Direction dir : movementDirections) {
			BoardSquare nextSq = startSq;

			while (nextSq != null) {
				nextSq = nextSq.getNextSquareInDirection(dir);
				final long nextSqAsBB = nextSq == null ? 0L : nextSq.asBitboard();
				final boolean blocked = (nextSqAsBB & occVar) != 0;

				if (nextSq != null) {
					attackSquares.add(nextSq);
				}
				if (blocked) {
					break;
				}
			}
		}
		return EngineUtils.bitwiseOr(attackSquares.toArray(new BoardSquare[0]));
	}
}
