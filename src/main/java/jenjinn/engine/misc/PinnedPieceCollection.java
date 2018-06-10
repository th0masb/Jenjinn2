/**
 *
 */
package jenjinn.engine.misc;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import jenjinn.engine.enums.BoardSquare;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.iterators.iterables.FlowIterable;

/**
 * @author ThomasB
 *
 */
public final class PinnedPieceCollection implements FlowIterable<PinnedPiece>
{
	private final List<PinnedPiece> pinnedPieces;
	private final Set<BoardSquare> pinnedPieceLocations;

	public PinnedPieceCollection(final Flow<PinnedPiece> pinnedPieces)
	{
		this.pinnedPieces = pinnedPieces.toList();
		this.pinnedPieceLocations = iterator().map(PinnedPiece::getLocation)
				.toCollection(() -> EnumSet.noneOf(BoardSquare.class));
	}

	@Override
	public Flow<PinnedPiece> iterator()
	{
		return Iterate.over(pinnedPieces);
	}

	public boolean containsLocation(final BoardSquare location)
	{
		return pinnedPieceLocations.contains(location);
	}

	public long getConstraintAreaOfPieceAt(final BoardSquare location)
	{
		for (final PinnedPiece piece : pinnedPieces) {
			if (piece.getLocation() == location) {
				return piece.getConstrainedArea();
			}
		}
		throw new IllegalArgumentException();
	}
}
