/**
 *
 */
package com.github.maumay.jenjinn.boardstate.legalmoves;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jenjinn.parseutils.AbstractTestFileParser;
import com.github.maumay.jenjinn.parseutils.BoardParser;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		Vec<String> lines = loadFile(fileName);

		Vec<String> boardStateAttributes = lines.take(9);
		Vec<String> expectedMoveLines = lines.drop(9)
				.takeWhile(s -> !s.startsWith("---"));
		Vec<String> expectedAttackLines = lines.dropWhile(s -> !s.startsWith("---"))
				.drop(1);

		return Arguments.of(BoardParser.parse(boardStateAttributes),
				parseMoves(expectedMoveLines), parseMoves(expectedAttackLines));
	}
}
