/**
 *
 */
package jenjinn.boardstate.pinnedpieces;


import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.base.Square;
import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.parseutils.BoardParser;
import jenjinn.pgn.CommonRegex;
import jflow.iterators.misc.Strings;
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
		return Arguments.of(BoardParser.parse(lines.take(9)), parseSquareSequence(lines.last()));
	}

	private Set<Square> parseSquareSequence(String encodedSequence)
	{
		String ec = encodedSequence.trim() + " ", sq = CommonRegex.SINGLE_SQUARE;
		if (ec.matches("none ")) {
			return Collections.emptySet();
		}
		else if (ec.matches("(" + sq + " +)+")) {
			return Strings.allMatches(ec, sq)
					.map(String::toUpperCase)
					.map(Square::valueOf)
					.toSet();
		}
		else {
			throw new IllegalArgumentException(encodedSequence);
		}
	}
}
