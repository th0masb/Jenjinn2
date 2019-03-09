/**
 *
 */
package jenjinn.eval.pawnstructure;

import com.github.maumay.jflow.utils.IntTup;
import com.github.maumay.jflow.vec.Vec;

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
	private final IntTup isolatedPawnCountDifferences;
	private final Vec<Integer> whitePhalanxSizes;
	private final Vec<Integer> blackPhalanxSizes;

	public ExpectedValues(int doubledPawnCountDifference, int passedPawnCountDifference,
			int chainLinkCountDifference, int backwardCountDifference,
			IntTup isolatedPawnCountDifferences, Vec<Integer> whitePhalanxSizes,
			Vec<Integer> blackPhalanxSizes)
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

	public IntTup getIsolatedPawnCountDifferences()
	{
		return isolatedPawnCountDifferences;
	}

	public Vec<Integer> getWhitePhalanxSizes()
	{
		return whitePhalanxSizes;
	}

	public Vec<Integer> getBlackPhalanxSizes()
	{
		return blackPhalanxSizes;
	}
}
