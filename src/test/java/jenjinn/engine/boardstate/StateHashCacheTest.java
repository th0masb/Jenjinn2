/**
 *
 */
package jenjinn.engine.boardstate;

import static xawd.jflow.utilities.CollectionUtil.str;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import xawd.jflow.iterators.construction.IterRange;

/**
 * @author t
 */
class StateHashCacheTest
{
	@Test
	void testIncrementClockValue()
	{
		final int cacheSize = HashCache.CACHE_SIZE;
		final HashCache start = new HashCache(IterRange.to(cacheSize).mapToLong(x -> x).toArray(), 0);

		for (int i = 0; i < 2*cacheSize; i++)
		{
			final long expectedDiscard = i < cacheSize - 1 ? i + 1 : 0;
			Assertions.assertEquals(expectedDiscard, start.incrementHalfMoveCount(0L));
			final long[] expectedCache = IterRange.to(cacheSize).mapToLong(x -> x).toArray();
			IterRange.to(i + 2).take(cacheSize).forEach(j -> expectedCache[j] = 0);
			Assertions.assertEquals(new HashCache(expectedCache, i + 1), start);
		}
	}

	@Test
	void testDecrementClockValue()
	{
		final int cacheSize = HashCache.CACHE_SIZE;
		final HashCache start = new HashCache(IterRange.to(cacheSize).mapToLong(x -> -1L).toArray(), cacheSize);

		final long[] replacementHashes = IterRange.to(cacheSize + 1).mapToLong(x -> x + 1).toArray();
		for (int i = 0; i < replacementHashes.length; i++)
		{
			start.decrementHalfMoveCount(replacementHashes[i]);
			final long[] expectedCache = IterRange.to(cacheSize - 1).mapToLong(x -> -1L).insert(replacementHashes[0]).toArray();
			IterRange.between(1, i + 1).forEach(j -> expectedCache[cacheSize - j] = replacementHashes[j]);
			Assertions.assertEquals(new HashCache(expectedCache, cacheSize - (i + 1)), start, str(i));
		}
	}
}
