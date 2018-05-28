/**
 *
 */
package jenjinn.engine.moves;

import static java.lang.Math.abs;

import java.util.Set;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;

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
	void updatePieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final BoardSquare source = getSource(), target = getTarget();
		final Side activeSide = state.getActiveSide(), passiveSide = activeSide.otherSide();
		final ChessPiece movingPiece = state.getPieceLocations().getPieceAt(source, activeSide);
		final ChessPiece removedPiece = state.getPieceLocations().getPieceAt(target, passiveSide);
		final boolean pieceWasRemoved = removedPiece != null;

		// Update locations
		unmakeDataStore.setPieceTaken(removedPiece);
		state.getPieceLocations().removePieceAt(source, movingPiece);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(source, movingPiece));
		state.getPieceLocations().addPieceAt(target, movingPiece);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(target, movingPiece));
		if (pieceWasRemoved) {
			state.getPieceLocations().removePieceAt(target, removedPiece);
			state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(target, removedPiece));
		}

		// Update enpassant stuff
		final BoardSquare oldEnpassantSquare = state.getEnPassantSquare();
		unmakeDataStore.setDiscardedEnpassantSquare(oldEnpassantSquare);
		if (oldEnpassantSquare != null) {
			state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getEnpassantFileFeature(oldEnpassantSquare));
		}

		final boolean pawnWasMoved = movingPiece.isPawn();
		if (pawnWasMoved) {
			final int squareOrdinalDifference = target.ordinal() - source.ordinal();
			if (abs(squareOrdinalDifference) == 16) {
				final BoardSquare newEnpassantSquare = BoardSquare.of(source.ordinal() + squareOrdinalDifference/2);
				state.setEnPassantSquare(newEnpassantSquare);
				state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getEnpassantFileFeature(newEnpassantSquare));
			}
			else {
				state.setEnPassantSquare(null);
			}
		}

		// Update half move clock
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		if (pawnWasMoved || pieceWasRemoved) {
			state.getHalfMoveClock().resetValue();
		}
		else {
			state.getHalfMoveClock().incrementValue();
		}
	}

	@Override
	void updateDevelopedPieces(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Set<DevelopmentPiece> developedPieces = state.getDevelopedPieces();
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

	@Override
	void resetPieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		// Reset half move clock
		state.getHalfMoveClock().setValue(unmakeDataStore.getDiscardedHalfMoveClockValue());
		// Reset enpassant stuff
		state.setEnPassantSquare(unmakeDataStore.getDiscardedEnpassantSquare());
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
