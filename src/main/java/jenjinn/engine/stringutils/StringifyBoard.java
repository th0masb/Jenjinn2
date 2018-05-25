/**
 *
 */
package jenjinn.engine.stringutils;

import static jenjinn.engine.bitboards.Bitboards.rankBitboard;

import java.util.Arrays;
import java.util.List;

import xawd.jflow.iterators.construction.Iterate;

/**
 * @author t
 *
 */
public final class StringifyBoard {

	public static String formatGrid(final TitledVisualGrid grid)
	{
		return formatGrids(Arrays.asList(grid), "");
	}

	public static String formatGrids(final List<TitledVisualGrid> gridData)
	{
		return formatGrids(gridData, "    ");
	}

	public static String formatGrids(final List<TitledVisualGrid> gridData, final String gridSeparator)
	{
		final List<List<String>> rawGridLines = Iterate.over(gridData).map(TitledVisualGrid::getGridLines).toList();

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < CharGrid.BOARD_LINE_HEIGHT + 1; i++) {
			final int j = i;
			Iterate.over(rawGridLines).forEach(rawGrid -> {
				sb.append(rawGrid.get(j));
				sb.append(gridSeparator);
			});
			final int end = sb.length();
			sb.replace(end - gridSeparator.length(), end, System.lineSeparator());
		}
		return sb.toString();
	}

	private StringifyBoard() {
		//		+--+
		//	    |BN|
		//	    +--+
		//	    |WQ|
		//		+--+
	}

	public static void main(final String[] args) {

		final TitledVisualGrid grid = VisualGridGenerator.from(rankBitboard(0) | rankBitboard(5));
		System.out.println(formatGrids(Arrays.asList(grid, grid, grid), "     "));
	}
}
