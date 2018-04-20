/**
 * 
 */
package jenjinn.engine.enums;

import static io.xyz.chains.utilities.CollectionUtil.asList;
import static io.xyz.chains.utilities.RangeUtil.range;
import static java.util.stream.Collectors.partitioningBy;
import static jenjinn.engine.enums.BoardSquare.A1;
import static jenjinn.engine.enums.BoardSquare.A2;
import static jenjinn.engine.enums.BoardSquare.A3;
import static jenjinn.engine.enums.BoardSquare.A4;
import static jenjinn.engine.enums.BoardSquare.A5;
import static jenjinn.engine.enums.BoardSquare.A6;
import static jenjinn.engine.enums.BoardSquare.A7;
import static jenjinn.engine.enums.BoardSquare.A8;
import static jenjinn.engine.enums.BoardSquare.B1;
import static jenjinn.engine.enums.BoardSquare.B2;
import static jenjinn.engine.enums.BoardSquare.B3;
import static jenjinn.engine.enums.BoardSquare.B5;
import static jenjinn.engine.enums.BoardSquare.B8;
import static jenjinn.engine.enums.BoardSquare.C1;
import static jenjinn.engine.enums.BoardSquare.C2;
import static jenjinn.engine.enums.BoardSquare.C3;
import static jenjinn.engine.enums.BoardSquare.C4;
import static jenjinn.engine.enums.BoardSquare.C5;
import static jenjinn.engine.enums.BoardSquare.C6;
import static jenjinn.engine.enums.BoardSquare.C7;
import static jenjinn.engine.enums.BoardSquare.D1;
import static jenjinn.engine.enums.BoardSquare.D3;
import static jenjinn.engine.enums.BoardSquare.D4;
import static jenjinn.engine.enums.BoardSquare.D5;
import static jenjinn.engine.enums.BoardSquare.D6;
import static jenjinn.engine.enums.BoardSquare.D7;
import static jenjinn.engine.enums.BoardSquare.E1;
import static jenjinn.engine.enums.BoardSquare.E2;
import static jenjinn.engine.enums.BoardSquare.E3;
import static jenjinn.engine.enums.BoardSquare.E4;
import static jenjinn.engine.enums.BoardSquare.E5;
import static jenjinn.engine.enums.BoardSquare.E6;
import static jenjinn.engine.enums.BoardSquare.E7;
import static jenjinn.engine.enums.BoardSquare.E8;
import static jenjinn.engine.enums.BoardSquare.F1;
import static jenjinn.engine.enums.BoardSquare.F3;
import static jenjinn.engine.enums.BoardSquare.F4;
import static jenjinn.engine.enums.BoardSquare.F5;
import static jenjinn.engine.enums.BoardSquare.F6;
import static jenjinn.engine.enums.BoardSquare.F7;
import static jenjinn.engine.enums.BoardSquare.G1;
import static jenjinn.engine.enums.BoardSquare.G3;
import static jenjinn.engine.enums.BoardSquare.G4;
import static jenjinn.engine.enums.BoardSquare.G5;
import static jenjinn.engine.enums.BoardSquare.G6;
import static jenjinn.engine.enums.BoardSquare.G7;
import static jenjinn.engine.enums.BoardSquare.H1;
import static jenjinn.engine.enums.BoardSquare.H2;
import static jenjinn.engine.enums.BoardSquare.H5;
import static jenjinn.engine.enums.BoardSquare.H8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import jenjinn.engine.misc.RankFileCoordinate;

/**
 * @author t
 */
public class BoardSquareTest 
{
	@Test
	void testGetNumberOfSquaresLeftInDirection() 
	{
		final BoardSquare a1 = BoardSquare.A1;
		final Map<Boolean, List<Direction>> collect = Direction.stream()
				.collect(partitioningBy(dir -> dir.name().matches(".*[SsWw].*")));
		collect.get(true).stream().forEach(dir -> assertEquals(0, a1.getNumberOfSquaresLeftInDirection(dir), dir.name()));
		collect.get(false).stream().forEach(dir -> assertTrue(a1.getNumberOfSquaresLeftInDirection(dir) > 0));
		
		final BoardSquare d4 = BoardSquare.D4;
		assertEquals(4, d4.getNumberOfSquaresLeftInDirection(Direction.N));
		assertEquals(3, d4.getNumberOfSquaresLeftInDirection(Direction.W));
		assertEquals(3, d4.getNumberOfSquaresLeftInDirection(Direction.SE));
		assertEquals(4, d4.getNumberOfSquaresLeftInDirection(Direction.NE));
		assertEquals(2, d4.getNumberOfSquaresLeftInDirection(Direction.NEE));
		assertEquals(1, d4.getNumberOfSquaresLeftInDirection(Direction.SSW));
	}

	@Test
	void testGetNextSquareInDirection() 
	{
		final BoardSquare a1 = BoardSquare.A1;
		final Map<Boolean, List<Direction>> collect = Direction.stream()
				.collect(partitioningBy(dir -> dir.name().matches(".*[SsWw].*")));
		collect.get(true).stream().forEach(dir -> assertNull(a1.getNextSquareInDirection(dir)));
		collect.get(false).stream().forEach(dir -> assertNotNull(a1.getNextSquareInDirection(dir)));
		
		final BoardSquare d4 = BoardSquare.D4;
		assertEquals(BoardSquare.D5, d4.getNextSquareInDirection(Direction.N));
		assertEquals(BoardSquare.C4, d4.getNextSquareInDirection(Direction.W));
		assertEquals(BoardSquare.E3, d4.getNextSquareInDirection(Direction.SE));
		assertEquals(BoardSquare.E5, d4.getNextSquareInDirection(Direction.NE));
		assertEquals(BoardSquare.F5, d4.getNextSquareInDirection(Direction.NEE));
		assertEquals(BoardSquare.C2, d4.getNextSquareInDirection(Direction.SSW));
	}

	@Test
	void testGetAllSquaresInDirection()
	{
		final List<BoardSquareDirectionTestData> testCases = asList(
				getTestCaseForEdgeSquare(),
				getTestCaseForCentreSquare());
		
		for (final BoardSquareDirectionTestData testCase : testCases)
		{
			for (int i = 0; i < 9; i++) {
				// Test that we get every direction individually correct
				final int j = i;
				Direction.stream()
				.forEach(dir -> 
				assertEquals(testCase.getActualSquaresUniDirection(dir, j), testCase.getExpectedSquaresUniDirection(dir, j)));
				
				// Test that combining two directions works
				final Direction dir1 = Direction.N, dir2 = Direction.E;
				assertEquals(testCase.getActualSquaresBiDirection(dir1, dir2, j), testCase.getExpectedSquaresBiDirection(dir1, dir2, j));
			}
		}
	}
	
	private BoardSquareDirectionTestData getTestCaseForEdgeSquare()
	{
		final ImmutableMap.Builder<Direction, List<BoardSquare>> expectedResultsBuilder = ImmutableMap.builder();
		return new BoardSquareDirectionTestData(A1, expectedResultsBuilder
		.put(Direction.N, asList(A2, A3, A4, A5, A6, A7, A8))
		.put(Direction.E, asList(B1, C1, D1, E1, F1, G1, H1))
		.put(Direction.S, asList())
		.put(Direction.W, asList())
		.put(Direction.NE, asList(B2, C3, D4, E5, F6, G7, H8))
		.put(Direction.SE, asList())
		.put(Direction.SW, asList())
		.put(Direction.NW, asList())
		.put(Direction.NNE, asList(B3, C5, D7))
		.put(Direction.NEE, asList(C2, E3, G4))
		.put(Direction.SEE, asList())
		.put(Direction.SSE, asList())
		.put(Direction.SSW, asList())
		.put(Direction.SWW, asList())
		.put(Direction.NWW, asList())
		.put(Direction.NNW, asList())
		.build());
	}
	
	private BoardSquareDirectionTestData getTestCaseForCentreSquare()
	{
		final ImmutableMap.Builder<Direction, List<BoardSquare>> expectedResultsBuilder = ImmutableMap.builder();
		return new BoardSquareDirectionTestData(E5, expectedResultsBuilder
		.put(Direction.N, asList(E6, E7, E8))
		.put(Direction.E, asList(F5, G5, H5))
		.put(Direction.S, asList(E4, E3, E2, E1))
		.put(Direction.W, asList(D5, C5, B5, A5))
		.put(Direction.NE, asList(F6, G7, H8))
		.put(Direction.SE, asList(F4, G3, H2))
		.put(Direction.SW, asList(D4, C3, B2, A1))
		.put(Direction.NW, asList(D6, C7, B8))
		.put(Direction.NNE, asList(F7))
		.put(Direction.NEE, asList(G6))
		.put(Direction.SEE, asList(G4))
		.put(Direction.SSE, asList(F3, G1))
		.put(Direction.SSW, asList(D3, C1))
		.put(Direction.SWW, asList(C4, A3))
		.put(Direction.NWW, asList(C6, A7))
		.put(Direction.NNW, asList(D7))
		.build());
	}

	@Test
	void testAsRankFileCoord() 
	{
		assertEquals(new RankFileCoordinate(3, 5), BoardSquare.C4.asRankFileCoord());
		assertEquals(new RankFileCoordinate(7, 2), BoardSquare.F8.asRankFileCoord());
		assertEquals(new RankFileCoordinate(1, 4), BoardSquare.D2.asRankFileCoord());
		assertEquals(new RankFileCoordinate(6, 6), BoardSquare.B7.asRankFileCoord());
	}

	@Test
	void testAsBitboard() 
	{
		range(64).stream().forEach(i -> assertEquals(1L << i, BoardSquare.values()[i].asBitboard()));
	}

	@Test
	void testFromIndex() 
	{
		range(64).stream().forEach(i -> assertEquals(BoardSquare.values()[i], BoardSquare.fromIndex(i)));
	}

	@Test
	void testFromRankAndFileIndices() 
	{
		assertEquals(BoardSquare.C4, BoardSquare.fromRankAndFileIndices(3, 5));
		assertEquals(BoardSquare.F8, BoardSquare.fromRankAndFileIndices(7, 2));
		assertEquals(BoardSquare.D2, BoardSquare.fromRankAndFileIndices(1, 4));
		assertEquals(BoardSquare.B7, BoardSquare.fromRankAndFileIndices(6, 6));
	}
}
