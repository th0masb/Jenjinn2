/**
 *
 */
package jenjinn.engine.movesearch.quiescent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static xawd.jflow.utilities.CollectionUtil.sizeOf;
import static xawd.jflow.utilities.CollectionUtil.string;

import java.util.Iterator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.movesearch.QuiescentSearcher;
import jenjinn.engine.utils.IntConstants;
import xawd.jflow.iterators.factories.CycledIteration;
import xawd.jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 *
 */
class QuiescentSearchTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState root, String result)
	{
		final QuiescentSearcher quiescent = new QuiescentSearcher();
		int expectedSignum = result.equals("POSITIVE")? 1 : -1;
		int actualResult = -1;
		try {
			actualResult = quiescent.search(root, IntConstants.INITIAL_ALPHA, IntConstants.INITIAL_BETA, QuiescentSearcher.DEPTH_CAP);
		}
		catch (final Throwable t) {
			t.printStackTrace();
			fail("Error thrown!");
		}
		assertEquals(expectedSignum, Math.signum(actualResult));
	}

	static Iterator<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return IterRange.between(1, 6).mapToObject(i -> "case" + pad(i)).map(parser::parse);
	}

	static String pad(final int caseNumber) {
		final String caseString = string(caseNumber);
		return CycledIteration.of("0")
				.take(3 - sizeOf(caseString))
				.append(caseString)
				.fold("", (a, b) -> a + b);
	}
}
