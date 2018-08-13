/**
 *
 */
package jenjinn.engine.movesearch.quiescent;

import static xawd.jflow.utilities.CollectionUtil.last;
import static xawd.jflow.utilities.CollectionUtil.string;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.parseutils.BoardParser;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		List<String> lines = loadFile(fileName);

		if (lines.size() == 10) {
			return Arguments.of(BoardParser.parse(take(9, lines)), parseResultLine(last(lines)));
		}
		else {
			throw new IllegalArgumentException(string(lines.size()));
		}
	}

	private String parseResultLine(String tail)
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
