/**
 *
 */
package jenjinn.eval.piecesquaretables;

import com.github.maumay.jflow.iterators.factories.Iter;
import com.github.maumay.jflow.utils.ArrayUtils;
import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.FileUtils;
import jenjinn.eval.PieceValues;
import jenjinn.pieces.Piece;

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

	public static PieceSquareTable parseFile(Piece piece, Class<?> packageProvider,
			String filename)
	{
		String fle = filename;
		if (!piece.isWhite() || !(fle.endsWith("midgame") || fle.endsWith("endgame")
				|| fle.endsWith("testing"))) {
			throw new IllegalArgumentException();
		}

		PieceValues pvalues = fle.endsWith("midgame") ? PieceValues.MIDGAME
				: fle.endsWith("endgame") ? PieceValues.ENDGAME : PieceValues.TESTING;

		Vec<String> lines = FileUtils.cacheResource(packageProvider, filename);

		if (lines.size() == 8) {
			int[] locationValues = lines.revIter()
					.map(line -> Strings.allMatches(line, NUMBER_PATTERN).toList())
					.map(matches -> ArrayUtils.intMap(Integer::parseInt, matches))
					.flatMapToInt(Iter::reverseInts).toArray();

			return new PieceSquareTable(piece, pvalues.valueOf(piece), locationValues);
		} else {
			throw new IllegalStateException(lines.toString());
		}
	}
}
