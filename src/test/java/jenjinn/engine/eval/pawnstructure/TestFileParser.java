/**
 *
 */
package jenjinn.engine.eval.pawnstructure;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.pgn.CommonRegex;
import xawd.jflow.collections.FlowList;
import xawd.jflow.iterators.misc.IntPair;
import xawd.jflow.utilities.Strings;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	public Arguments parse(String filename)
	{
		final List<String> lines = loadFile(filename);

		if (lines.size() == 9) {
			final String encodedWhiteLocs = head(lines), encodedBlackLocs = lines.get(1);
			final Long whiteLocs = decodeLocations(encodedWhiteLocs);
			final Long blackLocs = decodeLocations(encodedBlackLocs);

			final IntPair doubledPawnCounts = decodeIntegerPair(lines.get(2));
			final IntPair passedPawnCounts = decodeIntegerPair(lines.get(3));
			final IntPair chainLinkCounts = decodeIntegerPair(lines.get(4));
			final IntPair backwardCounts = decodeIntegerPair(lines.get(5));
			final FlowList<Integer> isolatedPawnCounts = decodeIntegerSequence(lines.get(6));

			final ExpectedValues expected = new ExpectedValues(
					doubledPawnCounts.first() - doubledPawnCounts.second(),
					passedPawnCounts.first() - passedPawnCounts.second(),
					chainLinkCounts.first() - chainLinkCounts.second(),
					backwardCounts.first() - backwardCounts.second(),
					IntPair.of(
							isolatedPawnCounts.get(0) - isolatedPawnCounts.get(2),
							isolatedPawnCounts.get(1) - isolatedPawnCounts.get(3)),
					decodeIntegerSequence(lines.get(7)),
					decodeIntegerSequence(lines.get(8)));

			return Arguments.of(whiteLocs, blackLocs, expected);
		}
		else {
			throw new IllegalArgumentException(filename + " is formatted incorrectly.");
		}
	}

	private FlowList<Integer> decodeIntegerSequence(String encodedSequence)
	{
		final String num = "([0-9]+)";
		if (!encodedSequence.matches("^" + num + "( " + num + ")+$")) {
			throw new IllegalArgumentException(encodedSequence);
		}
		return Strings.getAllMatches(encodedSequence, num)
				.map(Integer::parseInt)
				.toList();
	}

	private IntPair decodeIntegerPair(String encodedPair)
	{
		final String num = "([0-9]+)";
		if (!encodedPair.matches("^" + num + " +" + num + "$")) {
			throw new IllegalArgumentException(encodedPair);
		}
		final FlowList<Integer> decoded = Strings.getAllMatches(encodedPair, num)
				.map(Integer::parseInt)
				.toList();

		return IntPair.of(head(decoded), tail(decoded));
	}

	private Long decodeLocations(String encodedLocs)
	{
		final String sq = CommonRegex.SINGLE_SQUARE;
		if (!encodedLocs.matches("^" + sq + "( " + sq + ")*$")) {
			throw new IllegalArgumentException(encodedLocs);
		}
		return Strings.getAllMatches(encodedLocs, sq)
				.map(String::toUpperCase)
				.map(BoardSquare::valueOf)
				.mapToLong(BoardSquare::asBitboard)
				.fold(0L, (a, b) -> a | b);
	}
}
