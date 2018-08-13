/**
 *
 */
package jenjinn.engine.eval.staticexchangeevaluator;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.last;
import static xawd.jflow.utilities.Strings.allMatches;
import static xawd.jflow.utilities.Strings.firstMatch;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.pgn.CommonRegex;
import xawd.jflow.collections.FList;

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
		String sq = CommonRegex.SINGLE_SQUARE, doubleSq = CommonRegex.DOUBLE_SQUARE;
		if (encoded.matches("^" + doubleSq + " +(GOOD|BAD)$")) {
			FList<BoardSquare> sqs = allMatches(encoded, sq)
					.map(String::toUpperCase)
					.map(BoardSquare::valueOf)
					.toList();
			return new IndividualStateCase(head(sqs), last(sqs), firstMatch(encoded, "GOOD").isPresent());
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
