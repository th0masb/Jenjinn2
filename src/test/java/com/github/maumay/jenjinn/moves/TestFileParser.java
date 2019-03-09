/**
 *
 */
package com.github.maumay.jenjinn.moves;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.HashCache;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jenjinn.parseutils.AbstractTestFileParser;
import com.github.maumay.jenjinn.parseutils.BoardParser;
import com.github.maumay.jflow.vec.Vec;

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
	 * @param fileName - name of test case file which must be contained in same
	 *                 package as this parser.
	 * @return arguments consisting of (move to test, start board, expected
	 *         resulting board)
	 */
	@Override
	public Arguments parse(String fileName)
	{
		Vec<String> lines = loadFile(fileName);

		if (lines.size() != 19) {
			throw new IllegalArgumentException();
		}

		ChessMove reconstructedMove = ChessMove.decode(lines.head());
		BoardState startState = BoardParser.parse(lines.skip(1).take(9),
				STARTING_MOVE_COUNT);
		long expectedOldHash = startState.calculateHash();
		BoardState expectedEvolutionResult = BoardParser.parse(lines.skip(10).take(9),
				STARTING_MOVE_COUNT + 1);
		BoardState updatedExpected = insertPreviousHash(expectedEvolutionResult,
				expectedOldHash);
		return Arguments.of(reconstructedMove, startState, updatedExpected);
	}

	private BoardState insertPreviousHash(BoardState expectedEvolutionResult,
			long expectedOldHash)
	{
		long[] currentCache = expectedEvolutionResult.getHashCache().getCacheCopy();
		int halfMoveCount = expectedEvolutionResult.getHashCache().getHalfMoveCount();
		assertTrue(halfMoveCount > 0);
		currentCache[(halfMoveCount - 1) % currentCache.length] = expectedOldHash;
		return new BoardState(new HashCache(currentCache, halfMoveCount),
				expectedEvolutionResult.getPieceLocations(),
				expectedEvolutionResult.getHalfMoveClock(),
				expectedEvolutionResult.getCastlingStatus(),
				expectedEvolutionResult.getDevelopedPieces(),
				expectedEvolutionResult.getActiveSide(),
				expectedEvolutionResult.getEnPassantSquare());
	}
}
