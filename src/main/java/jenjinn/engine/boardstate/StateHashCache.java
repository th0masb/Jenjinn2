/**
 *
 */
package jenjinn.engine.boardstate;

/**
 * @author ThomasB
 */
public final class StateHashCache
{
	private static final int CACHE_SIZE = 12;

	private final long[] hashCache = new long[CACHE_SIZE];
	private int totalHalfMoveCount = 0, cacheIndexer = 0;

	private StateHashCache() {
	}

	public static StateHashCache getGameStartCache()
	{
		throw new RuntimeException();
	}

	public long incrementHalfMoveCount()
	{
		final long currentHash = hashCache[cacheIndexer];
		totalHalfMoveCount++;
		updateCacheIndexer();
		final long discardedHash = hashCache[cacheIndexer];
		hashCache[cacheIndexer] = currentHash;
		return discardedHash;
	}

	public void decrementHalfMoveCount(final long replacementHash)
	{
		hashCache[cacheIndexer] = replacementHash;
		totalHalfMoveCount--;
		updateCacheIndexer();
	}

	private void updateCacheIndexer()
	{
		cacheIndexer = totalHalfMoveCount % CACHE_SIZE;
	}

	public void xorFeatureWithCurrentHash(final long feature)
	{
		hashCache[cacheIndexer] ^= feature;
	}
}
