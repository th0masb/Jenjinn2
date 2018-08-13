/**
 *
 */
package jenjinn.engine.eval.staticexchangeevaluator;

import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.parseutils.BoardParser;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		List<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(take(9, lines)), parseIndividualCases(drop(9, lines)));
	}

	private List<IndividualStateCase> parseIndividualCases(List<String> caseLines)
	{
		if (caseLines.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return Iterate.over(caseLines).map(IndividualStateCase::from).toList();
	}
}
