/**
 *
 */
package jenjinn.engine.bitboards;

import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.BitboardsInitialisationSection2.calculateOccupancyVariations;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Direction;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class BitboardsInitialisationSection2Test
{
	@ParameterizedTest
	@MethodSource
	void testFindAllPossibleOrCombos(final long[] input, final Set<Long> expectedResult)
	{
		assertEquals(expectedResult, Iterate.overLongs(BitboardsInitialisationSection2.bitwiseOrAllSetsInPowerset(input)).mapToObject(x -> x).toSet());
	}

	static Stream<Arguments> testFindAllPossibleOrCombos()
	{
		return Stream.of(
				Arguments.of(new long[0], new HashSet<Long>()),
				Arguments.of(new long[] {4L}, new HashSet<>(asList(0L, 4L))),
				Arguments.of(new long[] {0b1L, 0b1010L}, new HashSet<>(asList(0L, 0b1L, 0b1010L, 0b1011L)))
				);
	}


	@ParameterizedTest
	@MethodSource
	void testCalculateOccupancyVariations(final Set<Long> expectedResult, final BoardSquare startSquare, final List<Direction> movementDirections)
	{
		assertEquals(expectedResult, Iterate.overLongs(calculateOccupancyVariations(startSquare, movementDirections)).mapToObject(i -> i).toSet());
	}

	static Stream<Arguments> testCalculateOccupancyVariations()
	{
		final Arguments firstCase = Arguments.of(
				new HashSet<>(asList(0L, 0b1000000000L)),
				BoardSquare.F2,
				asList(Direction.E, Direction.S)
				);

		final Arguments secondCase = Arguments.of(
				new HashSet<>(asList(0L, 0b10L, 0b100L, 0b1000L, 0b110L, 0b1010L, 0b1100L, 0b1110L)),
				BoardSquare.D1,
				asList(Direction.E)
				);

		return Stream.of(firstCase, secondCase);
	}
}
