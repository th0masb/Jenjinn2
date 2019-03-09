/**
 *
 */
package com.github.maumay.jenjinn.boardstate.pinnedpieces;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.parseutils.AbstractTestFileParser;
import com.github.maumay.jenjinn.parseutils.BoardParser;
import com.github.maumay.jenjinn.pgn.CommonRegex;
import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.vec.Vec;

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
