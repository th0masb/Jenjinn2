/**
 *
 */
package jenjinn.engine.movesearch.quiescent;

import static java.util.stream.Collectors.toList;
import static xawd.jflow.utilities.CollectionUtil.string;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.parseutils.BoardParseUtils;
import jenjinn.engine.utils.FileUtils;

/**
 * @author ThomasB
 */
class TestFileParser
{
	private TestFileParser()
	{
	}

	static Arguments parse(String fileName)
	{
		final Class<TestFileParser> cls = TestFileParser.class;
		final List<String> lines = FileUtils.loadResourceFromPackageOf(cls, fileName)
				.map(String::trim)
				.filter(s -> !s.startsWith("//") && !s.isEmpty())
				.collect(toList());

		if (lines.size() == 10) {
			return Arguments.of(BoardParseUtils.parseBoard(take(9, lines)), parseResultLine(tail(lines)));
		}
		else {
			throw new IllegalArgumentException(string(lines.size()));
		}
	}

	private static String parseResultLine(String tail)
	{

		return "";
	}
}
