/**
 *
 */
package jenjinn.engine.boardstate.squarecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.calculators.SquareControl;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.stringutils.VisualGridGenerator;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class SquareControlTest
{
	@ParameterizedTest
	@MethodSource
	void test(final BoardState state, final Map<ChessPiece, Long> expectedControl)
	{
		for (final ChessPiece p : ChessPieces.all()) {
			final long expected = expectedControl.get(p), actual = SquareControl.calculate(state, p);
			assertEquals(expected, actual, p.name() + System.lineSeparator() + VisualGridGenerator.from(expected, actual));
		}


		final long expectedWhitecontrol = ChessPieces.iterate().take(6)
				.mapToLong(expectedControl::get)
				.reduce(0L, (a, b) -> a | b);

		assertEquals(expectedWhitecontrol, SquareControl.calculate(state, Side.WHITE));

		final long expectedBlackcontrol = ChessPieces.iterate().drop(6)
				.mapToLong(expectedControl::get)
				.reduce(0L, (a, b) -> a | b);

		assertEquals(expectedBlackcontrol, SquareControl.calculate(state, Side.BLACK));
	}

	static Flow<Arguments> test()
	{
		return Iterate.over(TestFileParser.parse("case001"));
	}
}
