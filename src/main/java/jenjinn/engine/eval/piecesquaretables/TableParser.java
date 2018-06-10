/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.regex.Pattern;

import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.utils.FileUtils;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.utilities.MapUtil;
import xawd.jflow.utilities.StringUtils;

/**
 * @author ThomasB
 *
 */
public final class TableParser
{
	private TableParser() {}

	public static PieceSquareTable parseFile(final ChessPiece piece, final String filename)
	{
		return parseFile(piece, TableParser.class, filename);
	}

	public static PieceSquareTable parseFile(final ChessPiece piece, final Class<?> packageProvider, final String filename)
	{
		if (!piece.isWhite()) {
			throw new IllegalArgumentException();
		}
		final List<String> lines = FileUtils.loadResourceFromPackageOf(packageProvider, filename).collect(toList());
		final Pattern numberPattern = Pattern.compile("-?[0-9]+");

		final int[] parseResult = Iterate.reverseOver(lines)
				.map(line -> StringUtils.getAllMatches(line, numberPattern))
				.map(matches -> MapUtil.intMap(Integer::parseInt, matches))
				.flattenToInts(Iterate::reverseOver)
				.toArray();

		return new PieceSquareTable(piece, parseResult);
	}
}
