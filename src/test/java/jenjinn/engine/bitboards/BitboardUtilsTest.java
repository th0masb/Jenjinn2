/**
 *
 */
package jenjinn.engine.bitboards;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author ThomasB
 *
 */
class BitboardUtilsTest
{
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
				Arguments.of(new int[] {3, 10}, 0b10000001000L)
				);
	}
}
