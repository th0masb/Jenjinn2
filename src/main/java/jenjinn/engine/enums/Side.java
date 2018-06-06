package jenjinn.engine.enums;

/**
 * Enumeration of the two sides in a chessgame.
 *
 * @author TB
 * @date 1 Dec 2016
 */
public enum Side
{
	WHITE(6), BLACK(1);

	private final int penultimatePawnRank;

	private Side(final int penultimatePawnRank)
	{
		this.penultimatePawnRank = penultimatePawnRank;
	}

	public int getPenultimatePawnRank()
	{
		return penultimatePawnRank;
	}

	public boolean isWhite()
	{
		return this == Side.WHITE;
	}

	public Side otherSide()
	{
		if (this == BLACK) {
			return WHITE;
		}
		else {
			return BLACK;
		}
	}
}
