/**
 *
 */
package jenjinn.bitboards;

import static java.util.Arrays.asList;
import static jenjinn.base.Square.C1;
import static jenjinn.base.Square.D2;
import static jenjinn.base.Square.H1;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.base.Square;
import jenjinn.bitboards.BitboardIterator;
import jenjinn.bitboards.BitboardUtils;
import jflow.iterators.factories.Iter;

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
	void testBitwiseOrOfBoardSquareList(final List<Square> squares, final Long expectedResult)
	{
		assertEquals(expectedResult.longValue(), BitboardUtils.bitwiseOr(squares));
	}

	@ParameterizedTest
	@MethodSource("testBitwiseOrOfBoardSquareList")
	void testBitwiseOrOfBoardSquareFlow(final List<Square> squares, final Long expectedResult)
	{
		assertEquals(expectedResult.longValue(), BitboardUtils.bitwiseOr(Iter.over(squares)));
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
	void testGetSetBitIndices(final List<Square> expectedSquares, final Long bitboard)
	{
		assertEquals(expectedSquares, BitboardIterator.from(bitboard.longValue()).toList());
	}

	static Stream<Arguments> testGetSetBitIndices()
	{
		return Stream.of(
				Arguments.of(asList(), 0L),
				Arguments.of(asList(Square.C1), 0b100000L),
				Arguments.of(asList(Square.E1, Square.F2), 0b10000001000L)
				);
	}
}
