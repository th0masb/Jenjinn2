package jenjinn.engine.bitboarddatabase;

import static io.xyz.chains.utilities.CollectionUtil.insert;
import static io.xyz.chains.utilities.RangeUtil.range;
import static java.util.stream.Collectors.toCollection;
import static jenjinn.engine.bitboarddatabase.Bitboards.singleOccupancyBitboard;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.misc.BitboardUtils;
import jenjinn.engine.misc.PieceMovementDirections;

/**
 * First of three utility classes containing only static methods to initialise
 * the constants in the BBDB class. We generate the basic building blocks and
 * then the move and attack sets of all piece types on an empty board.
 *
 * @author TB
 * @date 21 Jan 2017
 */
public class BitboardsInitialisationSection1
{
	public static long[] generateSingleOccupancyBitboards()
	{
		return range(64)
				.stream()
				.mapToLong(i -> 1L << i)
				.toArray();
	}

	public static long[] generateRankBitboards()
	{
		return range(0, 64, 8)
				.stream()
				.mapToObj(BoardSquare::fromIndex)
				.map(square -> insert(square, square.getAllSquaresInDirection(Direction.W)))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	public static long[] generateFileBitboards()
	{
		return range(8)
				.stream()
				.mapToObj(BoardSquare::fromIndex)
				.map(square -> insert(square, square.getAllSquaresInDirection(Direction.N)))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	public static long[] generateDiagonalBitboards()
	{
		return range(15).stream()
				.map(i -> i < 8 ? i : 8*(i - 7) + 7)
				.mapToObj(BoardSquare::fromIndex)
				.map(square -> insert(square, square.getAllSquaresInDirection(Direction.NE)))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	public static long[] generateAntidiagonalBitboards()
	{
		return range(15).stream()
				.map(i -> i < 8 ? 7 - i : 8*(i - 7))
				.mapToObj(BoardSquare::fromIndex)
				.map(square -> insert(square, square.getAllSquaresInDirection(Direction.NW)))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	public static long[][] generateAllEmptyBoardPieceMovementBitboards()
	{
		return new long[][] {
			generateWhitePawnMovementBitboards(),
			generateBlackPawnMovementBitboards(),
			generateEmptyBoardBitboards(PieceMovementDirections.ND, 1),
			generateEmptyBoardBitboards(PieceMovementDirections.BD, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.RD, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.QD, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.KD, 1)
		};
	}
	
	private static long[] generateWhitePawnMovementBitboards()
	{
		final long[] moves = generateEmptyBoardBitboards(PieceMovementDirections.WPM, 1);
		range(8, 16).stream().forEach(i -> moves[i] |= singleOccupancyBitboard(i + 8));
		return moves;
	}

	private static long[] generateBlackPawnMovementBitboards()
	{
		final long[] moves = generateEmptyBoardBitboards(PieceMovementDirections.BPM, 1);
		range(48, 56).stream().forEach(i -> moves[i] |= singleOccupancyBitboard(i - 8));
		return moves;
	}
	
	private static long[] generateEmptyBoardBitboards(final List<Direction> moveDirections, final int lengthCap)
	{
		return range(64).stream()
				.mapToObj(BoardSquare::fromIndex)
				.map(square -> getSquaresFromSourceSquare(square, moveDirections, lengthCap))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	private static List<BoardSquare> getSquaresFromSourceSquare(final BoardSquare src, final List<Direction> directions, final int lengthCap)
	{
		return directions
				.stream()
				.map(dir -> src.getAllSquaresInDirection(dir, lengthCap))
				.flatMap(List::stream)
				.collect(toCollection(ArrayList::new));
	}

	public static long[][] generateAllEmptyBoardPieceAttackBitboards()
	{
		return new long[][] {
			generateEmptyBoardBitboards(PieceMovementDirections.WPA, 1),
			generateEmptyBoardBitboards(PieceMovementDirections.BPA, 1),
			generateEmptyBoardBitboards(PieceMovementDirections.ND, 1),
			generateEmptyBoardBitboards(PieceMovementDirections.BD, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.RD, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.QD, 8),
			generateEmptyBoardBitboards(PieceMovementDirections.KD, 1)
		};
	}
}
