/**
 *
 */
package jenjinn.engine.bitboards;

import static java.util.Arrays.asList;
import static jenjinn.engine.enums.BoardSquare.C1;
import static jenjinn.engine.enums.BoardSquare.D2;
import static jenjinn.engine.enums.BoardSquare.H1;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.enums.BoardSquare;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author ThomasB
 */
class BitboardUtilsTest
{
	@ParameterizedTest
	@MethodSource
	void testBitboardIntersection(final Long bitboardA, final Long bitboardB, final Boolean expectedIntersection)
	{
		assertEquals(expectedIntersection, BitboardUtils.bitboardsIntersect(bitboardA, bitboardB));
	}

	static Stream<Arguments> testBitboardIntersection()
	{
		return Stream.of(
				Arguments.of(0L, 0L, false),
				Arguments.of(0L, 0b1L, false),
				Arguments.of(0b1L, 0L, false),
				Arguments.of(0b1L, 0b10L, false),
				Arguments.of(0b100L, 0b101L, true));
	}

	@ParameterizedTest
	@MethodSource
	void testBitwiseOrOfLongArray(final long[] array, final Long expectedResult)
	{
		assertEquals(expectedResult.longValue(), BitboardUtils.bitwiseOr(array));
	}

	static Stream<Arguments> testBitwiseOrOfLongArray()
	{
		return Stream.of(
				Arguments.of(new long[] {}, 0L),
				Arguments.of(new long[] {0b1001, 0b1}, 0b1001L),
				Arguments.of(new long[] {0b1001, 0b10, 0b1010000}, 0b1011011L)
				);
	}

	@ParameterizedTest
	@MethodSource
	void testBitwiseOrOfBoardSquareList(final List<BoardSquare> squares, final Long expectedResult)
	{
		assertEquals(expectedResult.longValue(), BitboardUtils.bitwiseOr(squares));
	}

	@ParameterizedTest
	@MethodSource("testBitwiseOrOfBoardSquareList")
	void testBitwiseOrOfBoardSquareFlow(final List<BoardSquare> squares, final Long expectedResult)
	{
		assertEquals(expectedResult.longValue(), BitboardUtils.bitwiseOr(Iterate.over(squares)));
	}

	static Stream<Arguments> testBitwiseOrOfBoardSquareList()
	{
		return Stream.of(
				Arguments.of(asList(), 0L),
				Arguments.of(asList(C1, D2, H1), 0b1000000100001L)
				);
	}

	@ParameterizedTest
	@MethodSource
	void testGetSetBitIndices(final int[] expectedIndices, final Long bitboard)
	{
		Arrays.sort(expectedIndices);
		assertArrayEquals(expectedIndices, BitboardUtils.getSetBitIndices(bitboard.longValue()));
	}

	static Stream<Arguments> testGetSetBitIndices()
	{
		return Stream.of(
				Arguments.of(new int[] {}, 0L),
				Arguments.of(new int[] {5}, 0b100000L),
				Arguments.of(new int[] {3, 10}, 0b10000001000L)
				);
	}
}