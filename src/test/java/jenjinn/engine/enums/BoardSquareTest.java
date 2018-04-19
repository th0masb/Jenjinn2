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
