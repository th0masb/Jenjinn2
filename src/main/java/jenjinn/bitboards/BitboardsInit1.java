package jenjinn.bitboards;

import static jenjinn.bitboards.Bitboards.singleOccupancyBitboard;

import jenjinn.base.Dir;
import jenjinn.base.Square;
import jenjinn.pieces.PieceMovementDirs;
import jflow.iterators.factories.IterRange;
import jflow.seq.Seq;

/**
 * @author Tom
 * @date 18 April 2018
 */
final class BitboardsInit1
{
	static long[] generateSingleOccupancyBitboards()
	{
		return IterRange.to(64)
				.mapToLong(i -> 1L << i)
				.toArray();
	}

	static long[] generateRankBitboards()
	{
		return IterRange.to(8)
				.mapToLong(i -> 0b11111111L << (8*i))
				.toArray();
	}

	static long[] generateFileBitboards()
	{
		return IterRange.to(8)
				.mapToLong(i -> IterRange.to(8).mapToLong(j -> (1L << i) << (8*j)).fold(0L, (a, b) -> a | b))
				.toArray();
	}

	static long[] generateDiagonalBitboards()
	{
		return IterRange.to(15)
				.map(i -> i < 8 ? i : 8*(i - 7) + 7)
				.mapToObject(Square::of)
				.map(square -> square.getAllSquares(Dir.NE, 8).insert(square))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	static long[] generateAntidiagonalBitboards()
	{
		return IterRange.to(15)
				.map(i -> i < 8 ? 7 - i : 8*(i - 7))
				.mapToObject(Square::of)
				.map(square -> square.getAllSquares(Dir.NW, 8).insert(square))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

	static long[][] generateAllEmptyBoardPieceMovementBitboards()
	{
		return new long[][] {
			generateWhitePawnMovementBitboards(),
			generateBlackPawnMovementBitboards(),
			generateEmptyBoardBitboards(PieceMovementDirs.KNIGHT, 1),
			generateEmptyBoardBitboards(PieceMovementDirs.BISHOP, 8),
			generateEmptyBoardBitboards(PieceMovementDirs.ROOK, 8),
			generateEmptyBoardBitboards(PieceMovementDirs.QUEEN, 8),
			generateEmptyBoardBitboards(PieceMovementDirs.KING, 1)
		};
	}

	static long[][] generateAllEmptyBoardPieceAttackBitboards()
	{
		return new long[][] {
			generateEmptyBoardBitboards(PieceMovementDirs.WHITE_PAWN_ATTACK, 1),
			generateEmptyBoardBitboards(PieceMovementDirs.BLACK_PAWN_ATTACK, 1),
			generateEmptyBoardBitboards(PieceMovementDirs.KNIGHT, 1),
			generateEmptyBoardBitboards(PieceMovementDirs.BISHOP, 8),
			generateEmptyBoardBitboards(PieceMovementDirs.ROOK, 8),
			generateEmptyBoardBitboards(PieceMovementDirs.QUEEN, 8),
			generateEmptyBoardBitboards(PieceMovementDirs.KING, 1)
		};
	}

	private static long[] generateWhitePawnMovementBitboards()
	{
		long[] moves = generateEmptyBoardBitboards(PieceMovementDirs.WHITE_PAWN_MOVE, 1);
		IterRange.between(8, 16).forEach(i -> moves[i] |= singleOccupancyBitboard(i + 16));
		return moves;
	}

	private static long[] generateBlackPawnMovementBitboards()
	{
		long[] moves = generateEmptyBoardBitboards(PieceMovementDirs.BLACK_PAWN_MOVE, 1);
		IterRange.between(48, 56).forEach(i -> moves[i] |= singleOccupancyBitboard(i - 16));
		return moves;
	}

	private static long[] generateEmptyBoardBitboards(Seq<Dir> moveDirections, int lengthCap)
	{
		return Square.ALL.flow()
				.map(square -> square.getAllSquares(moveDirections, lengthCap))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
	}

}
