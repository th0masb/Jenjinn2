/**
 *
 */
package jenjinn.engine.integrationtests;

import static jenjinn.engine.base.FileUtils.loadResourceFromPackageOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

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
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class PgnGameConversionIntegrationTest extends AbstractBoardStateTest
{
	/**
	 * For some reason when I try and run this test on OpenJDK, Ubuntu 18.04 if I
	 * use more than seven files then it breaks saying it can't load a piece square 
	 * table resource...
	 * 
	 * Works fine on Oracle JDK
	 * 
	 */
	private final int nFiles = 7, nGames = 50;

	@Test
	void test()
	{
		final List<String> files = loadResourceFromPackageOf(getClass(), "integrationtestpgns")
				.sorted()
				.limit(nFiles)
				.collect(Collectors.toList());

		for (final String filename : files) {
			String resourceLoc = FileUtils.absoluteName(getClass(), filename);
			assertNotNull(getClass().getResourceAsStream(resourceLoc), "filename");
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resourceLoc)))) {
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
