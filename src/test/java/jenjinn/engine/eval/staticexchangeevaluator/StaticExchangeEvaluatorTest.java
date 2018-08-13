/**
 *
 */
package jenjinn.engine.eval.staticexchangeevaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.eval.StaticExchangeEvaluator;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
class StaticExchangeEvaluatorTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, List<IndividualStateCase> testCases)
	{
		StaticExchangeEvaluator see = new StaticExchangeEvaluator();
		for (IndividualStateCase testCase : testCases) {
			assertEquals(
					testCase.isGoodExchange,
					see.isGoodExchange(testCase.source, testCase.target, state),
					testCase.toString());
		}
	}

	static Flow<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iterate.over("case001").map(parser::parse);
	}
}
