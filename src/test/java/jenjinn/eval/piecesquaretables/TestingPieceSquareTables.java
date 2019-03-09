/**
 *
 */
package jenjinn.eval.piecesquaretables;

import com.github.maumay.jflow.iterators.factories.Iter;
import com.github.maumay.jflow.vec.Vec;

import jenjinn.pieces.ChessPieces;

/**
 * @author t
 */
public final class TestingPieceSquareTables
{
	private TestingPieceSquareTables()
	{
	}

	private static final PieceSquareTables TESTING_MIDGAME_TABLES = initMidgameTables();
	private static final PieceSquareTables TESTING_ENDGAME_TABLES = initEndgameTables();

	private static PieceSquareTables initMidgameTables()
	{
		Vec<PieceSquareTable> whiteTables = ChessPieces.WHITE
				.map(piece -> new PieceSquareTable(piece, 0,
						Iter.until(64).map(i -> i + 100 * piece.ordinal()).toArray()));

		return new PieceSquareTables(whiteTables);
	}

	private static PieceSquareTables initEndgameTables()
	{
		Vec<PieceSquareTable> whiteTables = ChessPieces.WHITE
				.map(piece -> new PieceSquareTable(piece, 0,
						Iter.until(64).map(i -> i + 1000 * piece.ordinal()).toArray()));

		return new PieceSquareTables(whiteTables);
	}

	/**
	 * Generate piece square tables where value for piece p at square s is:<br>
	 * <br>
	 *
	 * p.side*(100*p.ord + s.ord)
	 */
	public static PieceSquareTables getMidgameTables()
	{
		return TESTING_MIDGAME_TABLES;
	}

	/**
	 * Generate piece square tables where value for piece p at square s is:<br>
	 * <br>
	 *
	 * p.side*(1000*p.ord + s.ord)
	 */
	public static PieceSquareTables getEndgameTables()
	{
		return TESTING_ENDGAME_TABLES;
	}
}
