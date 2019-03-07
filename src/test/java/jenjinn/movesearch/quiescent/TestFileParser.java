/**
 *
 */
package jenjinn.movesearch.quiescent;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.parseutils.BoardParser;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		Seq<String> lines = loadFile(fileName);

		if (lines.size() == 10) {
			return Arguments.of(BoardParser.parse(lines.take(9)), parseResultLine(lines.last()));
		}
		else {
			throw new IllegalArgumentException(Strings.$(lines.size()));
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
