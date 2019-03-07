/**
 *
 */
package jenjinn.moves;

import java.util.Iterator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.MoveReversalData;
import jflow.iterators.factories.IterRange;
import jflow.iterators.factories.Repeatedly;
import jflow.iterators.misc.Strings;

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
		String caseString = Strings.$(caseNumber);
		return Repeatedly.cycle("0")
				.take(3 - caseString.length())
				.append(caseString)
				.fold("", (a, b) -> a + b);
	}
}
