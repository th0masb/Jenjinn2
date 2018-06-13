/**
 *
 */
package jenjinn.engine.boardstate.legalmoves;

import static java.util.stream.Collectors.toList;
import static jenjinn.engine.utils.FileUtils.loadResourceFromPackageOf;
import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.take;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.parseutils.BoardParseUtils;
import jenjinn.engine.parseutils.CommonRegex;
import jenjinn.engine.parseutils.ShorthandMoveParser;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
public final class TestFileParser
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

		return Arguments.of(BoardParseUtils.parseBoard(take(9, lines)), parseMoves(drop(9, lines)));
	}

	private static Set<ChessMove> parseMoves(final List<String> lines)
	{
		final String mv = CommonRegex.SHORTHAND_MOVE;
		return Iterate.over(lines)
				.flatten(line -> Iterate.over(getAllMatches(line, mv)))
				.flatten(shortmv -> Iterate.over(ShorthandMoveParser.parse(shortmv)))
				.toSet();
	}
}
