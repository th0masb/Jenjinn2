/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import xawd.jflow.iterators.construction.IterRange;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author ThomasB
 *
 */
public final class PieceSquareTables
{
	private final List<PieceSquareTable> tables;

	public PieceSquareTables(final List<PieceSquareTable> tables)
	{
		if (tables.size() != 12 || IterRange.to(12).anyMatch(i -> tables.get(i).getAssociatedPiece().ordinal() != i)) {
			throw new IllegalArgumentException();
		}
		this.tables = Iterate.over(tables).toImmutableList();
	}

	public int getLocationValue(final ChessPiece piece, final BoardSquare location)
	{
		return tables.get(piece.ordinal()).getValueAt(location);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tables == null) ? 0 : tables.hashCode());
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
		final PieceSquareTables other = (PieceSquareTables) obj;
		if (tables == null) {
			if (other.tables != null)
				return false;
		} else if (!tables.equals(other.tables))
			return false;
		return true;
	}
}
