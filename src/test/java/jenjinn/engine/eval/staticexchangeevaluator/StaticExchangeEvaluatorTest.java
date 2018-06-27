/**
 *
 */
package jenjinn.engine.eval.staticexchangeevaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.eval.StaticExchangeEvaluator;

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
		final StaticExchangeEvaluator see = new StaticExchangeEvaluator();
		for (final IndividualStateCase testCase : testCases) {
			assertEquals(
					testCase.isGoodExchange,
					see.isGoodExchange(testCase.source, testCase.target, state),
					testCase.toString());
		}
	}

	static Stream<Arguments> test()
	{
		throw new RuntimeException();
	}
}
