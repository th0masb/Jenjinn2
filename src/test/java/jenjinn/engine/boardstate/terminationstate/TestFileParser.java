/**
 *
 */
package jenjinn.engine.boardstate.terminationstate;

import static xawd.jflow.utilities.CollectionUtil.last;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.base.GameTermination;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.parseutils.BoardParser;

/**
 * @author ThomasB
 *
 */
final class TestFileParser extends AbstractTestFileParser
{

	@Override
	public Arguments parse(String fileName)
	{
		List<String> lines = loadFile(fileName);

		if (lines.size() == 11) {
			BoardState state = BoardParser.parse(take(9, lines));
			boolean hasLegalMoves = Boolean.parseBoolean(lines.get(9).toLowerCase().trim());
			GameTermination expectedTermination = GameTermination.valueOf(last(lines).toUpperCase().trim());
			return Arguments.of(state, hasLegalMoves, expectedTermination);
		}
		else {
			throw new IllegalArgumentException("Bad file format");
		}
	}
}
