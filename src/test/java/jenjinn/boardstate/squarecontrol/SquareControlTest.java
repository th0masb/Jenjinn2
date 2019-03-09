/**
 *
 */
package jenjinn.boardstate.squarecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jflow.iterators.EnhancedIterator;
import com.github.maumay.jflow.iterators.factories.Iter;

import jenjinn.base.Side;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.calculators.SquareControl;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;
import jenjinn.utils.VisualGridGenerator;

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

		long expectedBlackcontrol = ChessPieces.ALL.iter().skip(6)
				.mapToLong(expectedControl::get).fold(0L, (a, b) -> a | b);

		assertEquals(expectedBlackcontrol, SquareControl.calculate(state, Side.BLACK));
	}

	static EnhancedIterator<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iter.over(parser.parse("case001"));
	}
}
