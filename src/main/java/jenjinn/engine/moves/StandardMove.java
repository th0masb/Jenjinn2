/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;

/**
 * @author ThomasB
 *
 */
public final class StandardMove extends AbstractChessMove
{
	public StandardMove(final BoardSquare start, final BoardSquare target)
	{
		super(start, target);
	}

	@Override
	public void makeMove(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		unmakeDataStore.setDiscardedHash(state.getHashCache().incrementHalfMoveCount());
		StandardMoveForwardLogic.updateCastlingRights(this, state, unmakeDataStore);
		StandardMoveForwardLogic.updatePieceLocations(this, state, unmakeDataStore);
		StandardMoveForwardLogic.updateDevelopedPieces(this, state, unmakeDataStore);
		state.switchActiveSide();
	}

	@Override
	public void reverseMove(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		state.switchActiveSide();
		resetDevelopedPieces(state, unmakeDataStore);
		resetPieceLocations(state, unmakeDataStore);
		resetCastlingRights(state, unmakeDataStore);
		state.getHashCache().decrementHalfMoveCount(unmakeDataStore.getDiscardedHash());
	}

	private void resetDevelopedPieces(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		state.getDevelopedPieces().remove(unmakeDataStore.getPieceDeveloped());
	}

	private void resetCastlingRights(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		state.getCastlingStatus().getCastlingRights().addAll(unmakeDataStore.getDiscardedCastlingRights());
	}

	private void resetPieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		// Reset half move clock
		state.getHalfMoveClock().setValue(unmakeDataStore.getDiscardedHalfMoveClockValue());
		// Reset enpassant stuff
		state.setEnPassantSquare(unmakeDataStore.getDiscardedEnpassantSquare());
		// Reset location scores
		state.setMidgamePieceLocationEvaluation(unmakeDataStore.getDiscardedMidgameScore());
		state.setEndgamePieceLocationEvaluation(unmakeDataStore.getDiscardedEndgameScore());
		// Reset locations
		final ChessPiece previouslyMovedPiece = state.getPieceLocations().getPieceAt(getTarget(), state.getActiveSide());
		state.getPieceLocations().removePieceAt(getTarget(), previouslyMovedPiece);
		state.getPieceLocations().addPieceAt(getSource(), previouslyMovedPiece);

		final ChessPiece previouslyRemovedPiece = unmakeDataStore.getPieceTaken();
		if (previouslyMovedPiece != null) {
			state.getPieceLocations().addPieceAt(getTarget(), previouslyRemovedPiece);
		}
	}
}
