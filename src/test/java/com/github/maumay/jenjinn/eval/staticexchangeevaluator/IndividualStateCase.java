/**
 *
 */
package com.github.maumay.jenjinn.eval.staticexchangeevaluator;

import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.pgn.CommonRegex;
import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.vec.Vec;

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
			Vec<Square> sqs = Strings.allMatches(encoded, sq).map(String::toUpperCase)
					.map(Square::valueOf).toVec();
			return new IndividualStateCase(sqs.head(), sqs.last(),
					Strings.firstMatch(encoded, "GOOD").isPresent());
		} else {
			throw new IllegalArgumentException(encoded);
		}
	}

	@Override
	public String toString()
	{
		return new StringBuilder("[source=").append(source).append(", target=")
				.append(target).append(", result=").append(isGoodExchange).append("]")
				.toString();
	}
}
