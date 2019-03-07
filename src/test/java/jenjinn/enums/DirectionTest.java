package jenjinn.enums;

import static java.util.EnumSet.complementOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import jenjinn.base.Dir;
import jenjinn.base.Square;
import jflow.iterators.factories.Iter;

/**
 * @author ThomasB
 */
class DirectionTest
{
	@Test
	void testOfLineBetween()
	{
		final Square start = Square.E4;
		final EnumSet<Square> visitedSquares = EnumSet.noneOf(Square.class);

		Dir.ALL.forEach(dir -> {
			for (Square square : start.getAllSquares(dir, 8)) {
				visitedSquares.add(square);
				assertEquals(Optional.of(dir), Dir.ofLineBetween(start, square));
			}
		});

		Iter.over(complementOf(visitedSquares)).forEach(square -> {
			final Optional<Dir> result = Dir.ofLineBetween(start, square);
			assertEquals(Optional.empty(), result, square.name());
		});
	}

}
