/**
 *
 */
package jenjinn.parseutils;

import jenjinn.base.Dir;
import jenjinn.base.Square;
import jenjinn.pgn.CommonRegex;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

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
	public static Seq<Square> parse(String encodedCord)
	{
		String ec = encodedCord.trim();
		if (!ec.matches(CommonRegex.CORD)) {
			throw new IllegalArgumentException(encodedCord);
		}

		Seq<Square> squares = Strings.allMatches(ec, CommonRegex.SINGLE_SQUARE)
				.map(String::toUpperCase)
				.map(Square::valueOf)
				.toSeq();

		Square start = squares.head(), end = squares.last();
		Dir dir = Dir.ofLineBetween(start, end)
				.orElseThrow(() -> new IllegalArgumentException(encodedCord));

		return start.getAllSquares(dir, 10).flow()
				.takeWhile(sq -> sq != end)
				.insert(start)
				.append(end)
				.toSeq();
	}
}
