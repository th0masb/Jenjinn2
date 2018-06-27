/**
 *
 */
package jenjinn.engine.eval.kingsafety;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.eval.KingSafetyEvaluator;

/**
 * @author ThomasB
 */
class KingSafetyEvaluationTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, Integer expectedValue)
	{
		final KingSafetyEvaluator evaluator = new KingSafetyEvaluator();
		assertEquals(expectedValue.intValue(), evaluator.evaluate(state));
	}

	static Stream<Arguments> test()
	{
		throw new RuntimeException();
	}
}
