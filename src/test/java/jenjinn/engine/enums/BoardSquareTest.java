/**
 * 
 */
package jenjinn.engine.enums;

import static io.xyz.chains.utilities.CollectionUtil.asList;
import static io.xyz.chains.utilities.RangeUtil.range;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
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
import static jenjinn.engine.enums.BoardSquare.C1;
import static jenjinn.engine.enums.BoardSquare.C2;
import static jenjinn.engine.enums.BoardSquare.C3;
import static jenjinn.engine.enums.BoardSquare.C5;
import static jenjinn.engine.enums.BoardSquare.D1;
import static jenjinn.engine.enums.BoardSquare.D4;
import static jenjinn.engine.enums.BoardSquare.D7;
import static jenjinn.engine.enums.BoardSquare.E1;
import static jenjinn.engine.enums.BoardSquare.E3;
import static jenjinn.engine.enums.BoardSquare.E5;
import static jenjinn.engine.enums.BoardSquare.F1;
import static jenjinn.engine.enums.BoardSquare.F6;
import static jenjinn.engine.enums.BoardSquare.G1;
import static jenjinn.engine.enums.BoardSquare.G4;
import static jenjinn.engine.enums.BoardSquare.G7;
import static jenjinn.engine.enums.BoardSquare.H1;
import static jenjinn.engine.enums.BoardSquare.H8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import jenjinn.engine.misc.RankFileCoordinate;

/**
 * @author t
 */
class BoardSquareTest 
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
	void testGetAllSquaresInDirectionCapped() 
	{
		final BoardSquare a1 = BoardSquare.A1;
		assertTrue(a1.getAllSquaresInDirections(Direction.S, 3).isEmpty());
		assertTrue(a1.getAllSquaresInDirections(Direction.NNW, 3).isEmpty());
		assertEquals(EnumSet.of(BoardSquare.B2, BoardSquare.C3), EnumSet.copyOf(a1.getAllSquaresInDirections(Direction.NE, 2)));
		assertEquals(EnumSet.of(BoardSquare.B3, BoardSquare.C5, BoardSquare.D7), EnumSet.copyOf(a1.getAllSquaresInDirections(Direction.NNE, 3)));
		
		final BoardSquare f6 = BoardSquare.F6;
		assertEquals(EnumSet.of(BoardSquare.E6), EnumSet.copyOf(f6.getAllSquaresInDirections(Direction.W, 1)));
		assertEquals(EnumSet.of(BoardSquare.E7), EnumSet.copyOf(f6.getAllSquaresInDirections(Direction.NW, 1)));
		assertEquals(EnumSet.of(BoardSquare.F5), EnumSet.copyOf(f6.getAllSquaresInDirections(Direction.S, 1)));
	}

	@Test
	void testGetAllSquaresInDirectionUncapped() 
	{
		final BoardSquare a1 = BoardSquare.A1;
		assertTrue(a1.getAllSquaresInDirections(Direction.S).isEmpty());
		assertTrue(a1.getAllSquaresInDirections(Direction.NNW).isEmpty());
		
		final BoardSquare f6 = BoardSquare.F6;
		assertEquals(EnumSet.of(BoardSquare.F7, BoardSquare.F8), EnumSet.copyOf(f6.getAllSquaresInDirections(Direction.N)));
		assertEquals(EnumSet.of(BoardSquare.G6, BoardSquare.H6), EnumSet.copyOf(f6.getAllSquaresInDirections(Direction.E)));
	}
	
	@Test
	void testGetAllSquaresInDirection()
	{
		final BoardSquare edgeSquare = A1;
		
		L
		
		for (final int i = 0; i < 8; i++) {
			final int j = i;
			expectedResult.entrySet()
			.stream()
			.forEach(pair -> 
			assertEquals(take(j, pair.getValue()), edgeSquare.getAllSquaresInDirections(pair.getKey(), j), pair.getKey().name())
			);
		}
		
		expectedResult.entrySet()
		.stream()
		.forEach(pair -> 
		assertEquals(pair.getValue(), edgeSquare.getAllSquaresInDirections(pair.getKey()), pair.getKey().name())
		);
		
		final EnumSet<BoardSquare> expectedCombinedSet = combine(expectedResult.get(Direction.N), expectedResult.get(Direction.E));
		assertEquals(expectedCombinedSet, EnumSet.copyOf(edgeSquare.getAllSquaresInDirections(Direction.N, Direction.E)));
		assertEquals(expectedCombinedSet, EnumSet.copyOf(edgeSquare.getAllSquaresInDirections(asList(Direction.N, Direction.E))));
	}
	
	@SafeVarargs
	private final EnumSet<BoardSquare> combine(final List<BoardSquare>... squares)
	{
		return EnumSet.copyOf(Arrays.stream(squares).flatMap(List::stream).collect(toList()));
	}
	
	private List<BoardSquare> take(final int n, final List<BoardSquare> src)
	{
		return src.subList(0, Math.min(n, src.size()));
	}
	
	private Map<Direction, List<BoardSquare>> getExpectedValuesForSquareA1()
	{
		final ImmutableMap.Builder<Direction, List<BoardSquare>> expectedResultsBuilder = ImmutableMap.builder();
		
		return expectedResultsBuilder
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
		.build();
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
