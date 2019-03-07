/**
 *
 */
package jenjinn.boardstate.calculators;

import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;

import java.util.Map;
import java.util.Set;

import jenjinn.base.Square;
import jflow.iterators.Flow;
import jflow.iterators.factories.Iter;
import jflow.iterators.iterables.FlowIterable;

/**
 * @author ThomasB
 *
 */
public final class PinnedPieceCollection implements FlowIterable<PinnedPiece>
{
	private final Map<Square, PinnedPiece> cache;

	public PinnedPieceCollection(Flow<? extends PinnedPiece> pinnedPieces)
	{
		cache = unmodifiableMap(pinnedPieces.toMap(PinnedPiece::getLocation, identity()));
	}

	@Override
	public Flow<PinnedPiece> flow()
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
