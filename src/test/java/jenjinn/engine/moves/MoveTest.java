/**
 *
 */
package jenjinn.engine.moves;

import static xawd.jflow.utilities.CollectionUtil.string;

import java.util.Iterator;
import java.util.function.IntFunction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import xawd.jflow.iterators.factories.CycledIteration;
import xawd.jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 *
 */
class MoveTest extends AbstractBoardStateTest
{
	@ParameterizedTest
	@MethodSource
	void test(final ChessMove moveToTest, final BoardState startState, final BoardState expected)
	{
		final BoardState startCopy = startState.copy();
		final DataForReversingMove reversalData = new DataForReversingMove();

		moveToTest.makeMove(startState, reversalData);
		assertBoardStatesAreEqual(expected, startState);
		moveToTest.reverseMove(startState, reversalData);
		assertBoardStatesAreEqual(startCopy, startState);
	}

	static Iterator<Arguments> test()
	{
		final IntFunction<String> indexFormatter = i -> "testCase" + CycledIteration.of("9")
		.take(i / 10).reduce("", (a, b) -> a + b) + string(i % 10);
		return IterRange.to(36).mapToObject(indexFormatter).map(TestFileParser::parse);
	}
}
