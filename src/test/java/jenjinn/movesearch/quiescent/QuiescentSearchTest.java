/**
 *
 */
package jenjinn.movesearch.quiescent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.boardstate.BoardState;
import jenjinn.movesearch.QuiescentSearcher;
import jflow.iterators.factories.IterRange;
import jflow.iterators.factories.Repeatedly;
import jflow.iterators.misc.Strings;

/**
 * @author ThomasB
 *
 */
class QuiescentSearchTest
{
	@Disabled
	@ParameterizedTest
	@MethodSource
	void test(BoardState root, String result)
	{
		QuiescentSearcher quiescent = new QuiescentSearcher();
		int expectedSignum = result.equals("POSITIVE") ? 1 : -1;
		int actualResult = -1;
		try {
			actualResult = quiescent.search(root);
		} catch (final Throwable t) {
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

	static String pad(int caseNumber)
	{
		String caseString = Strings.$(caseNumber);
		return Repeatedly.cycle("0")
				.take(3 - caseString.length())
				.append(caseString)
				.fold("", (a, b) -> a + b);
	}
}
