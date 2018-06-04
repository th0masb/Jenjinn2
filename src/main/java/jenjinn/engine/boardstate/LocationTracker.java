/**
 *
 */
package jenjinn.engine.boardstate;

import static java.lang.Math.min;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.enums.BoardSquare;
import xawd.jflow.iterators.AbstractFlow;
import xawd.jflow.iterators.Flow;

/**
 * @author t
 *
 */
public final class LocationTracker implements Iterable<BoardSquare>
{
	private final BoardSquare[] locs = new BoardSquare[9];
	private int pieceCount = 0;

	private long allLocs;

	public LocationTracker(final Set<BoardSquare> locations)
	{
		pieceCount = min(9, locations.size());
		System.arraycopy(locations.toArray(), 0, locs, 0, pieceCount);
		allLocs = iterator().mapToLong(BoardSquare::asBitboard).reduce(0L, (a, b) -> a | b);
	}

	public LocationTracker(final long locations)
	{
		this(BitboardIterator.from(locations).toSet());
	}

	public long allLocs()
	{
		return allLocs;
	}

	public boolean contains(final BoardSquare location)
	{
		return bitboardsIntersect(allLocs, location.asBitboard());
	}

	public int pieceCount()
	{
		return pieceCount;
	}

	void addLoc(final BoardSquare location)
	{
		assert !bitboardsIntersect(allLocs, location.asBitboard());
		allLocs ^= location.asBitboard();
		locs[pieceCount] = location;
		pieceCount++;
	}

	void removeLoc(final BoardSquare location)
	{
		assert bitboardsIntersect(allLocs, location.asBitboard());
		allLocs ^= location.asBitboard();
		int index = -1;
		for (int i = 0; i < pieceCount; i++) {
			if (locs[i] == location) {
				index = i;
				break;
			}
		}
		for (int i = index + 1; i < pieceCount; i++) {
			locs[i - 1] = locs[i];
		}
		pieceCount--;
	}

	/**
	 * Note that this iterator makes no guarantee about the order in
	 * which squares appear in the iteration.
	 */
	@Override
	public Flow<BoardSquare> iterator()
	{
		return new AbstractFlow<BoardSquare>() {
			int count = 0;
			@Override
			public boolean hasNext() {
				return count < pieceCount;
			}
			@Override
			public BoardSquare next() {
				if (count++ >= pieceCount) {
					throw new NoSuchElementException();
				}
				else {
					return locs[count];
				}
			}
			@Override
			public void skip() {
				next();
			}
		};
	}

	public LocationTracker copy()
	{
		return new LocationTracker(new HashSet<>(Arrays.asList(locs).subList(0, pieceCount)));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (allLocs ^ (allLocs >>> 32));
		result = prime * result + iterator().toList().hashCode();
		result = prime * result + pieceCount;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final LocationTracker other = (LocationTracker) obj;
		if (allLocs != other.allLocs)
			return false;
		if (pieceCount != other.pieceCount)
			return false;
		if (!iterator().toSet().equals(other.iterator().toSet()))
			return false;
		return true;
	}
}
