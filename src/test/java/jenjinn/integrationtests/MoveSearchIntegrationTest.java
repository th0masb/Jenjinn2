/**
 * 
 */
package jenjinn.integrationtests;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import jenjinn.base.FileUtils;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.StartStateGenerator;
import jenjinn.moves.ChessMove;
import jenjinn.movesearch.TreeSearcher;
import jenjinn.pgn.BadPgnException;
import jenjinn.pgn.PgnGameConverter;
import jflow.seq.Seq;

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
					Seq<ChessMove> mvs = PgnGameConverter.parse(game);
					BoardState state = StartStateGenerator.createStartBoard();
					mvs.flow().take(mvs.size()/2).forEach(mv -> mv.makeMove(state));
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
