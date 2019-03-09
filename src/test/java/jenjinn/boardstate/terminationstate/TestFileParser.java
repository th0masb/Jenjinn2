/**
 *
 */
package jenjinn.boardstate.terminationstate;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.GameTermination;
import jenjinn.boardstate.BoardState;
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

		if (lines.size() == 11) {
			BoardState state = BoardParser.parse(lines.take(9));
			boolean hasLegalMoves = Boolean
					.parseBoolean(lines.get(9).toLowerCase().trim());
			GameTermination expectedTermination = GameTermination
					.valueOf(lines.last().toUpperCase().trim());
			return Arguments.of(state, hasLegalMoves, expectedTermination);
		} else {
			throw new IllegalArgumentException("Bad file format");
		}
	}
}
