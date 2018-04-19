package jenjinn.engine.enums;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Enum representing all the different directions
 * the various chesspieces can move on the board.
 * 
 * @author t
 */
public enum Direction
{
	N(1, 0), E(0, -1), S(-1, 0), W(0, 1),
	NE(1, -1), SE(-1, -1), SW(-1, 1), NW(1, 1),
	NNE(2, -1), NEE(1, -2), SEE(-1, -2), SSE(-2, -1),
	SSW(-2, 1), SWW(-1, 2), NWW(1, 2), NNW(2, 1);

	public int rankIndexChange;
	public int fileIndexChange;

	private Direction(final int rankIndexChange, final int fileIndexChange)
	{
		this.rankIndexChange = rankIndexChange;
		this.fileIndexChange = fileIndexChange;
	}
	
	public static Stream<Direction> stream()
	{
		return Arrays.stream(values());
	}
}
