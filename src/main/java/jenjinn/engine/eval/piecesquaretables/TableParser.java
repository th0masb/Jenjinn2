/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import static java.util.stream.Collectors.toList;
import static xawd.jflow.utilities.MapUtil.intMap;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.List;

import jenjinn.engine.eval.PieceValues;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.utils.FileUtils;
import xawd.jflow.iterators.factories.Iterate;

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

	public static PieceSquareTable parseFile(final ChessPiece piece, final String filename)
	{
		return parseFile(piece, TableParser.class, filename);
	}

	public static PieceSquareTable parseFile(final ChessPiece piece, final Class<?> packageProvider,
			final String filename)
	{
		final String fle = filename;
		if (!piece.isWhite() || !(fle.endsWith("midgame") || fle.endsWith("endgame") || fle.endsWith("testing"))) {
			throw new IllegalArgumentException();
		}

		final PieceValues pvalues = fle.endsWith("midgame") ? PieceValues.MIDGAME
				: fle.endsWith("endgame") ? PieceValues.ENDGAME : PieceValues.TESTING;

		final List<String> lines = FileUtils.loadResourceFromPackageOf(packageProvider, filename).map(String::trim)
				.filter(line -> !line.isEmpty()).collect(toList());

		if (lines.size() == 8) {
			final int[] locationValues = Iterate.reverseOver(lines).map(line -> getAllMatches(line, NUMBER_PATTERN))
					.map(matches -> intMap(Integer::parseInt, matches)).flattenToInts(Iterate::reverseOverInts)
					.toArray();

			return new PieceSquareTable(piece, pvalues.valueOf(piece), locationValues);
		} else {
			throw new IllegalStateException(lines.toString());
		}
	}

	public static void main(final String[] args)
	{
		System.out.println("table-testing".endsWith("testing"));
//		System.out.println(FileUtils.loadResourceFromPackageOf(TableParser.class, "bishop-midgame").collect(toList()));
	}
}
