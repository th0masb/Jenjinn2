/**
 *
 */
package jenjinn.engine.boardstate.terminationstate;

import static java.util.stream.Collectors.toList;
import static jenjinn.engine.utils.FileUtils.loadResourceFromPackageOf;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.base.GameTermination;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.parseutils.BoardParser;

/**
 * @author ThomasB
 *
 */
final class TestFileParser
{
	private TestFileParser()
	{
	}

	public static Arguments parse(final String fileName)
	{
		final List<String> lines = loadResourceFromPackageOf(TestFileParser.class, fileName)
				.map(String::trim)
				.filter(s -> !s.isEmpty() && !s.startsWith("//"))
				.collect(toList());

		if (lines.size() == 11) {
			final BoardState state = BoardParser.parse(take(9, lines));
			final boolean hasLegalMoves = Boolean.parseBoolean(lines.get(9).toLowerCase().trim());
			final GameTermination expectedTermination = GameTermination.valueOf(tail(lines).toUpperCase().trim());
			return Arguments.of(state, hasLegalMoves, expectedTermination);
		}
		else {
			throw new IllegalArgumentException("Bad file format");
		}
	}
}
