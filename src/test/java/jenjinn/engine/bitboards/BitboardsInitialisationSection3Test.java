/**
 *
 */
package jenjinn.engine.bitboards;

import static java.lang.Long.toBinaryString;
import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.BitboardsInitialisationSection3.findControlSetFromOccupancyVariation;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Direction;

/**
 * @author ThomasB
 */
class BitboardsInitialisationSection3Test
{
	@ParameterizedTest
	@MethodSource
	void testTakeUntil(final List<String> source, final Predicate<String> predicate, final List<String> expectedOutput)
	{
		assertEquals(expectedOutput, BitboardsInitialisationSection3.takeUntil(predicate, source));
	}

	static Stream<Arguments> testTakeUntil()
	{
		final Predicate<String> predicate = s -> Integer.parseInt(s) > 2;
		return Stream.of(
				Arguments.of(asList(), predicate, asList()),
				Arguments.of(asList("3"), predicate, asList("3")),
				Arguments.of(asList("0", "1"), predicate, asList("0", "1")),
				Arguments.of(asList("1", "3", "4"), predicate, asList("1", "3")),
				Arguments.of(asList("3", "4"), predicate, asList("3"))
				);
	}


	@ParameterizedTest
	@MethodSource
	void testFindControlSetFromOccupancyVariation(final Long expectedResult, final BoardSquare startSq, final Long occVar, final List<Direction> movementDirections)
	{
		final long calculatedResult = findControlSetFromOccupancyVariation(startSq, occVar, movementDirections);
		assertEquals(expectedResult.longValue(), calculatedResult, toBinaryString(expectedResult) + ", " + toBinaryString(calculatedResult));
	}

	static Stream<Arguments> testFindControlSetFromOccupancyVariation()
	{
		final BoardSquare centralSquare = BoardSquare.F2;
		final List<Direction> centralDirections = asList(Direction.E, Direction.S);

		final Arguments centralCaseOne = Arguments.of(0b1100000100L, centralSquare, 0L, centralDirections);
		final Arguments centralCaseTwo = Arguments.of(0b1100000100L, centralSquare, 0b100000100L, centralDirections);
		final Arguments centralCaseThree = Arguments.of(0b1000000100L, centralSquare, 0b1000000000L, centralDirections);

		final BoardSquare edgeSquare = BoardSquare.D1;
		final List<Direction> edgeDirections = asList(Direction.E, Direction.W, Direction.SE);

		final Arguments edgeCaseOne = Arguments.of(0b11101000L, edgeSquare, 0b100001011L, edgeDirections);
		final Arguments edgeCaseTwo = Arguments.of(0b00101000L, edgeSquare, 0b00101100L, edgeDirections);
		final Arguments edgeCaseThree = Arguments.of(0b01101100L, edgeSquare, 0b11000111L, edgeDirections);

		return Stream.of(centralCaseOne, centralCaseTwo, centralCaseThree, edgeCaseOne, edgeCaseTwo, edgeCaseThree);
	}
}
