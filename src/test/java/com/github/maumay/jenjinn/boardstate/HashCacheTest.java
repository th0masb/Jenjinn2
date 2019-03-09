/**
 *
 */
package com.github.maumay.jenjinn.boardstate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jenjinn.boardstate.HashCache;
import com.github.maumay.jflow.iterators.EnhancedIterator;
import com.github.maumay.jflow.iterators.factories.Iter;
import com.github.maumay.jflow.utils.Strings;

/**
 * @author t
 */
class HashCacheTest
{
	@Test
	void testIncrementClockValue()
	{
		final int cacheSize = HashCache.CACHE_SIZE;
		final HashCache start = new HashCache(
				Iter.until(cacheSize).mapToLong(x -> x).toArray(), 0);

		for (int i = 0; i < 2 * cacheSize; i++) {
			final long expectedDiscard = i < cacheSize - 1 ? i + 1 : 0;
			Assertions.assertEquals(expectedDiscard, start.incrementHalfMoveCount(0L));
			final long[] expectedCache = Iter.until(cacheSize).mapToLong(x -> x)
					.toArray();
			Iter.until(i + 2).take(cacheSize).forEach(j -> expectedCache[j] = 0);
			Assertions.assertEquals(new HashCache(expectedCache, i + 1), start);
		}
	}

	@Test
	void testDecrementClockValue()
	{
		final int cacheSize = HashCache.CACHE_SIZE;
		final HashCache start = new HashCache(
				Iter.until(cacheSize).mapToLong(x -> -1L).toArray(), cacheSize);

		final long[] replacementHashes = Iter.until(cacheSize + 1).mapToLong(x -> x + 1)
				.toArray();
		for (int i = 0; i < replacementHashes.length; i++) {
			start.decrementHalfMoveCount(replacementHashes[i]);
			final long[] expectedCache = Iter.until(cacheSize - 1).mapToLong(x -> -1L)
					.insert(replacementHashes[0]).toArray();
			Iter.between(1, i + 1)
					.forEach(j -> expectedCache[cacheSize - j] = replacementHashes[j]);
			Assertions.assertEquals(new HashCache(expectedCache, cacheSize - (i + 1)),
					start, Strings.str(i));
		}
	}

	@ParameterizedTest
	@MethodSource
	void testContainsThreeRepetitions(final long[] rawCache, final Integer totalMoveCount,
			final Boolean expectedOutcome)
	{
		assertEquals(HashCache.CACHE_SIZE, rawCache.length);
		final HashCache cache = new HashCache(rawCache, totalMoveCount.intValue());
		assertEquals(expectedOutcome, cache.containsThreeRepetitions());
	}

	static EnhancedIterator<Arguments> testContainsThreeRepetitions()
	{
		return Iter.over(
				Arguments.of(new long[] { 0, 1, 2, 4, 1, 5, 5, 7, 9, 100, -23, 1 }, 12,
						Boolean.TRUE),
				Arguments.of(new long[] { 0, 1, 2, 4, 1, 5, 5, 7, 9, 100, -23, 1 }, 11,
						Boolean.FALSE),
				Arguments.of(new long[] { 0, 1, 2, 4, 1, 5, 5, 7, 9, 100, -23, 2 }, 20,
						Boolean.FALSE),
				Arguments.of(new long[] { 2, 1, 2, 4, 1, 5, 5, 5, 9, 100, -23, 2 }, 20,
						Boolean.TRUE));
	}
}
