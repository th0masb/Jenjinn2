/**
 * 
 */
package jenjinn.engine.integrationtests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.movesearch.TreeSearcher;
import jenjinn.engine.pgn.BadPgnException;
import jenjinn.engine.pgn.PgnGameConverter;
import jenjinn.engine.utils.FileUtils;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class MoveSearchIntegrationTest
{
	@ParameterizedTest
	@MethodSource
	void test(String pgn)
	{
		TreeSearcher searcher = new TreeSearcher();
		try {
			List<ChessMove> mvs = PgnGameConverter.parse(pgn);
			BoardState state = StartStateGenerator.createStartBoard();
			Iterate.over(mvs).take(mvs.size()/2).forEach(mv -> mv.makeMove(state));
			searcher.getBestMoveFrom(state, 3000);
		} catch (BadPgnException e) {
			fail("Error in parsing pgn: " + pgn);
		}
	}

	static Stream<Arguments> test()
	{
		Class<?> cls = MoveSearchIntegrationTest.class;
		return FileUtils.loadResourceFromPackageOf(cls, "SemiSlavMeran").limit(10).map(Arguments::of);
	}
}
