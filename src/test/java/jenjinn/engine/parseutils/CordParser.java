/**
 *
 */
package jenjinn.engine.parseutils;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.last;
import static xawd.jflow.utilities.Strings.allMatches;

import java.util.List;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Direction;
import jenjinn.engine.pgn.CommonRegex;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public final class CordParser
{
	private CordParser()
	{
	}

	/**
	 * Given a string in the form {@code A2->A5} we construct a list of boardsquares
	 * connecting the two squares in a straight line cord.
	 *
	 * @param encodedCord
	 *            The string defining the cord.
	 * @return A list of boardsquares connecting the start and end squares
	 *         (inclusive).
	 * @throws IllegalArgumentException If no cord connects the two squares.
	 */
	public static List<BoardSquare> parse(String encodedCord)
	{
		String ec = encodedCord.trim();
		if (!ec.matches(CommonRegex.CORD)) {
			throw new IllegalArgumentException(encodedCord);
		}

		List<BoardSquare> squares = allMatches(ec, CommonRegex.SINGLE_SQUARE)
				.map(String::toUpperCase)
				.map(BoardSquare::valueOf)
				.toList();

		BoardSquare start = head(squares), end = last(squares);
		Direction dir = Direction.ofLineBetween(start, end)
				.orElseThrow(() -> new IllegalArgumentException(encodedCord));

		return Iterate.over(start.getAllSquaresInDirections(dir, 10))
				.takeWhile(sq -> sq != end)
				.insert(start)
				.append(end)
				.toList();
	}
}
