/**
 *
 */
package com.github.maumay.jenjinn.eval.staticexchangeevaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.eval.StaticExchangeEvaluator;
import com.github.maumay.jflow.iterators.Iter;
import com.github.maumay.jflow.iterators.RichIterator;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 *
 */
class StaticExchangeEvaluatorTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, Vec<IndividualStateCase> testCases)
	{
		StaticExchangeEvaluator see = new StaticExchangeEvaluator();
		for (IndividualStateCase testCase : testCases) {
			assertEquals(testCase.isGoodExchange,
					see.isGoodExchange(testCase.source, testCase.target, state),
					testCase.toString());
		}
	}

	static RichIterator<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iter.over("case001").map(parser::parse);
	}
}
