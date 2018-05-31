/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import java.util.List;

import jenjinn.engine.ChessPieces;
import xawd.jflow.iterators.construction.IterRange;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author t
 */
public final class TestingPieceSquareTables
{
	private TestingPieceSquareTables() {}

	private static final PieceSquareTables TESTING_MIDGAME_TABLES = initMidgameTables();
	private static final PieceSquareTables TESTING_ENDGAME_TABLES = initEndgameTables();

	private static PieceSquareTables initMidgameTables()
	{
		final List<PieceSquareTable> whiteTables = Iterate.over(ChessPieces.white())
				.map(piece -> new PieceSquareTable(piece, IterRange.to(64).map(i -> i + 100*piece.ordinal()).toArray()))
				.toList();

		final List<PieceSquareTable> allTables = Iterate.over(whiteTables)
				.append(Iterate.over(whiteTables).map(PieceSquareTable::invertValues))
				.toList();

		return new PieceSquareTables(allTables);
	}

	private static PieceSquareTables initEndgameTables()
	{
		final List<PieceSquareTable> whiteTables = Iterate.over(ChessPieces.white())
				.map(piece -> new PieceSquareTable(piece, IterRange.to(64).map(i -> i + 1000*piece.ordinal()).toArray()))
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
	public static PieceSquareTables getMidgameTables()
	{
		return TESTING_MIDGAME_TABLES;
	}

	/**
	 * Generate piece square tables where value for piece p at
	 * square s is:<br><br>
	 *
	 * p.side*(1000*p.ord + s.ord)
	 */
	public static PieceSquareTables getEndgameTables()
	{
		return TESTING_ENDGAME_TABLES;
	}
}
