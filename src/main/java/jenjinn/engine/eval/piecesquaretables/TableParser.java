/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import static xawd.jflow.utilities.Strings.allMatches;

import java.util.List;

import jenjinn.engine.base.FileUtils;
import jenjinn.engine.eval.PieceValues;
import jenjinn.engine.pieces.ChessPiece;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.utilities.MapUtil;

/**
 * @author ThomasB
 *
 */
public final class TableParser
{
	private static final String NUMBER_PATTERN = "-?[0-9]+";

	private TableParser()
	{
	}

	public static PieceSquareTable parseFile(ChessPiece piece, String filename)
	{
		return parseFile(piece, TableParser.class, filename);
	}

	public static PieceSquareTable parseFile(ChessPiece piece, Class<?> packageProvider, String filename)
	{
		String fle = filename;
		if (!piece.isWhite() || !(fle.endsWith("midgame") || fle.endsWith("endgame") || fle.endsWith("testing"))) {
			throw new IllegalArgumentException();
		}

		PieceValues pvalues = fle.endsWith("midgame") ? PieceValues.MIDGAME
				: fle.endsWith("endgame") ? PieceValues.ENDGAME : PieceValues.TESTING;

		List<String> lines = FileUtils.cacheResource(packageProvider, filename);

		if (lines.size() == 8) {
			int[] locationValues = Iterate.overReversed(lines)
					.map(line -> allMatches(line, NUMBER_PATTERN).toList())
					.map(matches -> MapUtil.intMap(Integer::parseInt, matches))
					.flattenToInts(Iterate::overReversedInts)
					.toArray();

			return new PieceSquareTable(piece, pvalues.valueOf(piece), locationValues);
		} else {
			throw new IllegalStateException(lines.toString());
		}
	}
}
