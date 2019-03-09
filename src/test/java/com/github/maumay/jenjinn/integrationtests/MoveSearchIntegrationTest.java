/**
 * 
 */
package com.github.maumay.jenjinn.integrationtests;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.maumay.jenjinn.base.FileUtils;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.StartStateGenerator;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jenjinn.movesearch.TreeSearcher;
import com.github.maumay.jenjinn.pgn.BadPgnException;
import com.github.maumay.jenjinn.pgn.PgnGameConverter;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 */
class MoveSearchIntegrationTest
{
	private final int nGames = 30;
	private final long timePerSearch = 3000;

	@Test
	void test()
	{
		TreeSearcher searcher = new TreeSearcher();

		try (BufferedReader reader = FileUtils.loadResource(getClass(),
				"BishopsOpening")) {
			reader.lines().limit(nGames).forEach(game -> {
				try {
					Vec<ChessMove> mvs = PgnGameConverter.parse(game);
					BoardState state = StartStateGenerator.createStartBoard();
					mvs.iter().take(mvs.size() / 2).forEach(mv -> mv.makeMove(state));
					searcher.getBestMoveFrom(state, timePerSearch);
				} catch (BadPgnException e) {
					fail("Error in parsing pgn: " + game);
				}
			});

		} catch (IOException e1) {
			e1.printStackTrace();
			fail();
		}
	}
}
