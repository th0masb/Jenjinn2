/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import java.util.List;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTable;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import xawd.jflow.iterators.construction.IterRange;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author t
 */
public final class TestingPieceSquareTables
{
	private TestingPieceSquareTables() {}

	private static final PieceSquareTables TESTING_TABLES = initTables();

	private static PieceSquareTables initTables()
	{
		final List<PieceSquareTable> whiteTables = Iterate.over(ChessPieces.white())
				.map(piece -> new PieceSquareTable(piece, IterRange.to(64).map(i -> i + 100*piece.ordinal()).toArray()))
				.toList();

		final List<PieceSquareTable> allTables = Iterate.over(whiteTables)
				.append(Iterate.over(whiteTables).map(PieceSquareTable::invertValues))
				.toList();

		return new PieceSquareTables(allTables);
	}

	/**
	 * Generate piece square tables where value for piece p at
	 * square s is:<br><br>
	 *
	 * p.side*(100*p.ord + s.ord)
	 */
	public static PieceSquareTables get()
	{
		return TESTING_TABLES;
	}
}
