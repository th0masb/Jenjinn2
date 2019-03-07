/**
 *
 */
package jenjinn.utils;

import jenjinn.base.Square;
import jenjinn.pieces.Piece;

/**
 * @author ThomasB
 */
public final class PieceSquarePair {

	private final Piece piece;
	private final Square square;

	public PieceSquarePair(final Piece piece, final Square square)
	{
		this.piece = piece;
		this.square = square;
	}

	public Piece getPiece()
	{
		return piece;
	}

	public Square getSquare()
	{
		return square;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((piece == null) ? 0 : piece.hashCode());
		result = prime * result + ((square == null) ? 0 : square.hashCode());
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
		final PieceSquarePair other = (PieceSquarePair) obj;
		if (piece != other.piece)
			return false;
		if (square != other.square)
			return false;
		return true;
	}
}
