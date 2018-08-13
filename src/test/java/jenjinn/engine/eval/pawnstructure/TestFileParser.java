/**
 *
 */
package jenjinn.engine.eval.pawnstructure;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.last;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.pgn.CommonRegex;
import xawd.jflow.collections.FList;
import xawd.jflow.iterators.misc.IntPair;
import xawd.jflow.utilities.Strings;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String filename)
	{
		List<String> lines = loadFile(filename);

		if (lines.size() == 9) {
			String encodedWhiteLocs = head(lines), encodedBlackLocs = lines.get(1);
			Long whiteLocs = decodeLocations(encodedWhiteLocs);
			Long blackLocs = decodeLocations(encodedBlackLocs);

			IntPair doubledPawnCounts = decodeIntegerPair(lines.get(2));
			IntPair passedPawnCounts = decodeIntegerPair(lines.get(3));
			IntPair chainLinkCounts = decodeIntegerPair(lines.get(4));
			IntPair backwardCounts = decodeIntegerPair(lines.get(5));
			FList<Integer> isolatedPawnCounts = decodeIntegerSequence(lines.get(6));

			ExpectedValues expected = new ExpectedValues(
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

	private FList<Integer> decodeIntegerSequence(String encodedSequence)
	{
		String num = "([0-9]+)";
		if (!encodedSequence.matches("^" + num + "( " + num + ")+$")) {
			throw new IllegalArgumentException(encodedSequence);
		}
		return Strings.allMatches(encodedSequence, num)
				.map(Integer::parseInt)
				.toList();
	}

	private IntPair decodeIntegerPair(String encodedPair)
	{
		String num = "([0-9]+)";
		if (!encodedPair.matches("^" + num + " +" + num + "$")) {
			throw new IllegalArgumentException(encodedPair);
		}
		FList<Integer> decoded = Strings.allMatches(encodedPair, num)
				.map(Integer::parseInt)
				.toList();

		return IntPair.of(head(decoded), last(decoded));
	}

	private Long decodeLocations(String encodedLocs)
	{
		String sq = CommonRegex.SINGLE_SQUARE;
		if (!encodedLocs.matches("^" + sq + "( " + sq + ")*$")) {
			throw new IllegalArgumentException(encodedLocs);
		}
		return Strings.allMatches(encodedLocs, sq)
				.map(String::toUpperCase)
				.map(BoardSquare::valueOf)
				.mapToLong(BoardSquare::asBitboard)
				.fold(0L, (a, b) -> a | b);
	}
}
