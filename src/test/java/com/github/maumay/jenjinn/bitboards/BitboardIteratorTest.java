/**
 *
 */
package com.github.maumay.jenjinn.bitboards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.bitboards.Bitboard;
import com.github.maumay.jenjinn.bitboards.BitboardIterator;

/**
 * @author ThomasB
 */
class BitboardIteratorTest
{
	@ParameterizedTest
	@MethodSource
	void test(final List<Square> squareCollection)
	{
		final Set<Square> squareSet = new HashSet<>(squareCollection);
		final long bitboard = Bitboard.fold(squareCollection);
		assertEquals(squareSet, BitboardIterator.from(bitboard).toSet());
	}

	static Stream<Arguments> test()
	{
		return Stream.of(
				Arguments.of(Arrays.asList()),
				Arguments.of(Arrays.asList(Square.H1)),
				Arguments.of(Arrays.asList(Square.A8)),
				Arguments.of(Arrays.asList(Square.H3, Square.C5)),
				Arguments.of(Arrays.asList(Square.H3, Square.C5, Square.A8)),
				Arguments.of(Arrays.asList(Square.H1, Square.C5, Square.A8))
				);
	}
}
