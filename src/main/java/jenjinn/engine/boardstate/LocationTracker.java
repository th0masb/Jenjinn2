/**
 *
 */
package jenjinn.engine.boardstate;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.bitboards.BitboardIterator;
import xawd.jflow.iterators.AbstractFlow;
import xawd.jflow.iterators.Flow;
import xawd.jflow.utilities.Optionals;

/**
 * @author t
 *
 */
public final class LocationTracker implements Iterable<BoardSquare>
{
	private final Set<BoardSquare> locs = EnumSet.noneOf(BoardSquare.class);
	private long allLocs;

	public LocationTracker(Set<BoardSquare> locations)
	{
		locs.addAll(locations);
		allLocs = iterator().mapToLong(BoardSquare::asBitboard).fold(0L, (a, b) -> a | b);
	}

	public LocationTracker(long locations)
	{
		this(BitboardIterator.from(locations).toSet());
	}

	public long allLocs()
	{
		return allLocs;
	}

	public boolean contains(BoardSquare location)
	{
		return bitboardsIntersect(allLocs, location.asBitboard());
	}

	public int pieceCount()
	{
		return locs.size();
	}

	void addLoc(BoardSquare location)
	{
		assert !bitboardsIntersect(allLocs, location.asBitboard());
		allLocs ^= location.asBitboard();
		locs.add(location);
	}

	void removeLoc(BoardSquare location)
	{
		assert bitboardsIntersect(allLocs, location.asBitboard());
		allLocs ^= location.asBitboard();
		locs.remove(location);
	}

	/**
	 * Note that this iterator makes no guarantee about the order in
	 * which squares appear in the iteration.
	 */
	@Override
	public Flow<BoardSquare> iterator()
	{
		Iterator<BoardSquare> src = locs.iterator();
		return new AbstractFlow<BoardSquare>(Optionals.ofInt(locs.size())) {
			@Override
			public boolean hasNext() {
				return src.hasNext();
			}
			@Override
			public BoardSquare next() {
				return src.next();
			}
			@Override
			public void skip() {
				next();
			}
		};
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
