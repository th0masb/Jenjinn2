/**
 *
 */
package jenjinn.eval.staticexchangeevaluator;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jflow.vec.Vec;

import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.parseutils.BoardParser;

/**
 * @author ThomasB
 *
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		Vec<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(lines.take(9)),
				parseIndividualCases(lines.skip(9)));
	}

	private Vec<IndividualStateCase> parseIndividualCases(Vec<String> caseLines)
	{
		if (caseLines.size() == 0) {
			throw new IllegalArgumentException();
		}
		return caseLines.map(IndividualStateCase::from);
	}
}
