package jenjinn.engine.boardstate.pinnedpieces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.calculators.PinnedPieces;
import jenjinn.engine.enums.BoardSquare;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class PinnedPiecesTest
{
	@ParameterizedTest
	@MethodSource
	void test(final BoardState state, final Set<BoardSquare> expectedPinnedPieces)
	{
		assertEquals(expectedPinnedPieces, PinnedPieces.in(state).getLocations());
	}

	static Flow<Arguments> test()
	{
		return Iterate.over(TestFileParser.parse("case001"), TestFileParser.parse("case002"));
	}
}
