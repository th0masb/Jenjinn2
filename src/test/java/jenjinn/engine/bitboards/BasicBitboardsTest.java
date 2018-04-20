/**
 * 
 */
package jenjinn.engine.bitboards;

import static io.xyz.chains.utilities.CollectionUtil.asList;
import static io.xyz.chains.utilities.CollectionUtil.insert;
import static io.xyz.chains.utilities.RangeUtil.range;
import static jenjinn.engine.bitboards.Bitboards.antiDiagonalBitboard;
import static jenjinn.engine.bitboards.Bitboards.diagonalBitboard;
import static jenjinn.engine.bitboards.Bitboards.fileBitboard;
import static jenjinn.engine.bitboards.Bitboards.rankBitboard;
import static jenjinn.engine.bitboards.Bitboards.singleOccupancyBitboard;
import static jenjinn.engine.enums.BoardSquare.A1;
import static jenjinn.engine.enums.BoardSquare.A2;
import static jenjinn.engine.enums.BoardSquare.A3;
import static jenjinn.engine.enums.BoardSquare.A4;
import static jenjinn.engine.enums.BoardSquare.A5;
import static jenjinn.engine.enums.BoardSquare.A6;
import static jenjinn.engine.enums.BoardSquare.A7;
import static jenjinn.engine.enums.BoardSquare.A8;
import static jenjinn.engine.enums.BoardSquare.B1;
import static jenjinn.engine.enums.BoardSquare.B3;
import static jenjinn.engine.enums.BoardSquare.B4;
import static jenjinn.engine.enums.BoardSquare.B5;
import static jenjinn.engine.enums.BoardSquare.B6;
import static jenjinn.engine.enums.BoardSquare.B7;
import static jenjinn.engine.enums.BoardSquare.C1;
import static jenjinn.engine.enums.BoardSquare.C4;
import static jenjinn.engine.enums.BoardSquare.C5;
import static jenjinn.engine.enums.BoardSquare.C6;
import static jenjinn.engine.enums.BoardSquare.D1;
import static jenjinn.engine.enums.BoardSquare.E1;
import static jenjinn.engine.enums.BoardSquare.E2;
import static jenjinn.engine.enums.BoardSquare.F1;
import static jenjinn.engine.enums.BoardSquare.F3;
import static jenjinn.engine.enums.BoardSquare.G1;
import static jenjinn.engine.enums.BoardSquare.G3;
import static jenjinn.engine.enums.BoardSquare.G6;
import static jenjinn.engine.enums.BoardSquare.H1;
import static jenjinn.engine.enums.BoardSquare.H2;
import static jenjinn.engine.enums.BoardSquare.H3;
import static jenjinn.engine.enums.BoardSquare.H4;
import static jenjinn.engine.enums.BoardSquare.H5;
import static jenjinn.engine.enums.BoardSquare.H6;
import static jenjinn.engine.enums.BoardSquare.H7;
import static jenjinn.engine.enums.BoardSquare.H8;
import static jenjinn.engine.enums.ChessPiece.BLACK_BISHOP;
import static jenjinn.engine.enums.ChessPiece.BLACK_KING;
import static jenjinn.engine.enums.ChessPiece.BLACK_KNIGHT;
import static jenjinn.engine.enums.ChessPiece.BLACK_PAWN;
import static jenjinn.engine.enums.ChessPiece.BLACK_QUEEN;
import static jenjinn.engine.enums.ChessPiece.BLACK_ROOK;
import static jenjinn.engine.enums.ChessPiece.WHITE_BISHOP;
import static jenjinn.engine.enums.ChessPiece.WHITE_KING;
import static jenjinn.engine.enums.ChessPiece.WHITE_KNIGHT;
import static jenjinn.engine.enums.ChessPiece.WHITE_PAWN;
import static jenjinn.engine.enums.ChessPiece.WHITE_QUEEN;
import static jenjinn.engine.enums.ChessPiece.WHITE_ROOK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import jenjinn.engine.enums.Direction;
import jenjinn.engine.misc.PieceMovementDirections;

/**
 * @author ThomasB
 *
 */
class BasicBitboardsTest
{
	@Test
	void testSingleOccupancyBitboard() 
	{
		range(64).stream().forEach(i -> assertEquals(1L << i, singleOccupancyBitboard(i)));
	}

	@Test
	void testRankBitboard() 
	{
		final long[] expectedRanks = Stream.of(A1, A2, A3, A4, A5, A6, A7, A8)
				.map(square -> insert(square, square.getAllSquaresInDirections(Direction.E, 8)))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
		
		range(8).stream().forEach(i -> assertEquals(expectedRanks[i], rankBitboard(i)));
	}

	@Test
	void testFileBitboard() 
	{
		final long[] expectedFiles = Stream.of(H1, G1, F1, E1, D1, C1, B1, A1)
				.map(square -> insert(square, square.getAllSquaresInDirections(Direction.N, 8)))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
		
		range(8).stream().forEach(i -> assertEquals(expectedFiles[i], fileBitboard(i)));
	}

	@Test
	void testDiagonalBitboard() 
	{
		final long[] expectedDiagonals = Stream.of(H1, G1, F1, E1, D1, C1, B1, A1, A2, A3, A4, A5, A6, A7, A8)
				.map(square -> insert(square, square.getAllSquaresInDirections(Direction.NE, 8)))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
		
		range(15).stream().forEach(i -> assertEquals(expectedDiagonals[i], diagonalBitboard(i)));
	}

	@Test
	void testAntiDiagonalBitboard() 
	{
		final long[] expectedAntiDiagonals = Stream.of(A1, B1, C1, D1, E1, F1, G1, H1, H2, H3, H4, H5, H6, H7, H8)
				.map(square -> insert(square, square.getAllSquaresInDirections(Direction.NW, 8)))
				.mapToLong(BitboardUtils::bitwiseOr)
				.toArray();
		
		range(15).stream().forEach(i -> assertEquals(expectedAntiDiagonals[i], antiDiagonalBitboard(i)));
	}

	@Test
	void testEmptyBoardMovesetBitboard() 
	{
		final List<EmptyBoardMovementTestData> testCases = asList(
				new EmptyBoardMovementTestData(WHITE_PAWN, A2, asList(A3, A4)),
				new EmptyBoardMovementTestData(WHITE_PAWN, B3, asList(B4)),
				new EmptyBoardMovementTestData(WHITE_KNIGHT, C5, C5.getAllSquaresInDirections(PieceMovementDirections.KNIGHT, 1)),
				new EmptyBoardMovementTestData(WHITE_BISHOP, F3, F3.getAllSquaresInDirections(PieceMovementDirections.BISHOP, 8)),
				new EmptyBoardMovementTestData(WHITE_ROOK, B3, B3.getAllSquaresInDirections(PieceMovementDirections.ROOK, 8)),
				new EmptyBoardMovementTestData(WHITE_QUEEN, H2, H2.getAllSquaresInDirections(PieceMovementDirections.QUEEN, 8)),
				new EmptyBoardMovementTestData(WHITE_KING, E2, E2.getAllSquaresInDirections(PieceMovementDirections.KING, 1)),
				
				new EmptyBoardMovementTestData(BLACK_PAWN, A2, asList(A1)),
				new EmptyBoardMovementTestData(BLACK_PAWN, B7, asList(B6, B5)),
				new EmptyBoardMovementTestData(BLACK_KNIGHT, C5, C5.getAllSquaresInDirections(PieceMovementDirections.KNIGHT, 1)),
				new EmptyBoardMovementTestData(BLACK_BISHOP, F3, F3.getAllSquaresInDirections(PieceMovementDirections.BISHOP, 8)),
				new EmptyBoardMovementTestData(BLACK_ROOK, B3, B3.getAllSquaresInDirections(PieceMovementDirections.ROOK, 8)),
				new EmptyBoardMovementTestData(BLACK_QUEEN, H2, H2.getAllSquaresInDirections(PieceMovementDirections.QUEEN, 8)),
				new EmptyBoardMovementTestData(BLACK_KING, E2, E2.getAllSquaresInDirections(PieceMovementDirections.KING, 1))
				);
		
		testCases
		.stream()
		.forEach(testCase -> assertEquals(testCase.getExpectedMoveBitboard(), testCase.getActualMoveBitboard(), testCase.toString()));
	}

	@Test
	void testEmptyBoardAttacksetBitboard() 
	{
		final List<EmptyBoardAttackTestData> testCases = asList(
				new EmptyBoardAttackTestData(WHITE_PAWN, A2, asList(B3)),
				new EmptyBoardAttackTestData(WHITE_PAWN, B3, asList(C4, A4)),
				new EmptyBoardAttackTestData(WHITE_PAWN, H5, asList(G6)),
				new EmptyBoardAttackTestData(WHITE_KNIGHT, C5, C5.getAllSquaresInDirections(PieceMovementDirections.KNIGHT, 1)),
				new EmptyBoardAttackTestData(WHITE_BISHOP, F3, F3.getAllSquaresInDirections(PieceMovementDirections.BISHOP, 8)),
				new EmptyBoardAttackTestData(WHITE_ROOK, B3, B3.getAllSquaresInDirections(PieceMovementDirections.ROOK, 8)),
				new EmptyBoardAttackTestData(WHITE_QUEEN, H2, H2.getAllSquaresInDirections(PieceMovementDirections.QUEEN, 8)),
				new EmptyBoardAttackTestData(WHITE_KING, E2, E2.getAllSquaresInDirections(PieceMovementDirections.KING, 1)),
				
				new EmptyBoardAttackTestData(BLACK_PAWN, A2, asList(B1)),
				new EmptyBoardAttackTestData(BLACK_PAWN, B7, asList(C6, A6)),
				new EmptyBoardAttackTestData(BLACK_PAWN, H4, asList(G3)),
				new EmptyBoardAttackTestData(BLACK_KNIGHT, C5, C5.getAllSquaresInDirections(PieceMovementDirections.KNIGHT, 1)),
				new EmptyBoardAttackTestData(BLACK_BISHOP, F3, F3.getAllSquaresInDirections(PieceMovementDirections.BISHOP, 8)),
				new EmptyBoardAttackTestData(BLACK_ROOK, B3, B3.getAllSquaresInDirections(PieceMovementDirections.ROOK, 8)),
				new EmptyBoardAttackTestData(BLACK_QUEEN, H2, H2.getAllSquaresInDirections(PieceMovementDirections.QUEEN, 8)),
				new EmptyBoardAttackTestData(BLACK_KING, E2, E2.getAllSquaresInDirections(PieceMovementDirections.KING, 1))
				);
		
		testCases
		.stream()
		.forEach(testCase -> assertEquals(testCase.getExpectedMoveBitboard(), testCase.getActualMoveBitboard(), testCase.toString()));
	}
}
