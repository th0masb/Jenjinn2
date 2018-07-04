/**
 *
 */
package jenjinn.engine.utils;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 */
public final class PieceSquarePair {

	private final ChessPiece piece;
	private final BoardSquare square;

	public PieceSquarePair(final ChessPiece piece, final BoardSquare square)
	{
		this.piece = piece;
		this.square = square;
	}

	public ChessPiece getPiece()
	{
		return piece;
	}

	public BoardSquare getSquare()
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
