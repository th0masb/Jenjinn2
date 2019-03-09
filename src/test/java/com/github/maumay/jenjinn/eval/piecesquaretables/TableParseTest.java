/**
 *
 */
package com.github.maumay.jenjinn.eval.piecesquaretables;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.maumay.jenjinn.eval.PieceValues;
import com.github.maumay.jenjinn.eval.piecesquaretables.PieceSquareTable;
import com.github.maumay.jenjinn.eval.piecesquaretables.TableParser;
import com.github.maumay.jenjinn.pieces.Piece;
import com.github.maumay.jflow.iterators.factories.Iter;

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
