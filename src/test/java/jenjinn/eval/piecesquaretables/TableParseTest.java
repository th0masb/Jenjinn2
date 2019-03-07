/**
 *
 */
package jenjinn.eval.piecesquaretables;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jenjinn.eval.PieceValues;
import jenjinn.eval.piecesquaretables.PieceSquareTable;
import jenjinn.eval.piecesquaretables.TableParser;
import jenjinn.pieces.Piece;
import jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 */
class TableParseTest
{
	@Test
	void test()
	{
		final Piece p = Piece.WHITE_BISHOP;
		final PieceSquareTable expected = new PieceSquareTable(
				p,
				PieceValues.TESTING.valueOf(p),
				IterRange.to(64).map(i -> i*(1 - 2*(i % 2))).toArray());
		assertEquals(expected, TableParser.parseFile(p, "table-testing"));
	}
}
