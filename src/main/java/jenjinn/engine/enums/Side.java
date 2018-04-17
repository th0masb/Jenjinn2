package jenjinn.engine.enums;

/**
 * Enumeration of the two sides in a chessgame.
 * 
 * @author TB
 * @date 1 Dec 2016
 */
public enum Side 
{
	W(0, 1), B(6, -1);

	public final int pawnPieceIndex;
	public final int pawnDirection;

	private Side(final int pawnPieceIndex, final int pawnDirection)
	{
		this.pawnPieceIndex = pawnPieceIndex;
		this.pawnDirection = pawnDirection;
	}

	public boolean isWhite()
	{
		return this == Side.W;
	}

	public Side otherSide()
	{
		if (this == B) {
			return W;
		}
		else {
			return B;
		}
	}

	public String getFilename()
	{
		return name().toLowerCase();
	}

	public boolean isMaximising()
	{
		if (this == W) {
			return true;
		}
		else {
			return false;
		}
	}
}
