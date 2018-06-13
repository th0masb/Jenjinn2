/**
 *
 */
package jenjinn.engine.parseutils;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public final class CordParser
{
	private static final String SQUARE_REGEX = "[a-hA-H][1-8]";

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
	public static List<BoardSquare> parse(final String encodedCord)
	{
		final String ec = encodedCord.trim();
		if (!ec.matches(SQUARE_REGEX + " *\\-\\> *" + SQUARE_REGEX)) {
			throw new IllegalArgumentException(encodedCord);
		}

		final List<BoardSquare> squares = Iterate.over(getAllMatches(ec, SQUARE_REGEX))
				.map(String::toUpperCase)
				.map(BoardSquare::valueOf)
				.toList();

		final BoardSquare start = head(squares), end = tail(squares);
		final Direction dir = Direction.ofLineBetween(start, end)
				.orElseThrow(() -> new IllegalArgumentException(encodedCord));

		return Iterate.over(start.getAllSquaresInDirections(dir, 10))
				.takeWhile(sq -> sq != end)
				.insert(start)
				.append(end)
				.toList();
	}

	public static void main(final String[] args)
	{
		System.out.println(Iterate.over("1").append("2").toList());
	}
}
