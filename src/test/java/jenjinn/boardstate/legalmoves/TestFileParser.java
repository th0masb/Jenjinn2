/**
 *
 */
package jenjinn.boardstate.legalmoves;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.parseutils.BoardParser;
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

		Seq<String> boardStateAttributes = lines.take(9);
		Seq<String> expectedMoveLines = lines.drop(9).takeWhile(s -> !s.startsWith("---"));
		Seq<String> expectedAttackLines = lines.dropWhile(s -> !s.startsWith("---")).drop(1);

		return Arguments.of(
				BoardParser.parse(boardStateAttributes),
				parseMoves(expectedMoveLines),
				parseMoves(expectedAttackLines));
	}
}
