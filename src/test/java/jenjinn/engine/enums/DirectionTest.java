package jenjinn.engine.enums;

import static java.util.EnumSet.complementOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Direction;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class DirectionTest
{
	@Test
	void testOfLineBetween()
	{
		final BoardSquare start = BoardSquare.E4;
		final EnumSet<BoardSquare> visitedSquares = EnumSet.noneOf(BoardSquare.class);

		Direction.iterateAll().forEach(dir -> {
			for (final BoardSquare square : start.getAllSquaresInDirections(dir, 8)) {
				visitedSquares.add(square);
				assertEquals(Optional.of(dir), Direction.ofLineBetween(start, square));
			}
		});

		Iterate.over(complementOf(visitedSquares)).forEach(square -> {
			final Optional<Direction> result = Direction.ofLineBetween(start, square);
			assertEquals(Optional.empty(), result, square.name());
		});
	}

}
