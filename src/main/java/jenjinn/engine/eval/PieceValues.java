/**
 *
 */
package jenjinn.engine.eval;

import jenjinn.engine.pieces.ChessPiece;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
public enum PieceValues
{
	MIDGAME(new int[] { 100, 310, 320, 480, 910, 0 }), ENDGAME(new int[] { 120, 270, 340, 550, 940, 0 }),
	TESTING(new int[] { 100, 300, 310, 500, 900, 0 });

	private final int[] values;

	private PieceValues(int[] values)
	{
		Iterate.overInts(values).allMatch2(i -> i >= 0).throwIfFalse(AssertionError::new);
		this.values = Iterate.overInts(values).map(i -> (int) 3.5 * i).toArray();
	}

	public int valueOf(ChessPiece piece)
	{
		return values[piece.ordinal() % 6];
	}

	public int valueOfPawn()
	{
		return values[0];
	}

	public int valueOfQueen()
	{
		return values[4];
	}
}
