/**
 *
 */
package jenjinn.engine.boardstate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static xawd.jflow.utilities.StringUtils.findFirstMatch;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.List;
import java.util.regex.Pattern;

import xawd.jflow.iterators.construction.Iterate;

/**
 * @author t
 *
 */
public final class BoardParseUtils {

	private BoardParseUtils() {}

	public static BoardState parseBoard(List<String> attributes)
	{
		attributes = Iterate.over(attributes).map(String::trim).toList();

		final DetailedPieceLocations pieceLocations = reconstructPieceLocations(
				attributes.get(0),
				attributes.get(1));

		//		BoardState constructedState = new Bo
		throw new RuntimeException();
	}

	public static HalfMoveClock reconstructHalfMoveClock(String clockString)
	{
		assertTrue(clockString.trim().matches("^half_move_clock: *[0-9]+$"));
		return new HalfMoveClock(Integer.parseInt(findFirstMatch(clockString, "[0-9]+").get()));
	}

	public static DetailedPieceLocations reconstructPieceLocations(String whiteLocs, String blackLocs)
	{
		assertTrue(whiteLocs.trim().matches("^white_locs:.*$"));
		assertTrue(blackLocs.trim().matches("^black_locs:.*$"));

		final Pattern squarePattern = Pattern.compile("[a-hA-H1-8]{2}");
		final Pattern squareSequencePattern = Pattern.compile("\\([a-hA-H1-8, ]*\\)");

		throw new RuntimeException();
		//		return Iterate.over(getAllMatches(whiteLocs + blackLocs, squareSequencePattern))
		//				.map(x -> getAllMatches(x, squarePattern))
		//				.map(xs -> objMap(String::toUpperCase, xs))
		//				.map(xs -> objMap(BoardSquare::valueOf, xs))
		//				.map(BitboardUtils::bitwiseOr)
		//				.build(DetailedPieceLocations::new);
	}

	public static void main(String[] args)
	{
		System.out.println(getAllMatches("(), (e3, A8), (R5, a5), (", "\\([a-hA-H1-8, ]*\\)"));
	}
}
