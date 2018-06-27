/**
 *
 */
package jenjinn.engine.eval.pawnstructure;

import xawd.jflow.collections.FlowList;
import xawd.jflow.iterators.misc.IntPair;

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
	private final FlowList<Integer> whitePhalanxSizes;
	private final FlowList<Integer> blackPhalanxSizes;

	public ExpectedValues(
			int doubledPawnCountDifference,
			int passedPawnCountDifference,
			int chainLinkCountDifference,
			int backwardCountDifference,
			IntPair isolatedPawnCountDifferences,
			FlowList<Integer> whitePhalanxSizes,
			FlowList<Integer> blackPhalanxSizes)
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

	public FlowList<Integer> getWhitePhalanxSizes()
	{
		return whitePhalanxSizes;
	}

	public FlowList<Integer> getBlackPhalanxSizes()
	{
		return blackPhalanxSizes;
	}
}
