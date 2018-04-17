package jenjinn.engine.enums;

/**
 * Enum representing all the different directions
 * the various chesspieces can move on the board.
 * 
 * @author t
 */
public enum Direction
{
	N(0, 1), 
	E(-1, 0), 
	S(0, -1), 
	W(1, 0),
	NE(-1, 1), 
	SE(-1, -1), 
	SW(1, -1), 
	NW(1, 1),
	NNE(-1, 2),
	NEE(-2, 1), 
	SEE(-2, -1), 
	SSE(-1, -2),
	SSW(1, -2),
	SWW(2, -1), 
	NWW(2, 1), 
	NNW(1, 2);

	public int rankIndexChange;
	public int fileIndexChange;

	private Direction(final int rankIndexChange, final int fileIndexChange)
	{
		this.rankIndexChange = rankIndexChange;
		this.fileIndexChange = fileIndexChange;
	}
}
