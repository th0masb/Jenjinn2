/**
 *
 */
package jenjinn.engine.moves;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static xawd.jflow.utilities.CollectionUtil.head;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.HashCache;
import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.parseutils.BoardParser;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	private static final int STARTING_MOVE_COUNT = 10;

	/**
	 * The file is expected to contain one line encoding the move, then nine lines
	 * encoding the start board, then nine lines encoded the expected resulting
	 * board. Blank lines are permitted between. See {@link BoardParser} for more
	 * detail on how the board is encoded.
	 *
	 *
	 * @param fileName
	 *            - name of test case file which must be contained in same package
	 *            as this parser.
	 * @return arguments consisting of (move to test, start board, expected
	 *         resulting board)
	 */
	@Override
	public Arguments parse(String fileName)
	{
		List<String> lines = loadFile(fileName);

		if (lines.size() != 19) {
			throw new IllegalArgumentException();
		}

		ChessMove reconstructedMove = ChessMove.decode(head(lines));
		BoardState startState = BoardParser.parse(lines.subList(1, 10), STARTING_MOVE_COUNT);
		long expectedOldHash = startState.calculateHash();
		BoardState expectedEvolutionResult = BoardParser.parse(lines.subList(10, 19), STARTING_MOVE_COUNT + 1);
		BoardState updatedExpected = insertPreviousHash(expectedEvolutionResult, expectedOldHash);
		return Arguments.of(reconstructedMove, startState, updatedExpected);
	}

	private BoardState insertPreviousHash(BoardState expectedEvolutionResult, long expectedOldHash)
	{
		long[] currentCache = expectedEvolutionResult.getHashCache().getCacheCopy();
		int halfMoveCount = expectedEvolutionResult.getHashCache().getHalfMoveCount();
		assertTrue(halfMoveCount > 0);
		currentCache[(halfMoveCount - 1) % currentCache.length] = expectedOldHash;
		return new BoardState(new HashCache(currentCache, halfMoveCount), expectedEvolutionResult.getPieceLocations(),
				expectedEvolutionResult.getHalfMoveClock(), expectedEvolutionResult.getCastlingStatus(),
				expectedEvolutionResult.getDevelopedPieces(), expectedEvolutionResult.getActiveSide(),
				expectedEvolutionResult.getEnPassantSquare());
	}
}
