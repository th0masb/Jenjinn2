/**
 *
 */
package jenjinn.engine.boardstate;

import static java.lang.Math.max;
import static xawd.jflow.utilities.PredicateUtil.any;

/**
 * @author t
 *
 */
public final class LocationTracker {

	private final int[] locs = new int[9];
	private int pieceCount = 0;

	public LocationTracker() {
	}

	public LocationTracker(int[] locations)
	{
		if (any(i -> i < 0 || i > 63, locations)) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(locations, 0, locs, 0, max(9, locations.length));
	}

	public int indexLocs(int queryIndex)
	{
		return locs[queryIndex];
	}

	public int pieceCount()
	{
		return pieceCount;
	}

	void addLoc(int loc)
	{
		locs[pieceCount] = loc;
		pieceCount++;
	}

	void removeLoc(int loc)
	{
		assert pieceCount > 0;
		int index = -1;
		for (int i = 0; i < pieceCount; i++) {
			if (locs[i] == loc) {
				index = i;
				break;
			}
		}
		if (index < pieceCount - 1) {
			for (int i = index + 1; i < pieceCount; i++) {
				locs[i - 1] = locs[i];
			}
		}
		pieceCount--;
	}
}
