/**
 * 
 */
package jenjinn.engine.integrationtests;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import jenjinn.engine.base.FileUtils;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.movesearch.TreeSearcher;
import jenjinn.engine.pgn.BadPgnException;
import jenjinn.engine.pgn.PgnGameConverter;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class MoveSearchIntegrationTest
{
	private final int  nGames        = 30;
	private final long timePerSearch = 3000;
	
	@Test
	void test()
	{
		TreeSearcher searcher = new TreeSearcher();
		
		try (BufferedReader reader = FileUtils.loadResource(getClass(), "BishopsOpening")) {
			reader.lines().limit(nGames).forEach(game -> 
			{
				try {
					List<ChessMove> mvs = PgnGameConverter.parse(game);
					BoardState state = StartStateGenerator.createStartBoard();
					Iterate.over(mvs).take(mvs.size()/2).forEach(mv -> mv.makeMove(state));
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
