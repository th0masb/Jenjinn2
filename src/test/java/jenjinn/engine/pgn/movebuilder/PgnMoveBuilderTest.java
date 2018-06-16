/**
 *
 */
package jenjinn.engine.pgn.movebuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.pgn.BadPgnException;
import jenjinn.engine.pgn.PgnMoveBuilder;
import xawd.jflow.iterators.Flow;

/**
 * @author t
 */
class PgnMoveBuilderTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, String encodedMove, ChessMove expectedOutput)
	{
		try {
			assertEquals(expectedOutput, PgnMoveBuilder.convertPgnCommand(state, encodedMove));
		} catch (final BadPgnException e) {
			fail(e.getMessage());
		}
	}

	static Flow<Arguments> test()
	{
		throw new RuntimeException();
	}
}
