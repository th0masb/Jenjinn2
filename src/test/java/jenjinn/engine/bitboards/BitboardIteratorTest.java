/**
 *
 */
package jenjinn.engine.bitboards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.base.BoardSquare;

/**
 * @author ThomasB
 */
class BitboardIteratorTest
{
	@ParameterizedTest
	@MethodSource
	void test(final List<BoardSquare> squareCollection)
	{
		final Set<BoardSquare> squareSet = new HashSet<>(squareCollection);
		final long bitboard = BitboardUtils.bitwiseOr(squareCollection);
		assertEquals(squareSet, BitboardIterator.from(bitboard).toSet());
	}

	static Stream<Arguments> test()
	{
		return Stream.of(
				Arguments.of(Arrays.asList()),
				Arguments.of(Arrays.asList(BoardSquare.H1)),
				Arguments.of(Arrays.asList(BoardSquare.A8)),
				Arguments.of(Arrays.asList(BoardSquare.H3, BoardSquare.C5)),
				Arguments.of(Arrays.asList(BoardSquare.H3, BoardSquare.C5, BoardSquare.A8)),
				Arguments.of(Arrays.asList(BoardSquare.H1, BoardSquare.C5, BoardSquare.A8))
				);
	}
}
