/**
 *
 */
package jenjinn.engine.moves;

import static xawd.jflow.utilities.CollectionUtil.sizeOf;
import static xawd.jflow.utilities.CollectionUtil.string;

import java.util.Iterator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.MoveReversalData;
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
		final MoveReversalData reversalData = new MoveReversalData();

		moveToTest.makeMove(startState, reversalData);
		assertBoardStatesAreEqual(expected, startState);
		moveToTest.reverseMove(startState, reversalData);
		assertBoardStatesAreEqual(startCopy, startState);
	}

	static Iterator<Arguments> test()
	{
		return IterRange.to(40).mapToObject(i -> "case" + pad(i)).map(TestFileParser::parse);
	}

	static String pad(final int caseNumber) {
		final String caseString = string(caseNumber);
		return CycledIteration.of("0")
				.take(3 - sizeOf(caseString))
				.append(caseString)
				.fold("", (a, b) -> a + b);
	}
}
