/**
 *
 */
package jenjinn.boardstate.legalmoves;

import org.junit.jupiter.params.provider.Arguments;

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

		Vec<String> boardStateAttributes = lines.take(9);
		Vec<String> expectedMoveLines = lines.skip(9)
				.takeWhile(s -> !s.startsWith("---"));
		Vec<String> expectedAttackLines = lines.skipWhile(s -> !s.startsWith("---"))
				.skip(1);

		return Arguments.of(BoardParser.parse(boardStateAttributes),
				parseMoves(expectedMoveLines), parseMoves(expectedAttackLines));
	}
}
