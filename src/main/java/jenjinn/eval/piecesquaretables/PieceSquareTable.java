/**
 *
 */
package jenjinn.eval.piecesquaretables;

import java.util.Arrays;

import jenjinn.base.Square;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;
import jflow.iterators.factories.IterRange;
import jflow.iterators.factories.Iter;

/**
 * @author ThomasB
 */
public final class PieceSquareTable
{
	private final Piece associatedPiece;
	private final int[] locationValues;

	public PieceSquareTable(Piece associatedPiece, int pieceValue, int[] locationValues)
	{
		if (associatedPiece == null || locationValues.length != 64) {
			throw new IllegalArgumentException();
		}
		this.associatedPiece = associatedPiece;
		this.locationValues = Iter.overInts(locationValues).map(n -> n + pieceValue).toArray();
	}

	public Piece getAssociatedPiece()
	{
		return associatedPiece;
	}

	public int getValueAt(Square location)
	{
		return locationValues[location.ordinal()];
	}

	public PieceSquareTable invertValues()
	{
		return new PieceSquareTable(
				ChessPieces.fromIndex((associatedPiece.ordinal() + 6) % 12),
				0,
				IterRange.to(64).map(i -> -locationValues[63  - 8*(i/8) - (7 - (i % 8))]).toArray()
				);
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((associatedPiece == null) ? 0 : associatedPiece.hashCode());
		result = prime * result + Arrays.hashCode(locationValues);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PieceSquareTable other = (PieceSquareTable) obj;
		if (associatedPiece != other.associatedPiece)
			return false;
		if (!Arrays.equals(locationValues, other.locationValues))
			return false;
		return true;
	}
}
