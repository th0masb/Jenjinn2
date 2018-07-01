/**
 *
 */
package jenjinn.engine.boardstate.pinnedpieces;

import static java.util.stream.Collectors.toList;
import static jenjinn.engine.parseutils.BoardParseUtils.parseBoard;
import static jenjinn.engine.utils.FileUtils.loadResourceFromPackageOf;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.CollectionUtil.take;
import static xawd.jflow.utilities.Strings.getAllMatches;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.parseutils.CommonRegex;
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

		return Arguments.of(parseBoard(take(9, lines)), parseSquareSequence(tail(lines)));
	}

	private static Set<BoardSquare> parseSquareSequence(final String encodedSequence)
	{
		final String ec = encodedSequence.trim() + " ", sq = CommonRegex.SINGLE_SQUARE;
		if (ec.matches("none ")) {
			return Collections.emptySet();
		}
		else if (ec.matches("(" + sq + " +)+")) {
			return Iterate.over(getAllMatches(ec, sq))
					.map(String::toUpperCase)
					.map(BoardSquare::valueOf)
					.toSet();
		}
		else {
			throw new IllegalArgumentException(encodedSequence);
		}
	}
}
