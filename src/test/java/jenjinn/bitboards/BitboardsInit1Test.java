/**
 *
 */
package jenjinn.bitboards;

import static jenjinn.base.Square.A1;
import static jenjinn.base.Square.A2;
import static jenjinn.base.Square.A3;
import static jenjinn.base.Square.A4;
import static jenjinn.base.Square.A5;
import static jenjinn.base.Square.A6;
import static jenjinn.base.Square.A7;
import static jenjinn.base.Square.A8;
import static jenjinn.base.Square.B1;
import static jenjinn.base.Square.B3;
import static jenjinn.base.Square.B4;
import static jenjinn.base.Square.B5;
import static jenjinn.base.Square.B6;
import static jenjinn.base.Square.B7;
import static jenjinn.base.Square.C1;
import static jenjinn.base.Square.C4;
import static jenjinn.base.Square.C5;
import static jenjinn.base.Square.C6;
import static jenjinn.base.Square.D1;
import static jenjinn.base.Square.E1;
import static jenjinn.base.Square.E2;
import static jenjinn.base.Square.F1;
import static jenjinn.base.Square.F3;
import static jenjinn.base.Square.G1;
import static jenjinn.base.Square.G3;
import static jenjinn.base.Square.G6;
import static jenjinn.base.Square.H1;
import static jenjinn.base.Square.H2;
import static jenjinn.base.Square.H3;
import static jenjinn.base.Square.H4;
import static jenjinn.base.Square.H5;
import static jenjinn.base.Square.H6;
import static jenjinn.base.Square.H7;
import static jenjinn.base.Square.H8;
import static jenjinn.pieces.Piece.BLACK_BISHOP;
import static jenjinn.pieces.Piece.BLACK_KING;
import static jenjinn.pieces.Piece.BLACK_KNIGHT;
import static jenjinn.pieces.Piece.BLACK_PAWN;
import static jenjinn.pieces.Piece.BLACK_QUEEN;
import static jenjinn.pieces.Piece.BLACK_ROOK;
import static jenjinn.pieces.Piece.WHITE_BISHOP;
import static jenjinn.pieces.Piece.WHITE_KING;
import static jenjinn.pieces.Piece.WHITE_KNIGHT;
import static jenjinn.pieces.Piece.WHITE_PAWN;
import static jenjinn.pieces.Piece.WHITE_QUEEN;
import static jenjinn.pieces.Piece.WHITE_ROOK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jflow.iterators.factories.Iter;
import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.Dir;
import jenjinn.base.Square;
import jenjinn.pieces.Piece;
import jenjinn.pieces.PieceMovementDirs;

/**
 * @author ThomasB
 *
 */
class BitboardsInit1Test
{
	@Test
	void testSingleOccupancyBitboard()
	{
		Iter.until(64).forEach(i -> assertEquals(1L << i, Bitboards.square(i)));
	}

	@Test
	void testRankBitboard()
	{
		long[] expectedRanks = Vec.of(A1, A2, A3, A4, A5, A6, A7, A8).iter()
				.map(square -> square.getAllSquares(Dir.E, 8).insert(square))
				.mapToLong(Bitboard::fold).toArray();

		Iter.until(8).forEach(i -> assertEquals(expectedRanks[i], Bitboards.rank(i)));
	}

	@Test
	void testFileBitboard()
	{
		long[] expectedFiles = Vec.of(H1, G1, F1, E1, D1, C1, B1, A1).iter()
				.map(square -> square.getAllSquares(Dir.N, 8).insert(square))
				.mapToLong(Bitboard::fold).toArray();

		Iter.until(8).forEach(i -> assertEquals(expectedFiles[i], Bitboards.file(i)));
	}

	@Test
	void testDiagonalBitboard()
	{
		long[] expectedDiagonals = Vec
				.of(H1, G1, F1, E1, D1, C1, B1, A1, A2, A3, A4, A5, A6, A7, A8).iter()
				.map(square -> square.getAllSquares(Dir.NE, 8).insert(square))
				.mapToLong(Bitboard::fold).toArray();

		Iter.until(15)
				.forEach(i -> assertEquals(expectedDiagonals[i], Bitboards.diag(i)));
	}

	@Test
	void testAntiDiagonalBitboard()
	{
		long[] expectedDiagonals = Vec
				.of(A1, B1, C1, D1, E1, F1, G1, H1, H2, H3, H4, H5, H6, H7, H8).iter()
				.map(square -> square.getAllSquares(Dir.NW, 8).insert(square))
				.mapToLong(Bitboard::fold).toArray();

		Iter.until(15)
				.forEach(i -> assertEquals(expectedDiagonals[i], Bitboards.adiag(i)));
	}

	@ParameterizedTest
	@MethodSource
	void testEmptyBoardMovesetBitboard(Piece piece, Square location,
			Vec<Square> expectedMoveLocations)
	{
		assertEquals(Bitboard.fold(expectedMoveLocations),
				Bitboards.emptyBoardMoveset(piece, location));
	}

	static Stream<Arguments> testEmptyBoardMovesetBitboard()
	{
		return Stream.of(Arguments.of(WHITE_PAWN, A2, Vec.of(A3, A4)),
				Arguments.of(WHITE_PAWN, B3, Vec.of(B4)),
				Arguments.of(WHITE_KNIGHT, C5,
						C5.getAllSquares(PieceMovementDirs.KNIGHT, 1)),
				Arguments.of(WHITE_BISHOP, F3,
						F3.getAllSquares(PieceMovementDirs.BISHOP, 8)),
				Arguments.of(WHITE_ROOK, B3, B3.getAllSquares(PieceMovementDirs.ROOK, 8)),
				Arguments.of(WHITE_QUEEN, H2,
						H2.getAllSquares(PieceMovementDirs.QUEEN, 8)),
				Arguments.of(WHITE_KING, E2, E2.getAllSquares(PieceMovementDirs.KING, 1)),

				Arguments.of(BLACK_PAWN, A2, Vec.of(A1)),
				Arguments.of(BLACK_PAWN, B7, Vec.of(B6, B5)),
				Arguments.of(BLACK_KNIGHT, C5,
						C5.getAllSquares(PieceMovementDirs.KNIGHT, 1)),
				Arguments.of(BLACK_BISHOP, F3,
						F3.getAllSquares(PieceMovementDirs.BISHOP, 8)),
				Arguments.of(BLACK_ROOK, B3, B3.getAllSquares(PieceMovementDirs.ROOK, 8)),
				Arguments.of(BLACK_QUEEN, H2,
						H2.getAllSquares(PieceMovementDirs.QUEEN, 8)),
				Arguments.of(BLACK_KING, E2,
						E2.getAllSquares(PieceMovementDirs.KING, 1)));
	}

	@ParameterizedTest
	@MethodSource
	void testEmptyBoardAttacksetBitboard(Piece piece, Square location,
			Vec<Square> expectedMoveLocations)
	{
		assertEquals(Bitboard.fold(expectedMoveLocations),
				Bitboards.emptyBoardAttackset(piece, location));
	}

	static Stream<Arguments> testEmptyBoardAttacksetBitboard()
	{
		return Stream.of(Arguments.of(WHITE_PAWN, A2, Vec.of(B3)),
				Arguments.of(WHITE_PAWN, B3, Vec.of(C4, A4)),
				Arguments.of(WHITE_PAWN, H5, Vec.of(G6)),
				Arguments.of(WHITE_KNIGHT, C5,
						C5.getAllSquares(PieceMovementDirs.KNIGHT, 1)),
				Arguments.of(WHITE_BISHOP, F3,
						F3.getAllSquares(PieceMovementDirs.BISHOP, 8)),
				Arguments.of(WHITE_ROOK, B3, B3.getAllSquares(PieceMovementDirs.ROOK, 8)),
				Arguments.of(WHITE_QUEEN, H2,
						H2.getAllSquares(PieceMovementDirs.QUEEN, 8)),
				Arguments.of(WHITE_KING, E2, E2.getAllSquares(PieceMovementDirs.KING, 1)),

				Arguments.of(BLACK_PAWN, A2, Vec.of(B1)),
				Arguments.of(BLACK_PAWN, B7, Vec.of(C6, A6)),
				Arguments.of(BLACK_PAWN, H4, Vec.of(G3)),
				Arguments.of(BLACK_KNIGHT, C5,
						C5.getAllSquares(PieceMovementDirs.KNIGHT, 1)),
				Arguments.of(BLACK_BISHOP, F3,
						F3.getAllSquares(PieceMovementDirs.BISHOP, 8)),
				Arguments.of(BLACK_ROOK, B3, B3.getAllSquares(PieceMovementDirs.ROOK, 8)),
				Arguments.of(BLACK_QUEEN, H2,
						H2.getAllSquares(PieceMovementDirs.QUEEN, 8)),
				Arguments.of(BLACK_KING, E2,
						E2.getAllSquares(PieceMovementDirs.KING, 1)));
	}
}
