/**
 *
 */
package com.github.maumay.jenjinn.eval.staticexchangeevaluator;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jenjinn.parseutils.AbstractTestFileParser;
import com.github.maumay.jenjinn.parseutils.BoardParser;
import com.github.maumay.jflow.vec.Vec;

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
				parseIndividualCases(lines.drop(9)));
	}

	private Vec<IndividualStateCase> parseIndividualCases(Vec<String> caseLines)
	{
		if (caseLines.size() == 0) {
			throw new IllegalArgumentException();
		}
		return caseLines.map(IndividualStateCase::from);
	}
}
