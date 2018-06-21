/**
 *
 */
package jenjinn.engine.eval;

import java.util.Arrays;

import jenjinn.engine.enums.ChessPiece;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public enum PieceValues
{
	MIDGAME(new int[] {100, 295, 310, 500, 900, 0}),
	ENDGAME(new int[] {100, 295, 310, 500, 900, 0});

	private final int[] values;

	private PieceValues(int[] values)
	{
		Iterate.overInts(values).allMatch(i -> i >= 0).throwIfFailed(AssertionError::new);
		this.values = Arrays.copyOf(values, values.length);
	}

	public int valueOf(ChessPiece piece)
	{
		return values[piece.ordinal() % 6];
	}
}
