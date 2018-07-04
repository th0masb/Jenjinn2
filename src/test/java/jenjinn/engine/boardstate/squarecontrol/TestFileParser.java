/**
 *
 */
package jenjinn.engine.boardstate.squarecontrol;

import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.sizeOf;
import static xawd.jflow.utilities.CollectionUtil.take;
import static xawd.jflow.utilities.Strings.getAllMatches;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.bitboards.BitboardUtils;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.parseutils.BoardParser;
import jenjinn.engine.parseutils.CordParser;
import jenjinn.engine.pgn.CommonRegex;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.iterators.misc.Pair;


/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	public Arguments parse(final String fileName)
	{
		final List<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(take(9, lines)), parseSquaresOfControl(drop(9, lines)));
	}

	private Map<ChessPiece, Long> parseSquaresOfControl(final List<String> squaresOfControl)
	{
		if (sizeOf(squaresOfControl) != 12) {
			throw new IllegalArgumentException(
					"Only passed squares of control for " + sizeOf(squaresOfControl) + " pieces");
		}
		return ChessPieces.iterate()
				.zipWith(squaresOfControl.iterator())
				.toMap(Pair::first, p -> parseSinglePieceSquaresOfControl(p.second()));
	}

	private Long parseSinglePieceSquaresOfControl(final String encoded)
	{
		final String ec = encoded.trim() + " ";
		final String sqrx = CommonRegex.SINGLE_SQUARE, cordrx = CommonRegex.CORD;

		if (ec.matches("none ")) {
			return 0L;
		}
		else if (ec.matches("((" + sqrx +"|" + cordrx + ") +)+")) {
			final Set<BoardSquare> squares = Iterate.over(getAllMatches(ec, sqrx))
			.map(String::toUpperCase)
			.map(BoardSquare::valueOf)
			.toCollection(HashSet::new);

			Iterate.over(getAllMatches(ec, cordrx))
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
