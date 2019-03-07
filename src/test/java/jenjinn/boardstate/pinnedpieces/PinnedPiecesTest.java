package jenjinn.boardstate.pinnedpieces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.base.Square;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.calculators.PinnedPieces;
import jflow.iterators.Flow;
import jflow.iterators.factories.Iter;

/**
 * @author ThomasB
 */
class PinnedPiecesTest
{
	@ParameterizedTest
	@MethodSource
	void test(final BoardState state, Set<Square> expectedPinnedPieces)
	{
		assertEquals(expectedPinnedPieces, PinnedPieces.in(state).getLocations());
	}

	static Flow<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iter.over(parser.parse("case001"), parser.parse("case002"));
	}
}
