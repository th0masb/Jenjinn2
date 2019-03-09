/**
 *
 */
package com.github.maumay.jenjinn.eval;

import com.github.maumay.jenjinn.pieces.Piece;
import com.github.maumay.jflow.iterators.factories.Iter;

/**
 * @author ThomasB
 */
public enum PieceValues
{
	MIDGAME(new int[] { 100, 310, 320, 480, 910, 0 }),
	ENDGAME(new int[] { 120, 270, 340, 550, 940, 0 }),
	TESTING(new int[] { 100, 300, 310, 500, 900, 0 });

	private final int[] values;

	private PieceValues(int[] values)
	{
		if (Iter.ints(values).anyMatch(n -> n < 0)) {
			throw new AssertionError();
		}
		this.values = Iter.ints(values).map(i -> (int) 3.5 * i).toArray();
	}

	public int valueOf(Piece piece)
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
