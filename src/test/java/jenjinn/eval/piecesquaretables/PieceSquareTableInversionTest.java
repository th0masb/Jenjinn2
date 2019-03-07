/**
 *
 */
package jenjinn.eval.piecesquaretables;

import static jenjinn.pieces.Piece.BLACK_KNIGHT;
import static jenjinn.pieces.Piece.WHITE_KNIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jflow.iterators.factories.Iter;
import jflow.iterators.factories.IterRange;

/**
 * @author t
 *
 */
class PieceSquareTableInversionTest
{
	@Test
	void test()
	{
		final PieceSquareTable startTable = new PieceSquareTable(WHITE_KNIGHT, 500, IterRange.between(0, -64, -1).toArray());

		final int[] expectedInvertedLocs = IterRange.between(7, -1, -1)
				.map(i -> 8*i)
				.mapToObject(i -> IterRange.between(i, i + 8).toArray())
				.flatMapToInt(Iter::overInts)
				.toArray();

		final PieceSquareTable expectedInversion = new PieceSquareTable(BLACK_KNIGHT, -500, expectedInvertedLocs);

		assertEquals(expectedInversion, startTable.invertValues());
		assertEquals(startTable, startTable.invertValues().invertValues());
	}
}
