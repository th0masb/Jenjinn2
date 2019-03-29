/**
 *
 */
package com.github.maumay.jenjinn.boardstate.squarecontrol;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.bitboards.Bitboard;
import com.github.maumay.jenjinn.parseutils.AbstractTestFileParser;
import com.github.maumay.jenjinn.parseutils.BoardParser;
import com.github.maumay.jenjinn.parseutils.CordParser;
import com.github.maumay.jenjinn.pgn.CommonRegex;
import com.github.maumay.jenjinn.pieces.ChessPieces;
import com.github.maumay.jenjinn.pieces.Piece;
import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.utils.Tup;
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
				parseSquaresOfControl(lines.drop(9)));
	}

	private Map<Piece, Long> parseSquaresOfControl(Vec<String> squaresOfControl)
	{
		if (squaresOfControl.size() != 12) {
			throw new IllegalArgumentException("Only passed squares of control for "
					+ squaresOfControl.size() + " pieces");
		}
		return ChessPieces.ALL.iter().zip(squaresOfControl.iterator()).toMap(Tup::_1,
				p -> parseSinglePieceSquaresOfControl(p._2));
	}

	private Long parseSinglePieceSquaresOfControl(String encoded)
	{
		String ec = encoded.trim() + " ";
		String sqrx = CommonRegex.SINGLE_SQUARE, cordrx = CommonRegex.CORD;

		if (ec.matches("none ")) {
			return 0L;
		} else if (ec.matches("((" + sqrx + "|" + cordrx + ") +)+")) {
			Set<Square> squares = Strings.allMatches(ec, sqrx).map(String::toUpperCase)
					.map(Square::valueOf).toCollection(HashSet::new);

			Strings.allMatches(ec, cordrx).map(CordParser::parse).flatMap(Vec::iter)
					.forEach(squares::add);

			return Bitboard.fold(squares);
		} else {
			throw new IllegalArgumentException(encoded);
		}
	}
}
