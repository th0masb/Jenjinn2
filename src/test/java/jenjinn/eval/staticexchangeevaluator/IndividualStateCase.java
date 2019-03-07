/**
 *
 */
package jenjinn.eval.staticexchangeevaluator;

import jenjinn.base.Square;
import jenjinn.pgn.CommonRegex;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

/**
 * @author ThomasB
 *
 */
final class IndividualStateCase
{
	public final Square source, target;
	public final boolean isGoodExchange;

	public IndividualStateCase(Square source, Square target, boolean isGoodExchange)
	{
		this.source = source;
		this.target = target;
		this.isGoodExchange = isGoodExchange;
	}


	public static IndividualStateCase from(String encoded)
	{
		String sq = CommonRegex.SINGLE_SQUARE, doubleSq = CommonRegex.DOUBLE_SQUARE;
		if (encoded.matches("^" + doubleSq + " +(GOOD|BAD)$")) {
			Seq<Square> sqs = Strings.allMatches(encoded, sq)
					.map(String::toUpperCase)
					.map(Square::valueOf)
					.toSeq();
			return new IndividualStateCase(sqs.head(), sqs.last(), Strings.firstMatch(encoded, "GOOD").isPresent());
		}
		else {
			throw new IllegalArgumentException(encoded);
		}
	}

	@Override
	public String toString()
	{
		return new StringBuilder("[source=")
				.append(source)
				.append(", target=")
				.append(target)
				.append(", result=")
				.append(isGoodExchange)
				.append("]")
				.toString();
	}
}
