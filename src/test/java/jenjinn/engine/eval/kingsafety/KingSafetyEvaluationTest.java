/**
 *
 */
package jenjinn.engine.eval.kingsafety;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.eval.KingSafetyEvaluator;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class KingSafetyEvaluationTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, Integer expectedValue)
	{
		assertEquals(expectedValue.intValue(), new KingSafetyEvaluator().evaluate(state));
	}

	static Flow<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iterate.over("case001").map(parser::parse);
	}
}
