/**
 *
 */
package jenjinn.engine.boardstate.squarecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.base.Side;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.calculators.SquareControl;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import jenjinn.engine.utils.VisualGridGenerator;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class SquareControlTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, Map<ChessPiece, Long> expectedControl)
	{
		for (ChessPiece p : ChessPieces.all()) {
			long expected = expectedControl.get(p), actual = SquareControl.calculate(state, p);
			assertEquals(expected, actual, p.name() + System.lineSeparator() + VisualGridGenerator.from(expected, actual));
		}


		long expectedWhitecontrol = ChessPieces.iterate().take(6)
				.mapToLong(expectedControl::get)
				.fold(0L, (a, b) -> a | b);

		assertEquals(expectedWhitecontrol, SquareControl.calculate(state, Side.WHITE));

		long expectedBlackcontrol = ChessPieces.iterate().drop(6)
				.mapToLong(expectedControl::get)
				.fold(0L, (a, b) -> a | b);

		assertEquals(expectedBlackcontrol, SquareControl.calculate(state, Side.BLACK));
	}

	static Flow<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Iterate.over(parser.parse("case001"));
	}
}
