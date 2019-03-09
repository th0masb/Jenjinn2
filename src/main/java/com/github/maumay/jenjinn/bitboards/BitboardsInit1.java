package com.github.maumay.jenjinn.bitboards;

import com.github.maumay.jenjinn.base.Dir;
import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.pieces.PieceMovementDirs;
import com.github.maumay.jflow.iterators.factories.Iter;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author Tom
 * @date 18 April 2018
 */
final class BitboardsInit1
{
	static long[] generateSingleOccupancyBitboards()
	{
		return Iter.until(64).mapToLong(i -> 1L << i).toArray();
	}

	static long[] generateRankBitboards()
	{
		return Iter.until(8).mapToLong(i -> 0b11111111L << (8 * i)).toArray();
	}

	static long[] generateFileBitboards()
	{
		return Iter
				.until(8).mapToLong(i -> Iter.until(8)
						.mapToLong(j -> (1L << i) << (8 * j)).fold(0L, (a, b) -> a | b))
				.toArray();
	}

	static long[] generateDiagonalBitboards()
	{
		return Iter.until(15).map(i -> i < 8 ? i : 8 * (i - 7) + 7)
				.mapToObject(Square::of)
				.map(square -> square.getAllSquares(Dir.NE, 8).insert(square))
				.mapToLong(Bitboard::fold).toArray();
	}

	static long[] generateAntidiagonalBitboards()
	{
		return Iter.until(15).map(i -> i < 8 ? 7 - i : 8 * (i - 7))
				.mapToObject(Square::of)
				.map(square -> square.getAllSquares(Dir.NW, 8).insert(square))
				.mapToLong(Bitboard::fold).toArray();
	}

	static long[][] generateAllEmptyBoardPieceMovementBitboards()
	{
		return new long[][] { generateWhitePawnMovementBitboards(),
				generateBlackPawnMovementBitboards(),
				generateEmptyBoardBitboards(PieceMovementDirs.KNIGHT, 1),
				generateEmptyBoardBitboards(PieceMovementDirs.BISHOP, 8),
				generateEmptyBoardBitboards(PieceMovementDirs.ROOK, 8),
				generateEmptyBoardBitboards(PieceMovementDirs.QUEEN, 8),
				generateEmptyBoardBitboards(PieceMovementDirs.KING, 1) };
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
				generateEmptyBoardBitboards(PieceMovementDirs.KING, 1) };
	}

	private static long[] generateWhitePawnMovementBitboards()
	{
		long[] moves = generateEmptyBoardBitboards(PieceMovementDirs.WHITE_PAWN_MOVE, 1);
		Iter.between(8, 16).forEach(i -> moves[i] |= Bitboards.square(i + 16));
		return moves;
	}

	private static long[] generateBlackPawnMovementBitboards()
	{
		long[] moves = generateEmptyBoardBitboards(PieceMovementDirs.BLACK_PAWN_MOVE, 1);
		Iter.between(48, 56).forEach(i -> moves[i] |= Bitboards.square(i - 16));
		return moves;
	}

	private static long[] generateEmptyBoardBitboards(Vec<Dir> moveDirections,
			int lengthCap)
	{
		return Square.ALL.iter()
				.map(square -> square.getAllSquares(moveDirections, lengthCap))
				.mapToLong(Bitboard::fold).toArray();
	}

}
