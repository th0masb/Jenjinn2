/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import static java.util.stream.Collectors.toList;
import static xawd.jflow.utilities.MapUtil.intMap;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.List;
import java.util.regex.Pattern;

import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.utils.FileUtils;
import xawd.jflow.iterators.construction.IterRange;
import xawd.jflow.iterators.construction.ReverseIterate;

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
		final List<String> lines = FileUtils.loadResourceFromPackageOf(packageProvider, filename).collect(toList());
		final Pattern numberPattern = Pattern.compile("-*[0-9]+");

		final int[] parseResult = ReverseIterate.over(lines)
				.map(line -> getAllMatches(line, numberPattern))
				.map(matches -> intMap(Integer::parseInt, matches))
				.flattenToInts(ReverseIterate::over)
				.toArray();

		return new PieceSquareTable(piece, piece.isWhite()? parseResult : invertValues(parseResult));
	}

	/**
	 * The corresponding piece square table for black pieces is horizontally symmetric to the one for
	 * white pieces.
	 */
	static int[] invertValues(final int[] whiteValues)
	{
		return IterRange.to(64).map(i -> -whiteValues[63 - 1 - 8*(i/8) - (7 - (i % 8))]).toArray();
	}
}
