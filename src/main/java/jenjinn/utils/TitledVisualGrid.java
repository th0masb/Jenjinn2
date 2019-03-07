/**
 *
 */
package jenjinn.utils;

import java.util.List;
import java.util.Map;

import jenjinn.base.Square;
import jenjinn.bitboards.BitboardIterator;
import jflow.iterators.factories.IterRange;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
public final class TitledVisualGrid
{
	private static final String UNTITLED = "Untitled";

	private final String title;
	private final Map<Square, CharPair> visualGrid;

	public TitledVisualGrid(String title, Map<Square, CharPair> visualGrid)
	{
		title = title.trim().isEmpty()? UNTITLED : title.trim();
		int titleLengthDifference = CharGrid.BOARD_CHAR_WIDTH - title.length() - 2;
		if (titleLengthDifference < 0) {
			throw new IllegalArgumentException("title too long.");
		}
		this.title = "  " + title + IterRange.to(titleLengthDifference).mapToObject(i -> " ").fold((s1, s2) -> s1 + s2);
		this.visualGrid = visualGrid;
	}

	public TitledVisualGrid(Map<Square, CharPair> visualGrid)
	{
		this("", visualGrid);
	}

	public static TitledVisualGrid from(String title, long bitboard)
	{
		return new TitledVisualGrid(title, BitboardIterator.from(bitboard).toMap(x -> x, x -> new CharPair('X', 'X')));
	}

	public static TitledVisualGrid from(long bitboard)
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

	public CharPair getEntryAt(Square square)
	{
		return visualGrid.get(square);
	}

	public List<String> getGridLines()
	{
		char[] grid = convertVisualGridToCharArray();
		int w = CharGrid.BOARD_CHAR_WIDTH, h = CharGrid.BOARD_LINE_HEIGHT;

		Seq<String> lines = IterRange.to(h).mapToObject(i ->
		{
			StringBuilder sb = new StringBuilder();
			int lineStart = i*w;
			IterRange.to(w).forEach(j -> sb.append(grid[lineStart + j]));
			return sb.reverse().toString();
		}).append(title).toSeq();

		return lines.rflow().toList();
	}

	private char[] convertVisualGridToCharArray()
	{
		char[] grid = CharGrid.getNewGrid();
		Square.ALL.forEach(square -> {
			int gridIndex = CharGrid.mapToGridIndex(square);
			char[] entry = visualGrid.containsKey(square)? visualGrid.get(square).toArray() : new char[] {' ', ' '};
			System.arraycopy(entry, 0, grid, gridIndex, 2);
		});
		return grid;
	}
}
