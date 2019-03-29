package com.github.maumay.jenjinn.boardstate.pinnedpieces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.calculators.PinnedPieces;
import com.github.maumay.jflow.iterators.Iter;
import com.github.maumay.jflow.iterators.RichIterator;

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

	static RichIterator<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iter.over(parser.parse("case001"), parser.parse("case002"));
	}
}
