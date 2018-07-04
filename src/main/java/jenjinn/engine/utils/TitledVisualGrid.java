/**
 *
 */
package jenjinn.engine.utils;

import static xawd.jflow.utilities.CollectionUtil.reverse;

import java.util.List;
import java.util.Map;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.bitboards.BitboardIterator;
import xawd.jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 */
public final class TitledVisualGrid
{
	private static final String UNTITLED = "Untitled";

	private final String title;
	private final Map<BoardSquare, CharPair> visualGrid;

	public TitledVisualGrid(String title, final Map<BoardSquare, CharPair> visualGrid)
	{
		title = title.trim().isEmpty()? UNTITLED : title.trim();
		final int titleLengthDifference = CharGrid.BOARD_CHAR_WIDTH - title.length() - 2;
		if (titleLengthDifference < 0) {
			throw new IllegalArgumentException("title too long.");
		}
		this.title = "  " + title + IterRange.to(titleLengthDifference).mapToObject(i -> " ").reduce((s1, s2) -> s1 + s2).get();
		this.visualGrid = visualGrid;
	}

	public TitledVisualGrid(final Map<BoardSquare, CharPair> visualGrid)
	{
		this("", visualGrid);
	}

	public static TitledVisualGrid from(final String title, final long bitboard)
	{
		return new TitledVisualGrid(title, BitboardIterator.from(bitboard).toMap(x -> x, x -> new CharPair('X', 'X')));
	}

	public static TitledVisualGrid from(final long bitboard)
	{
		return from("", bitboard);
	}

	public String getTitle()
	{
		return title;
	}

	@Override
	public String toString()
	{
		return StringifyBoard.formatGrid(this);
	}

	public CharPair getEntryAt(final BoardSquare square)
	{
		return visualGrid.get(square);
	}

	public List<String> getGridLines()
	{
		final char[] grid = convertVisualGridToCharArray();
		final int w = CharGrid.BOARD_CHAR_WIDTH, h = CharGrid.BOARD_LINE_HEIGHT;

		final List<String> lines = IterRange.to(h).mapToObject(i ->
		{
			final StringBuilder sb = new StringBuilder();
			final int lineStart = i*w;
			IterRange.to(w).forEach(j -> sb.append(grid[lineStart + j]));
			return sb.reverse().toString();
		}).append(title).toList();

		return reverse(lines);
	}

	private char[] convertVisualGridToCharArray()
	{
		final char[] grid = CharGrid.getNewGrid();
		BoardSquare.iterateAll().forEach(square -> {
			final int gridIndex = CharGrid.mapToGridIndex(square);
			final char[] entry = visualGrid.containsKey(square)? visualGrid.get(square).toArray() : new char[] {' ', ' '};
			System.arraycopy(entry, 0, grid, gridIndex, 2);
		});
		return grid;
	}
}
