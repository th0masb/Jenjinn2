/**
 *
 */
package com.github.maumay.jenjinn.boardstate.squarecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jenjinn.base.Side;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.calculators.SquareControl;
import com.github.maumay.jenjinn.pieces.ChessPieces;
import com.github.maumay.jenjinn.pieces.Piece;
import com.github.maumay.jenjinn.utils.VisualGridGenerator;
import com.github.maumay.jflow.iterators.Iter;
import com.github.maumay.jflow.iterators.RichIterator;

/**
 * @author ThomasB
 */
class SquareControlTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, Map<Piece, Long> expectedControl)
	{
		for (Piece p : ChessPieces.ALL) {
			long expected = expectedControl.get(p),
					actual = SquareControl.calculate(state, p);
			assertEquals(expected, actual, p.name() + System.lineSeparator()
					+ VisualGridGenerator.from(expected, actual));
		}

		long expectedWhitecontrol = ChessPieces.ALL.iter().take(6)
				.mapToLong(expectedControl::get).fold(0L, (a, b) -> a | b);

		assertEquals(expectedWhitecontrol, SquareControl.calculate(state, Side.WHITE));

		long expectedBlackcontrol = ChessPieces.ALL.iter().drop(6)
				.mapToLong(expectedControl::get).fold(0L, (a, b) -> a | b);

		assertEquals(expectedBlackcontrol, SquareControl.calculate(state, Side.BLACK));
	}

	static RichIterator<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iter.over(parser.parse("case001"));
	}
}
