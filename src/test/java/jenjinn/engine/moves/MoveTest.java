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
import xawd.jflow.iterators.factories.IterRange;
import xawd.jflow.iterators.factories.Repeatedly;

/**
 * @author ThomasB
 *
 */
class MoveTest extends AbstractBoardStateTest
{
	@ParameterizedTest
	@MethodSource
	void test(ChessMove moveToTest, BoardState startState, BoardState expected)
	{
		BoardState startCopy = startState.copy();
		MoveReversalData reversalData = new MoveReversalData();

		moveToTest.makeMove(startState, reversalData);
		assertBoardStatesAreEqual(expected, startState);
		moveToTest.reverseMove(startState, reversalData);
		assertBoardStatesAreEqual(startCopy, startState);
	}

	static Iterator<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return IterRange.to(40).mapToObject(i -> "case" + pad(i)).map(parser::parse);
	}

	static String pad(int caseNumber) {
		String caseString = string(caseNumber);
		return Repeatedly.cycle("0")
				.take(3 - sizeOf(caseString))
				.append(caseString)
				.fold("", (a, b) -> a + b);
	}
}
