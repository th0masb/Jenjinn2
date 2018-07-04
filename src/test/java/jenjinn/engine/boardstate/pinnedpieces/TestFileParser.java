/**
 *
 */
package jenjinn.engine.boardstate.pinnedpieces;

import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.CollectionUtil.take;
import static xawd.jflow.utilities.Strings.getAllMatches;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.parseutils.BoardParser;
import jenjinn.engine.pgn.CommonRegex;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	public Arguments parse(final String fileName)
	{
		final List<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(take(9, lines)), parseSquareSequence(tail(lines)));
	}

	private Set<BoardSquare> parseSquareSequence(final String encodedSequence)
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
