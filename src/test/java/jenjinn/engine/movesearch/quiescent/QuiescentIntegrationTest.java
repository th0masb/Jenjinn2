/**
 *
 */
package jenjinn.engine.movesearch.quiescent;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.misc.Infinity;
import jenjinn.engine.movesearch.QuiescentSearcher;
import jenjinn.engine.stringutils.VisualGridGenerator;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
class QuiescentIntegrationTest
{

	@ParameterizedTest
	@MethodSource
	void test(BoardState root, String result)
	{
		System.out.println(VisualGridGenerator.from(root.getPieceLocations()));
		try {
			final int res = QuiescentSearcher.search(root, Infinity.IC_ALPHA, Infinity.IC_BETA, QuiescentSearcher.DEPTH_CAP);
		}
		catch (final Throwable t) {
			t.printStackTrace();
			fail("Not yet implemented");
		}
	}

	static Flow<Arguments> test()
	{
		return Iterate.over("case004").map(TestFileParser::parse);
	}
}
