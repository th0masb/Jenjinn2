/**
 *
 */
package jenjinn.enums;

import static jenjinn.base.Square.A1;
import static jenjinn.base.Square.A2;
import static jenjinn.base.Square.A3;
import static jenjinn.base.Square.A4;
import static jenjinn.base.Square.A5;
import static jenjinn.base.Square.A6;
import static jenjinn.base.Square.A7;
import static jenjinn.base.Square.A8;
import static jenjinn.base.Square.B1;
import static jenjinn.base.Square.B2;
import static jenjinn.base.Square.B3;
import static jenjinn.base.Square.B5;
import static jenjinn.base.Square.B8;
import static jenjinn.base.Square.C1;
import static jenjinn.base.Square.C2;
import static jenjinn.base.Square.C3;
import static jenjinn.base.Square.C4;
import static jenjinn.base.Square.C5;
import static jenjinn.base.Square.C6;
import static jenjinn.base.Square.C7;
import static jenjinn.base.Square.D1;
import static jenjinn.base.Square.D3;
import static jenjinn.base.Square.D4;
import static jenjinn.base.Square.D5;
import static jenjinn.base.Square.D6;
import static jenjinn.base.Square.D7;
import static jenjinn.base.Square.E1;
import static jenjinn.base.Square.E2;
import static jenjinn.base.Square.E3;
import static jenjinn.base.Square.E4;
import static jenjinn.base.Square.E5;
import static jenjinn.base.Square.E6;
import static jenjinn.base.Square.E7;
import static jenjinn.base.Square.E8;
import static jenjinn.base.Square.F1;
import static jenjinn.base.Square.F3;
import static jenjinn.base.Square.F4;
import static jenjinn.base.Square.F5;
import static jenjinn.base.Square.F6;
import static jenjinn.base.Square.F7;
import static jenjinn.base.Square.G1;
import static jenjinn.base.Square.G3;
import static jenjinn.base.Square.G4;
import static jenjinn.base.Square.G5;
import static jenjinn.base.Square.G6;
import static jenjinn.base.Square.G7;
import static jenjinn.base.Square.H1;
import static jenjinn.base.Square.H2;
import static jenjinn.base.Square.H5;
import static jenjinn.base.Square.H8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.base.Dir;
import jenjinn.base.Square;
import jflow.iterators.factories.IterRange;
import jflow.iterators.misc.Pair;
import jflow.seq.Seq;

/**
 * @author t
 */
class BoardSquareTest
{
	@Test
	void testGetNumberOfSquaresLeftInDirection()
	{
		Square a1 = Square.A1;
		
		Pair<Seq<Dir>, Seq<Dir>> partitioned = Dir.ALL.partition(dir -> dir.name().matches(".*[SsWw].*"));
		partitioned._1.forEach(dir -> assertEquals(0, a1.getNumberOfSquaresLeft(dir), dir.name()));
		partitioned._2.forEach(dir -> assertTrue(a1.getNumberOfSquaresLeft(dir) > 0));

		Square d4 = Square.D4;
		assertEquals(4, d4.getNumberOfSquaresLeft(Dir.N));
		assertEquals(3, d4.getNumberOfSquaresLeft(Dir.W));
		assertEquals(3, d4.getNumberOfSquaresLeft(Dir.SE));
		assertEquals(4, d4.getNumberOfSquaresLeft(Dir.NE));
		assertEquals(2, d4.getNumberOfSquaresLeft(Dir.NEE));
		assertEquals(1, d4.getNumberOfSquaresLeft(Dir.SSW));
	}

	@Test
	void testGetNextSquareInDirection()
	{
		Square a1 = Square.A1;
		Pair<Seq<Dir>, Seq<Dir>> partitioned = Dir.ALL.partition(dir -> dir.name().matches(".*[SsWw].*"));
		partitioned._1.forEach(dir -> assertFalse(a1.getNextSquare(dir).isPresent()));
		partitioned._2.forEach(dir -> assertTrue(a1.getNextSquare(dir).isPresent()));

		Square d4 = Square.D4;
		assertEquals(Optional.of(Square.D5), d4.getNextSquare(Dir.N));
		assertEquals(Optional.of(Square.C4), d4.getNextSquare(Dir.W));
		assertEquals(Optional.of(Square.E3), d4.getNextSquare(Dir.SE));
		assertEquals(Optional.of(Square.E5), d4.getNextSquare(Dir.NE));
		assertEquals(Optional.of(Square.F5), d4.getNextSquare(Dir.NEE));
		assertEquals(Optional.of(Square.C2), d4.getNextSquare(Dir.SSW));
	}

	@ParameterizedTest
	@MethodSource
	void testGetAllSquaresInDirection(Square startSquare, Map<Dir, Seq<Square>> expectedSquaresInEachDirection)
	{
		Map<Dir, Seq<Square>> expsquares = expectedSquaresInEachDirection;
		for (int i = 0; i < 9; i++) {
			// Test that we get every direction individually correct
			int j = i;
			Dir.ALL.forEach(dir -> {
				Seq<Square> expected = expsquares.get(dir).take(j);
				Seq<Square> actual = startSquare.getAllSquares(dir, j);
				assertEquals(expected, actual);
			});
					
			// Test that combining two directions works
			Seq<Seq<Dir>> twoDirs = Seq.of(Seq.of(Dir.N, Dir.E), Seq.of(Dir.S, Dir.SE));
			for (Seq<Dir> pair : twoDirs) {
				Set<Square> expected = expsquares.get(pair.head()).take(j).toSet();
				expected.addAll(expsquares.get(pair.last()).take(j).toSet());
				assertEquals(expected, startSquare.getAllSquares(pair, j).toSet());
			}
		}
	}

	static Stream<Arguments> testGetAllSquaresInDirection()
	{
		return Stream.of(getTestCaseForEdgeSquare(), getTestCaseForCentreSquare());
	}

	private static Arguments getTestCaseForEdgeSquare()
	{
		Map<Dir, Seq<Square>> expectedResults = new HashMap<>();
		expectedResults.put(Dir.N, Seq.of(A2, A3, A4, A5, A6, A7, A8));
		expectedResults.put(Dir.E, Seq.of(B1, C1, D1, E1, F1, G1, H1));
		expectedResults.put(Dir.S, Seq.of());
		expectedResults.put(Dir.W, Seq.of());
		expectedResults.put(Dir.NE, Seq.of(B2, C3, D4, E5, F6, G7, H8));
		expectedResults.put(Dir.SE, Seq.of());
		expectedResults.put(Dir.SW, Seq.of());
		expectedResults.put(Dir.NW, Seq.of());
		expectedResults.put(Dir.NNE, Seq.of(B3, C5, D7));
		expectedResults.put(Dir.NEE, Seq.of(C2, E3, G4));
		expectedResults.put(Dir.SEE, Seq.of());
		expectedResults.put(Dir.SSE, Seq.of());
		expectedResults.put(Dir.SSW, Seq.of());
		expectedResults.put(Dir.SWW, Seq.of());
		expectedResults.put(Dir.NWW, Seq.of());
		expectedResults.put(Dir.NNW, Seq.of());

		return Arguments.of(A1, expectedResults);
	}

	private static Arguments getTestCaseForCentreSquare()
	{
		Map<Dir, Seq<Square>> expectedResults = new HashMap<>();
		expectedResults.put(Dir.N, Seq.of(E6, E7, E8));
		expectedResults.put(Dir.E, Seq.of(F5, G5, H5));
		expectedResults.put(Dir.S, Seq.of(E4, E3, E2, E1));
		expectedResults.put(Dir.W, Seq.of(D5, C5, B5, A5));
		expectedResults.put(Dir.NE, Seq.of(F6, G7, H8));
		expectedResults.put(Dir.SE, Seq.of(F4, G3, H2));
		expectedResults.put(Dir.SW, Seq.of(D4, C3, B2, A1));
		expectedResults.put(Dir.NW, Seq.of(D6, C7, B8));
		expectedResults.put(Dir.NNE, Seq.of(F7));
		expectedResults.put(Dir.NEE, Seq.of(G6));
		expectedResults.put(Dir.SEE, Seq.of(G4));
		expectedResults.put(Dir.SSE, Seq.of(F3, G1));
		expectedResults.put(Dir.SSW, Seq.of(D3, C1));
		expectedResults.put(Dir.SWW, Seq.of(C4, A3));
		expectedResults.put(Dir.NWW, Seq.of(C6, A7));
		expectedResults.put(Dir.NNW, Seq.of(D7));

		return Arguments.of(E5, expectedResults);
	}

	@Test
	void testAsBitboard()
	{
		IterRange.to(64).forEach(i -> assertEquals(1L << i, Square.values()[i].bitboard));
	}

	@Test
	void testFromIndex()
	{
		IterRange.to(64).forEach(i -> assertEquals(Square.values()[i], Square.of(i)));
	}

	@Test
	void testFromRankAndFileIndices()
	{
		assertEquals(Square.C4, Square.fromRankAndFileIndices(3, 5));
		assertEquals(Square.F8, Square.fromRankAndFileIndices(7, 2));
		assertEquals(Square.D2, Square.fromRankAndFileIndices(1, 4));
		assertEquals(Square.B7, Square.fromRankAndFileIndices(6, 6));
	}
}
