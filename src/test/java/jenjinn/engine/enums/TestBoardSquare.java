/**
 * 
 */
package jenjinn.engine.enums;

import static io.xyz.chains.utilities.RangeUtil.range;
import static java.util.stream.Collectors.partitioningBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jenjinn.engine.misc.RankFileCoordinate;

/**
 * @author t
 */
class TestBoardSquare 
{
	@Test
	void testGetNumberOfSquaresLeftInDirection() 
	{
		final BoardSquare a1 = BoardSquare.a1;
		final Map<Boolean, List<Direction>> collect = Direction.stream()
				.collect(partitioningBy(dir -> dir.name().matches(".*[SsWw].*")));
		collect.get(true).stream().forEach(dir -> assertEquals(0, a1.getNumberOfSquaresLeftInDirection(dir), dir.name()));
		collect.get(false).stream().forEach(dir -> assertTrue(a1.getNumberOfSquaresLeftInDirection(dir) > 0));
		
		final BoardSquare d4 = BoardSquare.d4;
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
		final BoardSquare a1 = BoardSquare.a1;
		final Map<Boolean, List<Direction>> collect = Direction.stream()
				.collect(partitioningBy(dir -> dir.name().matches(".*[SsWw].*")));
		collect.get(true).stream().forEach(dir -> assertNull(a1.getNextSquareInDirection(dir)));
		collect.get(false).stream().forEach(dir -> assertNotNull(a1.getNextSquareInDirection(dir)));
		
		final BoardSquare d4 = BoardSquare.d4;
		assertEquals(BoardSquare.d5, d4.getNextSquareInDirection(Direction.N));
		assertEquals(BoardSquare.c4, d4.getNextSquareInDirection(Direction.W));
		assertEquals(BoardSquare.e3, d4.getNextSquareInDirection(Direction.SE));
		assertEquals(BoardSquare.e5, d4.getNextSquareInDirection(Direction.NE));
		assertEquals(BoardSquare.f5, d4.getNextSquareInDirection(Direction.NEE));
		assertEquals(BoardSquare.c2, d4.getNextSquareInDirection(Direction.SSW));
	}

	@Test
	void testGetAllSquaresInDirectionCapped() 
	{
		final BoardSquare a1 = BoardSquare.a1;
		assertTrue(a1.getAllSquaresInDirection(Direction.S, 3).isEmpty());
		assertTrue(a1.getAllSquaresInDirection(Direction.NNW, 3).isEmpty());
		assertEquals(EnumSet.of(BoardSquare.b2, BoardSquare.c3), EnumSet.copyOf(a1.getAllSquaresInDirection(Direction.NE, 2)));
		assertEquals(EnumSet.of(BoardSquare.b3, BoardSquare.c5, BoardSquare.d7), EnumSet.copyOf(a1.getAllSquaresInDirection(Direction.NNE, 3)));
		
		final BoardSquare f6 = BoardSquare.f6;
		assertEquals(EnumSet.of(BoardSquare.e6), EnumSet.copyOf(f6.getAllSquaresInDirection(Direction.W, 1)));
		assertEquals(EnumSet.of(BoardSquare.e7), EnumSet.copyOf(f6.getAllSquaresInDirection(Direction.NW, 1)));
		assertEquals(EnumSet.of(BoardSquare.f5), EnumSet.copyOf(f6.getAllSquaresInDirection(Direction.S, 1)));
	}

	@Test
	void testGetAllSquaresInDirectionUncapped() 
	{
		final BoardSquare a1 = BoardSquare.a1;
		assertTrue(a1.getAllSquaresInDirection(Direction.S, 3).isEmpty());
		assertTrue(a1.getAllSquaresInDirection(Direction.NNW, 3).isEmpty());
		
		final BoardSquare f6 = BoardSquare.f6;
		assertEquals(EnumSet.of(BoardSquare.f7, BoardSquare.f8), EnumSet.copyOf(f6.getAllSquaresInDirection(Direction.N)));
		assertEquals(EnumSet.of(BoardSquare.g6, BoardSquare.h6), EnumSet.copyOf(f6.getAllSquaresInDirection(Direction.E)));
	}

	@Test
	void testAsRankFileCoord() 
	{
		assertEquals(new RankFileCoordinate(3, 5), BoardSquare.c4.asRankFileCoord());
		assertEquals(new RankFileCoordinate(7, 2), BoardSquare.f8.asRankFileCoord());
		assertEquals(new RankFileCoordinate(1, 4), BoardSquare.d2.asRankFileCoord());
		assertEquals(new RankFileCoordinate(6, 6), BoardSquare.b7.asRankFileCoord());
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
		assertEquals(BoardSquare.c4, BoardSquare.fromRankAndFileIndices(3, 5));
		assertEquals(BoardSquare.f8, BoardSquare.fromRankAndFileIndices(7, 2));
		assertEquals(BoardSquare.d2, BoardSquare.fromRankAndFileIndices(1, 4));
		assertEquals(BoardSquare.b7, BoardSquare.fromRankAndFileIndices(6, 6));
	}
}
