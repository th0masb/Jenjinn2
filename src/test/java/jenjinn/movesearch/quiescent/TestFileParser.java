/**
 *
 */
package jenjinn.movesearch.quiescent;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.vec.Vec;

import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.parseutils.BoardParser;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		Vec<String> lines = loadFile(fileName);

		if (lines.size() == 10) {
			return Arguments.of(BoardParser.parse(lines.take(9)),
					parseResultLine(lines.last()));
		} else {
			throw new IllegalArgumentException(Strings.str(lines.size()));
		}
	}

	private String parseResultLine(String tail)
	{
		String trimmed = tail.trim().toUpperCase();
		if (trimmed.equals("POSITIVE") || trimmed.equals("NEGATIVE")) {
			return trimmed;
		} else {
			throw new IllegalArgumentException(tail);
		}
	}
}
