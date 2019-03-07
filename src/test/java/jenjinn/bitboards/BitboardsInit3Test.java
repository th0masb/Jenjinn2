/**
 *
 */
package jenjinn.bitboards;

import static java.lang.Long.toBinaryString;
import static java.util.Arrays.asList;
import static jenjinn.bitboards.BitboardsInit3.findControlSetFromOccupancyVariation;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.base.Dir;
import jenjinn.base.Square;
import jenjinn.bitboards.BitboardsInit3;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
class BitboardsInit3Test
{
	@ParameterizedTest
	@MethodSource
	void testTakeUntil(List<String> source, Predicate<String> predicate, List<String> expectedOutput)
	{
		assertEquals(expectedOutput, BitboardsInit3.takeUntil(predicate, source));
	}

	static Stream<Arguments> testTakeUntil()
	{
		Predicate<String> predicate = s -> Integer.parseInt(s) > 2;
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
	void testFindControlSetFromOccupancyVariation(Long expectedResult, Square startSq, Long occVar, Seq<Dir> movementDirections)
	{
		long calculatedResult = findControlSetFromOccupancyVariation(startSq, occVar, movementDirections);
		assertEquals(expectedResult.longValue(), calculatedResult, toBinaryString(expectedResult) + ", " + toBinaryString(calculatedResult));
	}

	static Stream<Arguments> testFindControlSetFromOccupancyVariation()
	{
		Square centralSquare = Square.F2;
		Seq<Dir> centralDirections = Seq.of(Dir.E, Dir.S);

		Arguments centralCaseOne = Arguments.of(0b1100000100L, centralSquare, 0L, centralDirections);
		Arguments centralCaseTwo = Arguments.of(0b1100000100L, centralSquare, 0b100000100L, centralDirections);
		Arguments centralCaseThree = Arguments.of(0b1000000100L, centralSquare, 0b1000000000L, centralDirections);

		Square edgeSquare = Square.D1;
		Seq<Dir> edgeDirections = Seq.of(Dir.E, Dir.W, Dir.SE);

		Arguments edgeCaseOne = Arguments.of(0b11101000L, edgeSquare, 0b100001011L, edgeDirections);
		Arguments edgeCaseTwo = Arguments.of(0b00101000L, edgeSquare, 0b00101100L, edgeDirections);
		Arguments edgeCaseThree = Arguments.of(0b01101100L, edgeSquare, 0b11000111L, edgeDirections);

		return Stream.of(centralCaseOne, centralCaseTwo, centralCaseThree, edgeCaseOne, edgeCaseTwo, edgeCaseThree);
	}
}
