/**
 *
 */
package jenjinn.engine.integrationtests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import jenjinn.engine.base.FileUtils;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.MoveReversalData;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.moves.AbstractBoardStateTest;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.pgn.BadPgnException;
import jenjinn.engine.pgn.PgnGameConverter;
import xawd.jflow.collections.FlowList;
import xawd.jflow.collections.Lists;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class PgnGameConversionIntegrationTest extends AbstractBoardStateTest
{
	/**
	 * How many games from each file we will test.
	 */
	private final int nGames = 50;

	@Test
	void test()
	{
		List<String> files = Lists.copyMutable(FileUtils.cacheResource(getClass(), "integrationtestpgns"));
		Collections.sort(files);

		for (final String filename : files) {
			String resourceLoc = FileUtils.absoluteName(getClass(), filename);
			assertNotNull(getClass().getResourceAsStream(resourceLoc), "filename");
			
			try (BufferedReader reader = FileUtils.loadResource(getClass(), filename)) {
				reader.lines().limit(nGames).forEach(pgn -> {
					try {
						final List<ChessMove> mvs = PgnGameConverter.parse(pgn.trim());
						final BoardState state = StartStateGenerator.createStartBoard();
						final FlowList<MoveReversalData> reversalData = Iterate.over(mvs).map(x -> new MoveReversalData()).toList();
						reversalData.flow().zipWith(mvs).forEach(p -> p.second().makeMove(state, p.first()));
						reversalData.rflow().zipWith(Iterate.reverseOver(mvs)).forEach(p -> p.second().reverseMove(state, p.first()));
						assertBoardStatesAreEqual(StartStateGenerator.createStartBoard(), state);
					} catch (final BadPgnException e) {
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
