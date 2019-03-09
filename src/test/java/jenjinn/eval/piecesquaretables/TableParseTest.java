/**
 *
 */
package jenjinn.eval.piecesquaretables;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.maumay.jflow.iterators.factories.Iter;

import jenjinn.eval.PieceValues;
import jenjinn.pieces.Piece;

/**
 * @author ThomasB
 */
class TableParseTest
{
	@Test
	void test()
	{
		final Piece p = Piece.WHITE_BISHOP;
		final PieceSquareTable expected = new PieceSquareTable(p,
				PieceValues.TESTING.valueOf(p),
				Iter.until(64).map(i -> i * (1 - 2 * (i % 2))).toArray());
		assertEquals(expected, TableParser.parseFile(p, "table-testing"));
	}
}
