/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import static java.util.stream.Collectors.toList;
import static xawd.jflow.utilities.MapUtil.intMap;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import jenjinn.engine.utils.FileUtils;
import xawd.jflow.iterators.construction.ReverseIterate;

/**
 * @author ThomasB
 *
 */
public final class TableParser
{
	private TableParser() {}

	public static int[] parseFile(final String filename)
	{
		final List<String> lines = FileUtils.loadResourceFromPackageOf(TableParser.class, filename).collect(toList());
		final Pattern numberPattern = Pattern.compile("-*[0-9]+");

		final int[] parseResult = ReverseIterate.over(lines)
				.map(line -> getAllMatches(line, numberPattern))
				.map(matches -> intMap(Integer::parseInt, matches))
				.flattenToInts(ReverseIterate::over)
				.toArray();

		if (parseResult.length == 64) {
			return parseResult;
		}
		else {
			throw new AssertionError("Error parsing table file: " + filename + ". Only recovered " + parseResult.length + " values.");
		}
	}

	public static void main(final String[] args) {
		System.out.println(Arrays.toString(parseFile("knight-midgame")));
	}
}
