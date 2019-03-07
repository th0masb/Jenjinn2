/**
 *
 */
package jenjinn.eval;

import static java.lang.Math.min;

import jenjinn.pieces.Piece;
import jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 */
public enum KingSafetyTable
{
	INSTANCE(2.0);

	private final int[] safetyTable = { 0, 0, 1, 2, 3, 5, 7, 9, 12, 15, 18, 22, 26, 30, 35, 39, 44, 50, 56, 62, 68, 75,
			82, 85, 89, 97, 105, 113, 122, 131, 140, 150, 169, 180, 191, 202, 213, 225, 237, 248, 260, 272, 283, 295,
			307, 319, 330, 342, 354, 366, 377, 389, 401, 412, 424, 436, 448, 459, 471, 483, 494, 500, 500, 500, 500,
			500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500,
			500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500 };

	private final int[] outerUnits = {0, 1, 1, 3, 3, 0};
	private final int[] innerUnits = {0, 2, 2, 6, 8, 0};

	private KingSafetyTable(double scale)
	{
		IterRange.to(safetyTable.length).forEach(i -> safetyTable[i] *= scale);
	}

	public int getOuterUnitValue(Piece piece)
	{
		return outerUnits[piece.ordinal() % 6];
	}

	public int getInnerUnitValue(Piece piece)
	{
		return innerUnits[piece.ordinal() % 6];
	}

	public int indexSafetyTable(int indexValue)
	{
		return safetyTable[min(indexValue, safetyTable.length - 1)];
	}
}
