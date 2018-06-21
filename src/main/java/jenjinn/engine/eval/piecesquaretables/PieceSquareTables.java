/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import java.util.List;

import jenjinn.engine.boardstate.LocationTracker;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.iterators.factories.IterRange;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public final class PieceSquareTables
{
	private final List<PieceSquareTable> tables;

	public PieceSquareTables(final List<PieceSquareTable> whiteTables)
	{
		if (whiteTables.size() != 6 || IterRange.to(6).anyMatch(i -> whiteTables.get(i).getAssociatedPiece().ordinal() != i).get()) {
			throw new IllegalArgumentException();
		}
		this.tables = Iterate.over(whiteTables)
				.append(Iterate.over(whiteTables).map(PieceSquareTable::invertValues))
				.toImmutableList();
	}

	public int getLocationValue(final ChessPiece piece, final BoardSquare location)
	{
		return tables.get(piece.ordinal()).getValueAt(location);
	}

	public int evaluateLocations(final List<LocationTracker> pieceLocations)
	{
		if (pieceLocations.size() != 12) {
			throw new IllegalArgumentException();
		}
		int eval = 0;
		for (int i = 0; i < pieceLocations.size(); i++) {
			final PieceSquareTable pieceTable = tables.get(i);
			eval += pieceLocations.get(i).iterator()
					.mapToInt(loc -> pieceTable.getValueAt(loc))
					.reduce(0, (a, b) -> a + b);
		}
		return eval;
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

	public static PieceSquareTables endgame()
	{
		return new PieceSquareTables(
				Iterate.over(ChessPieces.white())
				.map(p -> TableParser.parseFile(p, p.name().substring(6).toLowerCase() + "-endgame"))
				.toList());
	}

	public static PieceSquareTables midgame()
	{
		return new PieceSquareTables(
				Iterate.over(ChessPieces.white())
				.map(p -> TableParser.parseFile(p, p.name().substring(6).toLowerCase() + "-midgame"))
				.toList());
	}

	public static void main(final String[] args)
	{
		System.out.println(ChessPiece.WHITE_BISHOP.name().substring(6).toLowerCase());
	}
}
