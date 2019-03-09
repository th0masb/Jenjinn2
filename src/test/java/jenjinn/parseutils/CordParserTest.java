package jenjinn.parseutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jflow.iterators.EnhancedIterator;
import com.github.maumay.jflow.iterators.factories.Iter;
import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.Square;

/**
 * @author ThomasB
 */
class CordParserTest
{
	@ParameterizedTest
	@MethodSource
	void test(String encodedCord, int[] expectedSquareIndices)
	{
		Vec<Square> squares = Iter.ints(expectedSquareIndices).mapToObject(Square::of)
				.toVec();
		assertEquals(squares, CordParser.parse(encodedCord));
	}

	static EnhancedIterator<Arguments> test()
	{
		return Iter.over(Arguments.of("g1->D1", new int[] { 1, 2, 3, 4 }),
				Arguments.of("G1->F3", new int[] { 1, 18 }));
	}

	void testErrors()
	{
		assertThrows(IllegalArgumentException.class, () -> CordParser.parse("g1 -> g2"));
		assertThrows(IllegalArgumentException.class, () -> CordParser.parse("g1 ->"));
		assertThrows(IllegalArgumentException.class, () -> CordParser.parse("g1 -> a2"));
	}
}
