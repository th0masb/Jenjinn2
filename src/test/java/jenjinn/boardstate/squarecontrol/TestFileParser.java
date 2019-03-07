/**
 *
 */
package jenjinn.boardstate.squarecontrol;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.base.Square;
import jenjinn.bitboards.BitboardUtils;
import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.parseutils.BoardParser;
import jenjinn.parseutils.CordParser;
import jenjinn.pgn.CommonRegex;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;
import jflow.iterators.misc.Pair;
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
		return Arguments.of(BoardParser.parse(lines.take(9)), parseSquaresOfControl(lines.drop(9)));
	}

	private Map<Piece, Long> parseSquaresOfControl(Seq<String> squaresOfControl)
	{
		if (squaresOfControl.size() != 12) {
			throw new IllegalArgumentException(
					"Only passed squares of control for " + squaresOfControl.size() + " pieces");
		}
		return ChessPieces.ALL.flow()
				.zipWith(squaresOfControl.iterator())
				.toMap(Pair::_1, p -> parseSinglePieceSquaresOfControl(p._2));
	}

	private Long parseSinglePieceSquaresOfControl(String encoded)
	{
		String ec = encoded.trim() + " ";
		String sqrx = CommonRegex.SINGLE_SQUARE, cordrx = CommonRegex.CORD;

		if (ec.matches("none ")) {
			return 0L;
		}
		else if (ec.matches("((" + sqrx +"|" + cordrx + ") +)+")) {
			Set<Square> squares = Strings.allMatches(ec, sqrx)
			.map(String::toUpperCase)
			.map(Square::valueOf)
			.toCollection(HashSet::new);

			Strings.allMatches(ec, cordrx)
			.map(CordParser::parse)
			.flatMap(Seq::flow)
			.forEach(squares::add);

			return BitboardUtils.bitwiseOr(squares);
		}
		else {
			throw new IllegalArgumentException(encoded);
		}
	}
}
