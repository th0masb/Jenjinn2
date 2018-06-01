/**
 *
 */
package jenjinn.engine.moves;

import java.util.Iterator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import xawd.jflow.iterators.construction.IterRange;

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
		return IterRange.between(1, 2).mapToObject(i -> "boardEvolutionTestCase" + i).map(TestFileParser::parse);
	}
}
