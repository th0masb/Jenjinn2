/**
 *
 */
package jenjinn.eval.staticexchangeevaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.boardstate.BoardState;
import jenjinn.eval.StaticExchangeEvaluator;
import jflow.iterators.Flow;
import jflow.iterators.factories.Iter;
import jflow.seq.Seq;

/**
 * @author ThomasB
 *
 */
class StaticExchangeEvaluatorTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, Seq<IndividualStateCase> testCases)
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
		return Iter.over("case001").map(parser::parse);
	}
}
