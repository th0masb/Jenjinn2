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
import xawd.jflow.iterators.Flow;

/**
 * @author ThomasB
 */
class SquarecontrolTest
{
	@ParameterizedTest
	@MethodSource
	void test(final BoardState state, final Map<ChessPiece, Long> expectedControl)
	{
		ChessPieces.iterate()
		.forEach(piece -> assertEquals(expectedControl.get(piece).longValue(), SquareControl.calculate(state, piece)));

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
		throw new RuntimeException();
	}
}
