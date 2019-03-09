/**
 *
 */
package com.github.maumay.jenjinn.boardstate.terminationstate;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jenjinn.base.GameTermination;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.parseutils.AbstractTestFileParser;
import com.github.maumay.jenjinn.parseutils.BoardParser;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 *
 */
final class TestFileParser extends AbstractTestFileParser
{

	@Override
	public Arguments parse(String fileName)
	{
		Vec<String> lines = loadFile(fileName);

		if (lines.size() == 11) {
			BoardState state = BoardParser.parse(lines.take(9));
			boolean hasLegalMoves = Boolean
					.parseBoolean(lines.get(9).toLowerCase().trim());
			GameTermination expectedTermination = GameTermination
					.valueOf(lines.last().toUpperCase().trim());
			return Arguments.of(state, hasLegalMoves, expectedTermination);
		} else {
			throw new IllegalArgumentException("Bad file format");
		}
	}
}
