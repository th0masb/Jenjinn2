package jenjinn.engine.bitboards;

import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;
import static jenjinn.engine.bitboards.Bitboards.singleOccupancyBitboard;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.misc.PieceMovementDirections;
import xawd.jflow.iterators.construction.IterRange;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author Tom
 * @date 18 April 2018
 */
public class BitboardsInitialisationSection1
{
	public static long[] generateSingleOccupancyBitboards()
	{
		return IterRange.to(64)
				.mapToLong(i -> 1L << i)
				.toArray();
	}

	public static long[] generateRankBitboards()
	{
		return IterRange.to(8)
				.mapToLong(i -> 0b11111111L << (8*i))
				.toArray();
	}

	public static long[] generateFileBitboards()
	{
		return IterRange.to(8)
				.mapToLong(i -> bitwiseOr(IterRange.to(8).mapToLong(j -> (1L << i) << (8*j))))
				.toArray();
	}

	public static long[] generateDiagonalBitboards()
	{
		return IterRange.to(15)
				.map(i -> i < 8 ? i : 8*(i - 7) + 7)
				.mapToObject(BoardSquare::fromIndex)
				.map(square -> Iterate.over(square.getAllSquaresInDirections(Direction.NE, 8)).insert(square))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	public static long[] generateAntidiagonalBitboards()
	{
		return IterRange.to(15)
				.map(i -> i < 8 ? 7 - i : 8*(i - 7))
				.mapToObject(BoardSquare::fromIndex)
				.map(square -> Iterate.over(square.getAllSquaresInDirections(Direction.NW, 8)).insert(square))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	public static long[][] generateAllEmptyBoardPieceMovementBitboards()
	{
		return new long[][] {
			generateWhitePawnMovementBitboards(),
			generateBlackPawnMovementBitboards(),
			generateEmptyBoardBitboards(PieceMovementDirections.KNIGHT, 1),
			generateEmptyBoardBitboards(PieceMovementDirections.BISHOP, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.ROOK, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.QUEEN, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.KING, 1)
		};
	}

	public static long[][] generateAllEmptyBoardPieceAttackBitboards()
	{
		return new long[][] {
			generateEmptyBoardBitboards(PieceMovementDirections.WHITE_PAWN_ATTACK, 1),
			generateEmptyBoardBitboards(PieceMovementDirections.BLACK_PAWN_ATTACK, 1),
			generateEmptyBoardBitboards(PieceMovementDirections.KNIGHT, 1),
			generateEmptyBoardBitboards(PieceMovementDirections.BISHOP, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.ROOK, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.QUEEN, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.KING, 1)
		};
	}

	private static long[] generateWhitePawnMovementBitboards()
	{
		final long[] moves = generateEmptyBoardBitboards(PieceMovementDirections.WHITE_PAWN_MOVE, 1);
		IterRange.between(8, 16).forEach(i -> moves[i] |= singleOccupancyBitboard(i + 16));
		return moves;
	}

	private static long[] generateBlackPawnMovementBitboards()
	{
		final long[] moves = generateEmptyBoardBitboards(PieceMovementDirections.BLACK_PAWN_MOVE, 1);
		IterRange.between(48, 56).forEach(i -> moves[i] |= singleOccupancyBitboard(i - 16));
		return moves;
	}

	private static long[] generateEmptyBoardBitboards(final List<Direction> moveDirections, final int lengthCap)
	{
		return BoardSquare.iterateAll()
				.map(square -> square.getAllSquaresInDirections(moveDirections, lengthCap))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

}
