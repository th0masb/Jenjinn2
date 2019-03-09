/**
 *
 */
package jenjinn.moves;

import java.util.Iterator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jflow.iterators.factories.Iter;
import com.github.maumay.jflow.iterators.factories.Repeatedly;
import com.github.maumay.jflow.utils.Strings;

import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.MoveReversalData;

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
		return Iter.until(40).mapToObject(i -> "case" + pad(i)).map(parser::parse);
	}

	static String pad(int caseNumber)
	{
		String caseString = Strings.str(caseNumber);
		return Repeatedly.cycle("0").take(3 - caseString.length()).append(caseString)
				.fold("", (a, b) -> a + b);
	}
}
