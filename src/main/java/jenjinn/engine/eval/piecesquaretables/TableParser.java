/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import static java.util.stream.Collectors.toList;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.CollectionUtil.take;
import static xawd.jflow.utilities.MapUtil.intMap;
import static xawd.jflow.utilities.StringUtils.findFirstMatch;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.List;

import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.utils.FileUtils;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public final class TableParser
{
	private static final String NUMBER_PATTERN = "-?[0-9]+", POSITIVE_INTEGER = "[0-9]+";

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
		final List<String> lines = FileUtils.loadResourceFromPackageOf(packageProvider, filename)
				.map(String::trim)
				.filter(line -> !line.isEmpty())
				.collect(toList());

		if (lines.size() == 9) {
			final int[] locationValues = Iterate.reverseOver(take(8, lines))
					.map(line -> getAllMatches(line, NUMBER_PATTERN))
					.map(matches -> intMap(Integer::parseInt, matches))
					.flattenToInts(Iterate::reverseOverInts)
					.toArray();

			 final int pieceValue = findFirstMatch(tail(lines), POSITIVE_INTEGER)
					 .map(Integer::parseInt)
					 .orElseThrow(IllegalStateException::new);

			 return new PieceSquareTable(piece, pieceValue, locationValues);
		}
		else {
			throw new IllegalStateException(lines.toString());
		}
	}

	public static void main(final String[] args)
	{
		System.out.println(FileUtils.loadResourceFromPackageOf(TableParser.class, "bishop-midgame").collect(toList()));
	}
}
