/**
 *
 */
package jenjinn.engine.eval.staticexchangeevaluator;

import static java.util.stream.Collectors.toList;
import static jenjinn.engine.parseutils.BoardParseUtils.parseBoard;
import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.utils.FileUtils;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
final class TestFileParser
{
	private TestFileParser()
	{
	}

	static Arguments parse(String filename)
	{
		final Class<?> cls = TestFileParser.class;
		final List<String> lines = FileUtils.loadResourceFromPackageOf(cls, filename)
		.map(String::trim)
		.filter(s -> !(s.isEmpty() || s.startsWith("//")))
		.collect(toList());

		return Arguments.of(parseBoard(take(9, lines)), parseIndividualCases(drop(9, lines)));
	}

	private static List<IndividualStateCase> parseIndividualCases(List<String> caseLines)
	{
		if (caseLines.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return Iterate.over(caseLines).map(IndividualStateCase::from).toList();
	}
}
