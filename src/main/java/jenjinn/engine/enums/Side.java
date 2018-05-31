package jenjinn.engine.enums;

/**
 * Enumeration of the two sides in a chessgame.
 * 
 * @author TB
 * @date 1 Dec 2016
 */
public enum Side 
{
	WHITE(0, 1), BLACK(6, -1);

	public final int pawnPieceIndex;
	public final int pawnDirection;

	private Side(final int pawnPieceIndex, final int pawnDirection)
	{
		this.pawnPieceIndex = pawnPieceIndex;
		this.pawnDirection = pawnDirection;
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

	public String getFilename()
	{
		return name().toLowerCase();
	}

	public boolean isMaximising()
	{
		if (this == WHITE) {
			return true;
		}
		else {
			return false;
		}
	}
}
