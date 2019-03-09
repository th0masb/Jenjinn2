/**
 *
 */
package jenjinn.boardstate.terminationstate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jflow.iterators.EnhancedIterator;

import jenjinn.base.GameTermination;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.calculators.TerminationState;

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

	static EnhancedIterator<Arguments> test()
	{
		throw new RuntimeException();
	}
}
