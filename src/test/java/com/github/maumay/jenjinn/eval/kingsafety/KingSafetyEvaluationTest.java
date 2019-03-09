/**
 *
 */
package com.github.maumay.jenjinn.eval.kingsafety;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.eval.KingSafetyEvaluator;
import com.github.maumay.jflow.iterators.EnhancedIterator;
import com.github.maumay.jflow.iterators.factories.Iter;

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

	static EnhancedIterator<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iter.over("case001").map(parser::parse);
	}
}
