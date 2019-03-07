package jenjinn.base;

/**
 * Enumeration of the two sides in a chessgame.
 *
 * @author TB
 * @date 1 Dec 2016
 */
public enum Side
{
	WHITE(6), BLACK(1);

	public final int penultimatePawnRank;

	private Side(int penultimatePawnRank)
	{
		this.penultimatePawnRank = penultimatePawnRank;
	}

	public boolean isWhite()
	{
		return this == Side.WHITE;
	}

	public boolean isBlack()
	{
		return this == BLACK;
	}

	public Side otherSide()
	{
		return isWhite()? BLACK : WHITE;
	}
}
