/**
 *
 */
package jenjinn.utils;

import java.util.Arrays;

import jenjinn.base.Square;
import jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 *
 */
final class CharGrid
{
	private CharGrid()
	{
	}

	public static final int BOARD_CHAR_WIDTH = 3 * 8 + 1, BOARD_LINE_HEIGHT = 2 * 8 + 1;
	private static final char[] BLANK_GRID = initGrid();

	private static char[] initGrid()
	{
		final char[] grid = new char[BOARD_CHAR_WIDTH * BOARD_LINE_HEIGHT];

		IterRange.to(BOARD_LINE_HEIGHT).forEach(i -> {
			final char[] line = (i % 2 == 0) ? getEvenIndexLine() : getOddLineIndex();
			System.arraycopy(line, 0, grid, i * BOARD_CHAR_WIDTH, BOARD_CHAR_WIDTH);
		});

		return grid;
	}

	private static char[] getEvenIndexLine()
	{
		final char[] line = new char[BOARD_CHAR_WIDTH];
		IterRange.to(BOARD_CHAR_WIDTH).forEach(i -> line[i] = (i % 3 == 0) ? '+' : '-');
		return line;
	}

	private static char[] getOddLineIndex()
	{
		final char[] line = new char[BOARD_CHAR_WIDTH];
		IterRange.to(BOARD_CHAR_WIDTH).forEach(i -> line[i] = (i % 3 == 0) ? '|' : ' ');
		return line;
	}

	static int mapToGridIndex(final Square square)
	{
		final int squareIndex = square.ordinal();
		final int rankIndex = squareIndex / 8, fileIndex = squareIndex % 8;

		final int lineStartIndex = BOARD_CHAR_WIDTH * (1 + 2 * rankIndex);
		final int indexOnLine = 1 + fileIndex * 3;

		return lineStartIndex + indexOnLine;
	}

	static char[] getNewGrid()
	{
		return Arrays.copyOf(BLANK_GRID, BLANK_GRID.length);
	}

	// private static String testGrid()
	// {
	// final char[] grid = getNewGrid();
	//
	// BoardSquare.iterateAll().forEach(square ->
	// {
	// final int gridIndex = mapToGridIndex(square);
	// final char[] entry = reverse(square.name().toCharArray());
	// System.arraycopy(entry, 0, grid, gridIndex, 2);
	// });
	//
	// return FormatBoard.formatGrid(grid);
	// }
	//
	// public static void main(final String[] args)
	// {
	// System.out.println(Arrays.toString(BLANK_GRID));
	// }
}
