package jenjinn.engine.boardstate.pinnedpieces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.calculators.PinnedPieces;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class PinnedPiecesTest
{
	@ParameterizedTest
	@MethodSource
	void test(final BoardState state, Set<BoardSquare> expectedPinnedPieces)
	{
		assertEquals(expectedPinnedPieces, PinnedPieces.in(state).getLocations());
	}

	static Flow<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iterate.over(parser.parse("case001"), parser.parse("case002"));
	}
}
