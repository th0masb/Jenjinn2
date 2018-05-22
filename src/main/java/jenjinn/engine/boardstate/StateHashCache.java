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

	// For checking repetition draws
	private boolean enoughMovesPlayedToDraw = false;

	private StateHashCache() {
	}

	public static StateHashCache getGameStartCache()
	{
		throw new RuntimeException();
	}

	public void insertHashValue(final long hash, final int overallClockValue)
	{
		assert overallClockValue > 0;
		hashCache[overallClockValue % CACHE_SIZE] = hash;
		enoughMovesPlayedToDraw = overallClockValue > 8;
	}

	public long getHashValueAt(final int overallClockValue)
	{
		assert overallClockValue > 0;
		return hashCache[overallClockValue % CACHE_SIZE];
	}
}
