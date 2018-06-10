/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import static jenjinn.engine.enums.ChessPiece.BLACK_KNIGHT;
import static jenjinn.engine.enums.ChessPiece.WHITE_KNIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import xawd.jflow.iterators.factories.IterRange;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author t
 *
 */
class PieceSquareTableInversionTest
{
	@Test
	void test()
	{
		final PieceSquareTable startTable = new PieceSquareTable(WHITE_KNIGHT, IterRange.between(0, -64, -1).toArray());

		final int[] expectedInvertedLocs = IterRange.between(7, -1, -1)
				.map(i -> 8*i)
				.mapToObject(i -> IterRange.between(i, i + 8).toArray())
				.flattenToInts(Iterate::over)
				.toArray();

		final PieceSquareTable expectedInversion = new PieceSquareTable(BLACK_KNIGHT, expectedInvertedLocs);

		assertEquals(expectedInversion, startTable.invertValues());
		assertEquals(startTable, startTable.invertValues().invertValues());
	}
}
