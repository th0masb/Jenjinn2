/**
 *
 */
package jenjinn.engine.moves;

import java.util.EnumSet;
import java.util.Set;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;

/**
 * @author ThomasB
 *
 */
public abstract class AbstractChessMove implements ChessMove
{
	private final BoardSquare source, target;

	public AbstractChessMove(final BoardSquare start, final BoardSquare target) {
		this.source = start;
		this.target = target;
	}

	@Override
	public BoardSquare getSource()
	{
		return source;
	}

	@Override
	public BoardSquare getTarget()
	{
		return target;
	}

	protected void updateCastlingRights(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		if (state.getCastlingStatus().getCastlingRights().size() > 0)
		{
			final Set<CastleZone> rightsRemoved = EnumSet.copyOf(CastleRightsRemoval.getRightsRemovedBy(this));
			rightsRemoved.retainAll(state.getCastlingStatus().getCastlingRights());
			state.getCastlingStatus().getCastlingRights().removeAll(rightsRemoved);
			unmakeDataStore.setDiscardedCastlingRights(rightsRemoved);

			for (final CastleZone rightRemoved : rightsRemoved) {
				state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getCastleRightsFeature(rightRemoved));
			}
		}
		else if (unmakeDataStore.getDiscardedCastlingRights().size() > 0)
		{
			unmakeDataStore.setDiscardedCastlingRights(EnumSet.noneOf(CastleZone.class));
		}
	}
}
