/**
 *
 */
package jenjinn.engine.moves;

import java.util.EnumSet;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 *
 */
public final class StandardMove extends AbstractChessMove {

	public StandardMove(final BoardSquare start, final BoardSquare target)
	{
		super(start, target);
	}

	@Override
	public void makeMove(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		updateCastlingRights(state, unmakeDataStore);
		updatePieceLocations(state, unmakeDataStore);
		updateDevelopedPieces(state, unmakeDataStore);
	}

	private void updateDevelopedPieces(BoardState state, DataForReversingMove unmakeDataStore)
	{
		final EnumSet<DevelopmentPiece> developedPieces = state.getDevelopedPieces();
		if (developedPieces.size() < 12) {
			final DevelopmentPiece potentialDevelopment = DevelopmentPiece.fromStartSquare(getSource());
			if (potentialDevelopment != null && !developedPieces.contains(potentialDevelopment)) {
				developedPieces.add(potentialDevelopment);
				unmakeDataStore.setPieceDeveloped(potentialDevelopment);
			}
			else {
				unmakeDataStore.setPieceDeveloped(null);
			}
		}
	}

	private void updatePieceLocations(BoardState state, DataForReversingMove unmakeDataStore)
	{
		final Side currentActiveSide = state.getActiveSide(), nextActiveSide = currentActiveSide.otherSide();
		final ChessPiece movingPiece = state.getPieceLocations().getPieceAt(getSource(), currentActiveSide);
		final ChessPiece removedPiece = state.getPieceLocations().getPieceAt(getTarget(), nextActiveSide);
		unmakeDataStore.setPieceTaken(removedPiece);
		state.getPieceLocations().removePieceAt(getSource(), movingPiece);
		state.getPieceLocations().addPieceAt(getTarget(), movingPiece);
		if (removedPiece != null) {
			state.getPieceLocations().removePieceAt(getTarget(), removedPiece);
		}
	}

	private void updateCastlingRights(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		if (state.getCastlingStatus().getCastlingRights().size() > 0) {
			final EnumSet<CastleZone> rightsRemoved = CastleRightsRemoval.getRightsRemovedBy(this);
			state.getCastlingStatus().getCastlingRights().removeAll(rightsRemoved);
			unmakeDataStore.setDiscardedCastlingRights(rightsRemoved);
		}
		else if (unmakeDataStore.getDiscardedCastlingRights().size() > 0){
			unmakeDataStore.setDiscardedCastlingRights(EnumSet.noneOf(CastleZone.class));
		}
	}

	@Override
	public void reverseMove(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		throw new RuntimeException();
	}
}
