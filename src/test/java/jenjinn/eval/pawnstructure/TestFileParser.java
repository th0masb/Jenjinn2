/**
 *
 */
package jenjinn.eval.pawnstructure;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jflow.utils.IntTup;
import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.Square;
import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.pgn.CommonRegex;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String filename)
	{
		Vec<String> lines = loadFile(filename);

		if (lines.size() == 9) {
			String encodedWhiteLocs = lines.head(), encodedBlackLocs = lines.get(1);
			Long whiteLocs = decodeLocations(encodedWhiteLocs);
			Long blackLocs = decodeLocations(encodedBlackLocs);

			IntTup doubledPawnCounts = decodeIntegerTup(lines.get(2));
			IntTup passedPawnCounts = decodeIntegerTup(lines.get(3));
			IntTup chainLinkCounts = decodeIntegerTup(lines.get(4));
			IntTup backwardCounts = decodeIntegerTup(lines.get(5));
			Vec<Integer> isolatedPawnCounts = decodeIntegerVecuence(lines.get(6));

			ExpectedValues expected = new ExpectedValues(
					doubledPawnCounts._1 - doubledPawnCounts._2,
					passedPawnCounts._1 - passedPawnCounts._2,
					chainLinkCounts._1 - chainLinkCounts._2,
					backwardCounts._1 - backwardCounts._2,
					IntTup.of(isolatedPawnCounts.get(0) - isolatedPawnCounts.get(2),
							isolatedPawnCounts.get(1) - isolatedPawnCounts.get(3)),
					decodeIntegerVecuence(lines.get(7)),
					decodeIntegerVecuence(lines.get(8)));

			return Arguments.of(whiteLocs, blackLocs, expected);
		} else {
			throw new IllegalArgumentException(filename + " is formatted incorrectly.");
		}
	}

	private Vec<Integer> decodeIntegerVecuence(String encodedVecuence)
	{
		String num = "([0-9]+)";
		if (!encodedVecuence.matches("^" + num + "( " + num + ")+$")) {
			throw new IllegalArgumentException(encodedVecuence);
		}
		return Strings.allMatches(encodedVecuence, num).map(Integer::parseInt).toVec();
	}

	private IntTup decodeIntegerTup(String encodedTup)
	{
		String num = "([0-9]+)";
		if (!encodedTup.matches("^" + num + " +" + num + "$")) {
			throw new IllegalArgumentException(encodedTup);
		}
		Vec<Integer> decoded = Strings.allMatches(encodedTup, num).map(Integer::parseInt)
				.toVec();

		return IntTup.of(decoded.head(), decoded.last());
	}

	private Long decodeLocations(String encodedLocs)
	{
		String sq = CommonRegex.SINGLE_SQUARE;
		if (!encodedLocs.matches("^" + sq + "( " + sq + ")*$")) {
			throw new IllegalArgumentException(encodedLocs);
		}
		return Strings.allMatches(encodedLocs, sq).map(String::toUpperCase)
				.map(Square::valueOf).mapToLong(s -> s.bitboard)
				.fold(0L, (a, b) -> a | b);
	}
}
