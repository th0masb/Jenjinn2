/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import java.util.Arrays;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import xawd.jflow.iterators.construction.IterRange;

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

	public PieceSquareTable invertValues()
	{
		return new PieceSquareTable(
				ChessPieces.fromIndex((associatedPiece.ordinal() + 6) % 12),
				IterRange.to(64).map(i -> -locationValues[63  - 8*(i/8) - (7 - (i % 8))]).toArray()
				);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associatedPiece == null) ? 0 : associatedPiece.hashCode());
		result = prime * result + Arrays.hashCode(locationValues);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PieceSquareTable other = (PieceSquareTable) obj;
		if (associatedPiece != other.associatedPiece)
			return false;
		if (!Arrays.equals(locationValues, other.locationValues))
			return false;
		return true;
	}
}
