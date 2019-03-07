/**
 *
 */
package jenjinn.boardstate;

import static jenjinn.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import jenjinn.base.Square;
import jenjinn.bitboards.BitboardIterator;
import jflow.iterators.Flow;
import jflow.iterators.factories.Iter;

/**
 * @author t
 *
 */
public final class LocationTracker implements Iterable<Square>
{
	private final Set<Square> locs = EnumSet.noneOf(Square.class);
	private long allLocs;

	public LocationTracker(Set<Square> locations)
	{
		locs.addAll(locations);
		allLocs = iterator().mapToLong(sq -> sq.bitboard).fold(0L, (a, b) -> a | b);
	}

	public LocationTracker(long locations)
	{
		this(BitboardIterator.from(locations).toSet());
	}

	public long allLocs()
	{
		return allLocs;
	}

	public boolean contains(Square location)
	{
		return bitboardsIntersect(allLocs, location.bitboard);
	}

	public int pieceCount()
	{
		return locs.size();
	}

	void addLoc(Square location)
	{
		assert !bitboardsIntersect(allLocs, location.bitboard);
		allLocs ^= location.bitboard;
		locs.add(location);
	}

	void removeLoc(Square location)
	{
		assert bitboardsIntersect(allLocs, location.bitboard);
		allLocs ^= location.bitboard;
		locs.remove(location);
	}

	/**
	 * Note that this iterator makes no guarantee about the order in
	 * which squares appear in the iteration.
	 */
	@Override
	public Flow<Square> iterator()
	{
		return Iter.over(locs);
	}

	public LocationTracker copy()
	{
		return new LocationTracker(new HashSet<>(locs));
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + (int) (allLocs ^ (allLocs >>> 32));
		result = prime * result + ((locs == null) ? 0 : locs.hashCode());
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
		LocationTracker other = (LocationTracker) obj;
		if (allLocs != other.allLocs)
			return false;
		if (locs == null) {
			if (other.locs != null)
				return false;
		} else if (!locs.equals(other.locs))
			return false;
		return true;
	}
}
