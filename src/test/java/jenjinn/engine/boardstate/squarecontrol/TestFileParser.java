/**
 *
 */
package jenjinn.engine.boardstate.squarecontrol;

import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.sizeOf;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.bitboards.BitboardUtils;
import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.parseutils.BoardParser;
import jenjinn.engine.parseutils.CordParser;
import jenjinn.engine.pgn.CommonRegex;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.iterators.misc.Pair;
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
		return Arguments.of(BoardParser.parse(take(9, lines)), parseSquaresOfControl(drop(9, lines)));
	}

	private Map<ChessPiece, Long> parseSquaresOfControl(List<String> squaresOfControl)
	{
		if (sizeOf(squaresOfControl) != 12) {
			throw new IllegalArgumentException(
					"Only passed squares of control for " + sizeOf(squaresOfControl) + " pieces");
		}
		return ChessPieces.iterate()
				.zipWith(squaresOfControl.iterator())
				.toMap(Pair::first, p -> parseSinglePieceSquaresOfControl(p.second()));
	}

	private Long parseSinglePieceSquaresOfControl(String encoded)
	{
		String ec = encoded.trim() + " ";
		String sqrx = CommonRegex.SINGLE_SQUARE, cordrx = CommonRegex.CORD;

		if (ec.matches("none ")) {
			return 0L;
		}
		else if (ec.matches("((" + sqrx +"|" + cordrx + ") +)+")) {
			Set<BoardSquare> squares = Strings.allMatches(ec, sqrx)
			.map(String::toUpperCase)
			.map(BoardSquare::valueOf)
			.toCollection(HashSet::new);

			Strings.allMatches(ec, cordrx)
			.map(CordParser::parse)
			.flatten(Iterate::over)
			.forEach(squares::add);

			return BitboardUtils.bitwiseOr(squares);
		}
		else {
			throw new IllegalArgumentException(encoded);
		}
	}
}
