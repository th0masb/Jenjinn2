/**
 *
 */
package jenjinn.engine.boardstate;

import static java.lang.Math.min;
import static jenjinn.engine.bitboards.BitboardUtils.getSetBitIndices;
import static xawd.jflow.utilities.PredicateUtil.any;

import java.util.Arrays;

import jenjinn.engine.enums.BoardSquare;
import xawd.jflow.iterators.IntFlow;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author t
 *
 */
public final class LocationTracker {

	private final int[] locs = new int[9];
	private int pieceCount = 0;

	public LocationTracker(int[] locations)
	{
		if (any(i -> i < 0 || i > 63, locations)) {
			throw new IllegalArgumentException();
		}
		pieceCount = min(9, locations.length);
		System.arraycopy(locations, 0, locs, 0, pieceCount);
	}

	public LocationTracker(long locations)
	{
		this(getSetBitIndices(locations));
	}

	public long allLocs()
	{
		long allLocs = 0L;
		for (int i = 0; i < pieceCount; i++) {
			allLocs |= 1L << locs[i];
		}
		return allLocs;
	}

	public boolean contains(BoardSquare location)
	{
		for (int i = 0; i < pieceCount; i++) {
			if (locs[i] == location.ordinal()) {
				return true;
			}
		}
		return false;
	}

	public int indexLocs(int queryIndex)
	{
		return locs[queryIndex];
	}

	public int pieceCount()
	{
		return pieceCount;
	}

	public IntFlow iterateLocations()
	{
		return Iterate.over(locs).take(pieceCount);
	}

	public LocationTracker copy()
	{
		return new LocationTracker(Arrays.copyOf(locs, pieceCount));
	}

	void addLoc(BoardSquare location)
	{
		locs[pieceCount] = location.ordinal();
		pieceCount++;
	}

	void removeLoc(BoardSquare location)
	{
		assert pieceCount > 0;
		int index = -1;
		for (int i = 0; i < pieceCount; i++) {
			if (locs[i] == location.ordinal()) {
				index = i;
				break;
			}
		}
		assert index > -1;
		for (int i = index + 1; i < pieceCount; i++) {
			locs[i - 1] = locs[i];
		}
		pieceCount--;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(locs);
		result = prime * result + pieceCount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final LocationTracker other = (LocationTracker) obj;
		if (!Arrays.equals(locs, other.locs))
			return false;
		if (pieceCount != other.pieceCount)
			return false;
		return true;
	}
}
