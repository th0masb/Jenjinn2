/**
 *
 */
package jenjinn.eval.staticexchangeevaluator;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.parseutils.BoardParser;
import jflow.seq.Seq;

/**
 * @author ThomasB
 *
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		Seq<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(lines.take(9)), parseIndividualCases(lines.drop(9)));
	}

	private Seq<IndividualStateCase> parseIndividualCases(Seq<String> caseLines)
	{
		if (caseLines.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return caseLines.map(IndividualStateCase::from);
	}
}
