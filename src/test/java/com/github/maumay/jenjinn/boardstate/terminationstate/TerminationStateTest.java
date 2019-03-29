/**
 *
 */
package com.github.maumay.jenjinn.boardstate.terminationstate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jenjinn.base.GameTermination;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.calculators.TerminationState;
import com.github.maumay.jflow.iterators.RichIterator;

/**
 * @author ThomasB
 */
class TerminationStateTest
{
	@Disabled
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, Boolean hasLegalMoves,
			GameTermination expectedTerminationState)
	{
		assertEquals(expectedTerminationState, TerminationState.of(state, hasLegalMoves));
	}

	static RichIterator<Arguments> test()
	{
		throw new RuntimeException();
	}
}
