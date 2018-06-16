/**
 *
 */
package jenjinn.engine.boardstate.legalmoves;

import static java.util.stream.Collectors.toList;
import static jenjinn.engine.parseutils.BoardParseUtils.parseBoard;
import static jenjinn.engine.utils.FileUtils.loadResourceFromPackageOf;
import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.parseutils.CommonRegex;
import jenjinn.engine.parseutils.ShorthandMoveParser;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
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

		final List<String> boardStateAttributes = Iterate.over(lines).take(9).toList();
		final List<String> expectedMoveLines = Iterate.over(lines)
				.drop(9).takeWhile(s -> !s.startsWith("---")).toList();
		final List<String> expectedAttackLines = Iterate.over(lines)
				.dropWhile(s -> !s.startsWith("---")).drop(1).toList();

		return Arguments.of(
				parseBoard(boardStateAttributes),
				parseMoves(expectedMoveLines),
				parseMoves(expectedAttackLines));
	}

	private static Set<ChessMove> parseMoves(final List<String> lines)
	{
		if (lines.isEmpty()) {
			throw new IllegalArgumentException();
		}
		else if (head(lines).trim().toLowerCase().matches("none")) {
			return Collections.emptySet();
		}
		else {
			final String mv = CommonRegex.SHORTHAND_MOVE;
			return Iterate.over(lines)
					.flatten(line -> Iterate.over(getAllMatches(line, mv)))
					.flatten(shortmv -> Iterate.over(ShorthandMoveParser.parse(shortmv)))
					.toSet();
		}
	}
}
