/**
 *
 */
package com.github.maumay.jenjinn.boardstate.calculators;

import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jflow.iterables.RichIterable;
import com.github.maumay.jflow.iterators.Iter;
import com.github.maumay.jflow.iterators.RichIterator;

/**
 * @author ThomasB
 *
 */
public final class PinnedPieceCollection implements RichIterable<PinnedPiece>
{
	private final Map<Square, PinnedPiece> cache;

	public PinnedPieceCollection(Iterator<? extends PinnedPiece> pinnedPieces)
	{
		cache = unmodifiableMap(
				Iter.wrap(pinnedPieces).toMap(PinnedPiece::getLocation, identity()));
	}

	@Override
	public RichIterator<PinnedPiece> iter()
	{
		return Iter.over(cache.values());
	}

	public Set<Square> getLocations()
	{
		return cache.keySet();
	}

	public boolean containsLocation(Square location)
	{
		return cache.containsKey(location);
	}

	public long getConstraintAreaOfPieceAt(Square location)
	{
		return cache.get(location).getConstrainedArea();
	}
}
