/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;

/**
 * @author ThomasB
 */
public final class PieceSquareTable
{
	private final ChessPiece associatedPiece;
	private final int[] locationValues;

	public PieceSquareTable(final ChessPiece associatedPiece, final int[] locationValues)
	{
		if (associatedPiece == null || locationValues.length != 64) {
			throw new IllegalArgumentException();
		}
		this.associatedPiece = associatedPiece;
		this.locationValues = locationValues;
	}

	public ChessPiece getAssociatedPiece()
	{
		return associatedPiece;
	}

	public int getValueAt(final BoardSquare location)
	{
		return locationValues[location.ordinal()];
	}
}
