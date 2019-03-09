/**
 *
 */
package jenjinn.boardstate.pinnedpieces;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.Square;
import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.parseutils.BoardParser;
import jenjinn.pgn.CommonRegex;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		Vec<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(lines.take(9)),
				parseSquareVecuence(lines.last()));
	}

	private Set<Square> parseSquareVecuence(String encodedVecuence)
	{
		String ec = encodedVecuence.trim() + " ", sq = CommonRegex.SINGLE_SQUARE;
		if (ec.matches("none ")) {
			return Collections.emptySet();
		} else if (ec.matches("(" + sq + " +)+")) {
			return Strings.allMatches(ec, sq).map(String::toUpperCase)
					.map(Square::valueOf).toSet();
		} else {
			throw new IllegalArgumentException(encodedVecuence);
		}
	}
}
