/**
 *
 */
package jenjinn.engine.boardstate.pinnedpieces;

import static xawd.jflow.utilities.CollectionUtil.last;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.parseutils.BoardParser;
import jenjinn.engine.pgn.CommonRegex;
import xawd.jflow.utilities.Strings;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		List<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(take(9, lines)), parseSquareSequence(last(lines)));
	}

	private Set<BoardSquare> parseSquareSequence(String encodedSequence)
	{
		String ec = encodedSequence.trim() + " ", sq = CommonRegex.SINGLE_SQUARE;
		if (ec.matches("none ")) {
			return Collections.emptySet();
		}
		else if (ec.matches("(" + sq + " +)+")) {
			return Strings.allMatches(ec, sq)
					.map(String::toUpperCase)
					.map(BoardSquare::valueOf)
					.toSet();
		}
		else {
			throw new IllegalArgumentException(encodedSequence);
		}
	}
}
