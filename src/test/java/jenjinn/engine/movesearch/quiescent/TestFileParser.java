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

import jenjinn.engine.parseutils.BoardParser;
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
			return Arguments.of(BoardParser.parse(take(9, lines)), parseResultLine(tail(lines)));
		}
		else {
			throw new IllegalArgumentException(string(lines.size()));
		}
	}

	private static String parseResultLine(String tail)
	{
		String trimmed = tail.trim().toUpperCase();
		if (trimmed.equals("POSITIVE") || trimmed.equals("NEGATIVE")) {
			return trimmed;
		}
		else {
			throw new IllegalArgumentException(tail);
		}
	}
}
