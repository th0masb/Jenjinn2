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
import com.github.maumay.jflow.iterables.EnhancedIterable;
import com.github.maumay.jflow.iterators.EnhancedIterator;
import com.github.maumay.jflow.iterators.factories.Iter;

/**
 * @author ThomasB
 *
 */
public final class PinnedPieceCollection implements EnhancedIterable<PinnedPiece>
{
	private final Map<Square, PinnedPiece> cache;

	public PinnedPieceCollection(Iterator<? extends PinnedPiece> pinnedPieces)
	{
		cache = unmodifiableMap(
				Iter.wrap(pinnedPieces).toMap(PinnedPiece::getLocation, identity()));
	}

	@Override
	public EnhancedIterator<PinnedPiece> iter()
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
