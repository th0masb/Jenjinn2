package jenjinn.engine.bitboards;

import static io.xyz.chains.utilities.CollectionUtil.insert;
import static io.xyz.chains.utilities.MapUtil.longMap;
import static io.xyz.chains.utilities.RangeUtil.range;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;
import static jenjinn.engine.bitboards.Bitboards.singleOccupancyBitboard;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.misc.PieceMovementDirections;

/**
 * @author Tom
 * @date 18 April 2018
 */
public class BitboardsInitialisationSection1
{
	public static long[] generateSingleOccupancyBitboards()
	{
		return range(64).stream()
				.mapToLong(i -> 1L << i)
				.toArray();
	}

	public static long[] generateRankBitboards()
	{
		return range(8).stream()
				.mapToLong(i -> 0b11111111L << (8*i))
				.toArray();
	}

	public static long[] generateFileBitboards()
	{
		return range(8).stream()
				.mapToLong(i -> bitwiseOr(longMap(j -> (1L << i) << (8*j), range(8))))
				.toArray();
	}

	public static long[] generateDiagonalBitboards()
	{
		return range(15).stream()
				.map(i -> i < 8 ? i : 8*(i - 7) + 7)
				.mapToObj(BoardSquare::fromIndex)
				.map(square -> insert(square, square.getAllSquaresInDirections(Direction.NE)))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	public static long[] generateAntidiagonalBitboards()
	{
		return range(15).stream()
				.map(i -> i < 8 ? 7 - i : 8*(i - 7))
				.mapToObj(BoardSquare::fromIndex)
				.map(square -> insert(square, square.getAllSquaresInDirections(Direction.NW)))
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
		range(8, 16).stream().forEach(i -> moves[i] |= singleOccupancyBitboard(i + 16));
		return moves;
	}

	private static long[] generateBlackPawnMovementBitboards()
	{
		final long[] moves = generateEmptyBoardBitboards(PieceMovementDirections.BLACK_PAWN_MOVE, 1);
		range(48, 56).stream().forEach(i -> moves[i] |= singleOccupancyBitboard(i - 16));
		return moves;
	}
	
	private static long[] generateEmptyBoardBitboards(final List<Direction> moveDirections, final int lengthCap)
	{
		return range(64).stream()
				.mapToObj(BoardSquare::fromIndex)
				.map(square -> square.getAllSquaresInDirections(moveDirections, lengthCap))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

}
