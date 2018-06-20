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
	private final IntPair doubledPawnCounts;
	private final IntPair isolatedPawnCounts;
	private final IntPair passedPawnCounts;
	private final IntPair chainLinkCounts;
	private final FlowList<Integer> whitePhalanxSizes;
	private final FlowList<Integer> blackPhalanxSizes;

	public ExpectedValues(
			IntPair doubledPawnCounts,
			IntPair isolatedPawnCounts,
			IntPair passedPawnCounts,
			IntPair chainLinkCounts,
			FlowList<Integer> whitePhalanxSizes,
			FlowList<Integer> blackPhalanxSizes)
	{
		this.doubledPawnCounts = doubledPawnCounts;
		this.isolatedPawnCounts = isolatedPawnCounts;
		this.passedPawnCounts = passedPawnCounts;
		this.chainLinkCounts = chainLinkCounts;
		this.whitePhalanxSizes = whitePhalanxSizes;
		this.blackPhalanxSizes = blackPhalanxSizes;
	}

	public IntPair getDoubledPawnCounts()
	{
		return doubledPawnCounts;
	}

	public IntPair getIsolatedPawnCounts()
	{
		return isolatedPawnCounts;
	}

	public IntPair getPassedPawnCounts()
	{
		return passedPawnCounts;
	}

	public IntPair getChainLinkCounts()
	{
		return chainLinkCounts;
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
