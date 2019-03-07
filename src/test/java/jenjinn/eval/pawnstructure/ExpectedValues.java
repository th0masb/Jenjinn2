/**
 *
 */
package jenjinn.eval.pawnstructure;

import jflow.iterators.misc.IntPair;
import jflow.seq.Seq;

/**
 * @author ThomasB
 *
 */
public final class ExpectedValues
{
	private final int doubledPawnCountDifference;
	private final int passedPawnCountDifference;
	private final int chainLinkCountDifference;
	private final int backwardCountDifference;
	private final IntPair isolatedPawnCountDifferences;
	private final Seq<Integer> whitePhalanxSizes;
	private final Seq<Integer> blackPhalanxSizes;

	public ExpectedValues(
			int doubledPawnCountDifference,
			int passedPawnCountDifference,
			int chainLinkCountDifference,
			int backwardCountDifference,
			IntPair isolatedPawnCountDifferences,
			Seq<Integer> whitePhalanxSizes,
			Seq<Integer> blackPhalanxSizes)
	{
		this.doubledPawnCountDifference = doubledPawnCountDifference;
		this.passedPawnCountDifference = passedPawnCountDifference;
		this.chainLinkCountDifference = chainLinkCountDifference;
		this.backwardCountDifference = backwardCountDifference;
		this.isolatedPawnCountDifferences = isolatedPawnCountDifferences;
		this.whitePhalanxSizes = whitePhalanxSizes;
		this.blackPhalanxSizes = blackPhalanxSizes;
	}

	public int getDoubledPawnCountDifference()
	{
		return doubledPawnCountDifference;
	}

	public int getPassedPawnCountDifference()
	{
		return passedPawnCountDifference;
	}

	public int getChainLinkCountDifference()
	{
		return chainLinkCountDifference;
	}

	public int getBackwardCountDifference()
	{
		return backwardCountDifference;
	}

	public IntPair getIsolatedPawnCountDifferences()
	{
		return isolatedPawnCountDifferences;
	}

	public Seq<Integer> getWhitePhalanxSizes()
	{
		return whitePhalanxSizes;
	}

	public Seq<Integer> getBlackPhalanxSizes()
	{
		return blackPhalanxSizes;
	}
}
