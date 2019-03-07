/**
 *
 */
package jenjinn.eval.pawnstructure;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.base.Square;
import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.pgn.CommonRegex;
import jflow.iterators.misc.IntPair;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String filename)
	{
		Seq<String> lines = loadFile(filename);

		if (lines.size() == 9) {
			String encodedWhiteLocs = lines.head(), encodedBlackLocs = lines.get(1);
			Long whiteLocs = decodeLocations(encodedWhiteLocs);
			Long blackLocs = decodeLocations(encodedBlackLocs);

			IntPair doubledPawnCounts = decodeIntegerPair(lines.get(2));
			IntPair passedPawnCounts = decodeIntegerPair(lines.get(3));
			IntPair chainLinkCounts = decodeIntegerPair(lines.get(4));
			IntPair backwardCounts = decodeIntegerPair(lines.get(5));
			Seq<Integer> isolatedPawnCounts = decodeIntegerSequence(lines.get(6));

			ExpectedValues expected = new ExpectedValues(
					doubledPawnCounts._1 - doubledPawnCounts._2,
					passedPawnCounts._1 - passedPawnCounts._2,
					chainLinkCounts._1 - chainLinkCounts._2,
					backwardCounts._1 - backwardCounts._2,
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

	private Seq<Integer> decodeIntegerSequence(String encodedSequence)
	{
		String num = "([0-9]+)";
		if (!encodedSequence.matches("^" + num + "( " + num + ")+$")) {
			throw new IllegalArgumentException(encodedSequence);
		}
		return Strings.allMatches(encodedSequence, num)
				.map(Integer::parseInt)
				.toSeq();
	}

	private IntPair decodeIntegerPair(String encodedPair)
	{
		String num = "([0-9]+)";
		if (!encodedPair.matches("^" + num + " +" + num + "$")) {
			throw new IllegalArgumentException(encodedPair);
		}
		Seq<Integer> decoded = Strings.allMatches(encodedPair, num)
				.map(Integer::parseInt)
				.toSeq();

		return IntPair.of(decoded.head(), decoded.last());
	}

	private Long decodeLocations(String encodedLocs)
	{
		String sq = CommonRegex.SINGLE_SQUARE;
		if (!encodedLocs.matches("^" + sq + "( " + sq + ")*$")) {
			throw new IllegalArgumentException(encodedLocs);
		}
		return Strings.allMatches(encodedLocs, sq)
				.map(String::toUpperCase)
				.map(Square::valueOf)
				.mapToLong(s -> s.bitboard)
				.fold(0L, (a, b) -> a | b);
	}
}
