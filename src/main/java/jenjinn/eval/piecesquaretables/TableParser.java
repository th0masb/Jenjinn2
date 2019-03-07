/**
 *
 */
package jenjinn.eval.piecesquaretables;


import jenjinn.base.FileUtils;
import jenjinn.eval.PieceValues;
import jenjinn.pieces.Piece;
import jflow.iterators.factories.Iter;
import jflow.iterators.misc.ArrayUtils;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

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

	public static PieceSquareTable parseFile(Piece piece, String filename)
	{
		return parseFile(piece, TableParser.class, filename);
	}

	public static PieceSquareTable parseFile(Piece piece, Class<?> packageProvider, String filename)
	{
		String fle = filename;
		if (!piece.isWhite() || !(fle.endsWith("midgame") || fle.endsWith("endgame") || fle.endsWith("testing"))) {
			throw new IllegalArgumentException();
		}

		PieceValues pvalues = fle.endsWith("midgame") ? PieceValues.MIDGAME
				: fle.endsWith("endgame") ? PieceValues.ENDGAME : PieceValues.TESTING;

		Seq<String> lines = FileUtils.cacheResource(packageProvider, filename);

		if (lines.size() == 8) {
			int[] locationValues = lines.rflow()
					.map(line -> Strings.allMatches(line, NUMBER_PATTERN).toList())
					.map(matches -> ArrayUtils.intMap(Integer::parseInt, matches))
					.flatMapToInt(Iter::overReversedInts)
					.toArray();

			return new PieceSquareTable(piece, pvalues.valueOf(piece), locationValues);
		} else {
			throw new IllegalStateException(lines.toString());
		}
	}
}
