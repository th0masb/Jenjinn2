/**
 *
 */
package jenjinn.engine.enums;

import static java.util.Arrays.asList;
import static jenjinn.engine.base.BoardSquare.A1;
import static jenjinn.engine.base.BoardSquare.A2;
import static jenjinn.engine.base.BoardSquare.A3;
import static jenjinn.engine.base.BoardSquare.A4;
import static jenjinn.engine.base.BoardSquare.A5;
import static jenjinn.engine.base.BoardSquare.A6;
import static jenjinn.engine.base.BoardSquare.A7;
import static jenjinn.engine.base.BoardSquare.A8;
import static jenjinn.engine.base.BoardSquare.B1;
import static jenjinn.engine.base.BoardSquare.B2;
import static jenjinn.engine.base.BoardSquare.B3;
import static jenjinn.engine.base.BoardSquare.B5;
import static jenjinn.engine.base.BoardSquare.B8;
import static jenjinn.engine.base.BoardSquare.C1;
import static jenjinn.engine.base.BoardSquare.C2;
import static jenjinn.engine.base.BoardSquare.C3;
import static jenjinn.engine.base.BoardSquare.C4;
import static jenjinn.engine.base.BoardSquare.C5;
import static jenjinn.engine.base.BoardSquare.C6;
import static jenjinn.engine.base.BoardSquare.C7;
import static jenjinn.engine.base.BoardSquare.D1;
import static jenjinn.engine.base.BoardSquare.D3;
import static jenjinn.engine.base.BoardSquare.D4;
import static jenjinn.engine.base.BoardSquare.D5;
import static jenjinn.engine.base.BoardSquare.D6;
import static jenjinn.engine.base.BoardSquare.D7;
import static jenjinn.engine.base.BoardSquare.E1;
import static jenjinn.engine.base.BoardSquare.E2;
import static jenjinn.engine.base.BoardSquare.E3;
import static jenjinn.engine.base.BoardSquare.E4;
import static jenjinn.engine.base.BoardSquare.E5;
import static jenjinn.engine.base.BoardSquare.E6;
import static jenjinn.engine.base.BoardSquare.E7;
import static jenjinn.engine.base.BoardSquare.E8;
import static jenjinn.engine.base.BoardSquare.F1;
import static jenjinn.engine.base.BoardSquare.F3;
import static jenjinn.engine.base.BoardSquare.F4;
import static jenjinn.engine.base.BoardSquare.F5;
import static jenjinn.engine.base.BoardSquare.F6;
import static jenjinn.engine.base.BoardSquare.F7;
import static jenjinn.engine.base.BoardSquare.G1;
import static jenjinn.engine.base.BoardSquare.G3;
import static jenjinn.engine.base.BoardSquare.G4;
import static jenjinn.engine.base.BoardSquare.G5;
import static jenjinn.engine.base.BoardSquare.G6;
import static jenjinn.engine.base.BoardSquare.G7;
import static jenjinn.engine.base.BoardSquare.H1;
import static jenjinn.engine.base.BoardSquare.H2;
import static jenjinn.engine.base.BoardSquare.H5;
import static jenjinn.engine.base.BoardSquare.H8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.last;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Direction;
import xawd.jflow.iterators.factories.IterRange;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.iterators.misc.PredicatePartition;

/**
 * @author t
 */
class BoardSquareTest
{
	@Test
	void testGetNumberOfSquaresLeftInDirection()
	{
		BoardSquare a1 = BoardSquare.A1;
		PredicatePartition<Direction> partitioned = Direction.iterateAll()
				.partition(dir -> dir.name().matches(".*[SsWw].*"));
		partitioned.getAccepted()
				.forEach(dir -> assertEquals(0, a1.getNumberOfSquaresLeftInDirection(dir), dir.name()));
		partitioned.getRejected().forEach(dir -> assertTrue(a1.getNumberOfSquaresLeftInDirection(dir) > 0));

		BoardSquare d4 = BoardSquare.D4;
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
		BoardSquare a1 = BoardSquare.A1;
		PredicatePartition<Direction> partitioned = Direction.iterateAll()
				.partition(dir -> dir.name().matches(".*[SsWw].*"));
		partitioned.getAccepted().forEach(dir -> assertNull(a1.getNextSquareInDirection(dir)));
		partitioned.getRejected().forEach(dir -> assertNotNull(a1.getNextSquareInDirection(dir)));

		BoardSquare d4 = BoardSquare.D4;
		assertEquals(BoardSquare.D5, d4.getNextSquareInDirection(Direction.N));
		assertEquals(BoardSquare.C4, d4.getNextSquareInDirection(Direction.W));
		assertEquals(BoardSquare.E3, d4.getNextSquareInDirection(Direction.SE));
		assertEquals(BoardSquare.E5, d4.getNextSquareInDirection(Direction.NE));
		assertEquals(BoardSquare.F5, d4.getNextSquareInDirection(Direction.NEE));
		assertEquals(BoardSquare.C2, d4.getNextSquareInDirection(Direction.SSW));
	}

	@ParameterizedTest
	@MethodSource
	void testGetAllSquaresInDirection(BoardSquare startSquare,
			Map<Direction, List<BoardSquare>> expectedSquaresInEachDirection)
	{
		for (int i = 0; i < 9; i++) {
			// Test that we get every direction individually correct
			int j = i;
			Direction.iterateAll().forEach(dir -> assertEquals(take(j, expectedSquaresInEachDirection.get(dir)),
					startSquare.getAllSquaresInDirections(dir, j)));

			// Test that combining two directions works
			for (List<Direction> pair : asList(asList(Direction.N, Direction.E),
					asList(Direction.S, Direction.SE))) {
				Set<BoardSquare> expected = Iterate.over(expectedSquaresInEachDirection.get(head(pair))).take(j)
						.toCollection(HashSet::new);
				expected.addAll(take(j, expectedSquaresInEachDirection.get(last(pair))));

				assertEquals(expected, new HashSet<>(startSquare.getAllSquaresInDirections(pair, j)));
			}
		}
	}

	static Stream<Arguments> testGetAllSquaresInDirection()
	{
		return Stream.of(getTestCaseForEdgeSquare(), getTestCaseForCentreSquare());
	}

	private static Arguments getTestCaseForEdgeSquare()
	{
		Map<Direction, List<BoardSquare>> expectedResults = new HashMap<>();
		expectedResults.put(Direction.N, asList(A2, A3, A4, A5, A6, A7, A8));
		expectedResults.put(Direction.E, asList(B1, C1, D1, E1, F1, G1, H1));
		expectedResults.put(Direction.S, asList());
		expectedResults.put(Direction.W, asList());
		expectedResults.put(Direction.NE, asList(B2, C3, D4, E5, F6, G7, H8));
		expectedResults.put(Direction.SE, asList());
		expectedResults.put(Direction.SW, asList());
		expectedResults.put(Direction.NW, asList());
		expectedResults.put(Direction.NNE, asList(B3, C5, D7));
		expectedResults.put(Direction.NEE, asList(C2, E3, G4));
		expectedResults.put(Direction.SEE, asList());
		expectedResults.put(Direction.SSE, asList());
		expectedResults.put(Direction.SSW, asList());
		expectedResults.put(Direction.SWW, asList());
		expectedResults.put(Direction.NWW, asList());
		expectedResults.put(Direction.NNW, asList());

		return Arguments.of(A1, expectedResults);
	}

	private static Arguments getTestCaseForCentreSquare()
	{
		Map<Direction, List<BoardSquare>> expectedResults = new HashMap<>();
		expectedResults.put(Direction.N, asList(E6, E7, E8));
		expectedResults.put(Direction.E, asList(F5, G5, H5));
		expectedResults.put(Direction.S, asList(E4, E3, E2, E1));
		expectedResults.put(Direction.W, asList(D5, C5, B5, A5));
		expectedResults.put(Direction.NE, asList(F6, G7, H8));
		expectedResults.put(Direction.SE, asList(F4, G3, H2));
		expectedResults.put(Direction.SW, asList(D4, C3, B2, A1));
		expectedResults.put(Direction.NW, asList(D6, C7, B8));
		expectedResults.put(Direction.NNE, asList(F7));
		expectedResults.put(Direction.NEE, asList(G6));
		expectedResults.put(Direction.SEE, asList(G4));
		expectedResults.put(Direction.SSE, asList(F3, G1));
		expectedResults.put(Direction.SSW, asList(D3, C1));
		expectedResults.put(Direction.SWW, asList(C4, A3));
		expectedResults.put(Direction.NWW, asList(C6, A7));
		expectedResults.put(Direction.NNW, asList(D7));

		return Arguments.of(E5, expectedResults);
	}

	@Test
	void testAsBitboard()
	{
		IterRange.to(64).forEach(i -> assertEquals(1L << i, BoardSquare.values()[i].asBitboard()));
	}

	@Test
	void testFromIndex()
	{
		IterRange.to(64).forEach(i -> assertEquals(BoardSquare.values()[i], BoardSquare.of(i)));
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
