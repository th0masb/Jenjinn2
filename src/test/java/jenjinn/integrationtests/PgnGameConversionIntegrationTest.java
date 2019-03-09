/**
 *
 */
package jenjinn.integrationtests;

import static java.util.Comparator.naturalOrder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.FileUtils;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.MoveReversalData;
import jenjinn.boardstate.StartStateGenerator;
import jenjinn.moves.AbstractBoardStateTest;
import jenjinn.moves.ChessMove;
import jenjinn.pgn.BadPgnException;
import jenjinn.pgn.PgnGameConverter;

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
