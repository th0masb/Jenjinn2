/**
 *
 */
package jenjinn.engine.eval.staticexchangeevaluator;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.StringUtils.findFirstMatch;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.parseutils.CommonRegex;
import xawd.jflow.collections.FlowList;

/**
 * @author ThomasB
 *
 */
final class IndividualStateCase
{
	public final BoardSquare source, target;
	public final boolean isGoodExchange;

	public IndividualStateCase(BoardSquare source, BoardSquare target, boolean isGoodExchange)
	{
		this.source = source;
		this.target = target;
		this.isGoodExchange = isGoodExchange;
	}


	public static IndividualStateCase from(String encoded)
	{
		final String sq = CommonRegex.SINGLE_SQUARE, doubleSq = CommonRegex.DOUBLE_SQUARE;
		if (encoded.matches("^" + doubleSq + " +(GOOD|BAD)$")) {
			final FlowList<BoardSquare> sqs = getAllMatches(encoded, sq)
					.map(String::toUpperCase)
					.map(BoardSquare::valueOf)
					.toList();
			return new IndividualStateCase(head(sqs), tail(sqs), findFirstMatch(encoded, "GOOD").isPresent());
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
