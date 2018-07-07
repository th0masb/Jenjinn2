/**
 *
 */
package jenjinn.engine.integrationtests;

import static jenjinn.engine.base.FileUtils.loadResourceFromPackageOf;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

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
	private static final int N_GAMES_PER_FILE = 50;

	@Test
	void test()
	{
		final List<String> files = loadResourceFromPackageOf(getClass(), "integrationtestpgns")
				.sorted()
				.collect(Collectors.toList());

		for (final String filename : files) {
			final Stream<String> pgns = loadResourceFromPackageOf(getClass(), filename);
			pgns.limit(N_GAMES_PER_FILE).forEach(pgn -> {
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
		}
	}
}
