/**
 *
 */
package com.github.maumay.jenjinn.integrationtests;

import static java.util.Comparator.naturalOrder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.maumay.jenjinn.base.FileUtils;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.MoveReversalData;
import com.github.maumay.jenjinn.boardstate.StartStateGenerator;
import com.github.maumay.jenjinn.moves.AbstractBoardStateTest;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jenjinn.pgn.BadPgnException;
import com.github.maumay.jenjinn.pgn.PgnGameConverter;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 */
class PgnGameConversionIntegrationTest extends AbstractBoardStateTest
{
	/**
	 * How many games from each file we will test.
	 */
	private final int nGames = 100;

	@Test
	void test()
	{
		Vec<String> files = FileUtils.cacheResource(getClass(), "integrationtestpgns")
				.sorted(naturalOrder());

		for (String filename : files) {
			String resourceLoc = FileUtils.absoluteName(getClass(), filename);
			assertNotNull(getClass().getResourceAsStream(resourceLoc), "filename");

			try (BufferedReader reader = FileUtils.loadResource(getClass(), filename)) {
				reader.lines().limit(nGames).forEach(pgn -> {
					try {
						Vec<ChessMove> mvs = PgnGameConverter.parse(pgn.trim());
						BoardState state = StartStateGenerator.createStartBoard();
						Vec<MoveReversalData> reversalData = mvs
								.map(x -> new MoveReversalData());
						reversalData.iter().zipWith(mvs)
								.forEach(p -> p._2.makeMove(state, p._1));
						reversalData.revIter().zipWith(mvs.revIter())
								.forEach(p -> p._2.reverseMove(state, p._1));
						assertBoardStatesAreEqual(StartStateGenerator.createStartBoard(),
								state);
					} catch (BadPgnException e) {
						e.printStackTrace();
						fail("Pgn: " + pgn + "\n" + filename);
					}
				});

			} catch (IOException e1) {
				e1.printStackTrace();
				fail();
			}
		}
	}
}
