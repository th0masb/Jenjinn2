package jenjinn.engine.moves;

import static java.lang.Math.abs;

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
 */
final class StandardMoveForwardLogic
{
	private StandardMoveForwardLogic() {
	}

	static void updatePieceLocations(final StandardMove move, final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final BoardSquare source = move.getSource(), target = move.getTarget();
		final Side currentActiveSide = state.getActiveSide(), nextActiveSide = currentActiveSide.otherSide();
		final ChessPiece movingPiece = state.getPieceLocations().getPieceAt(source, currentActiveSide);
		final ChessPiece removedPiece = state.getPieceLocations().getPieceAt(target, nextActiveSide);
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

		// Update location scores
		int midgameScore = state.getMidgamePieceLocationEvaluation(), endgameScore = state.getEndgamePieceLocationEvaluation();
		unmakeDataStore.setDiscardedMidgameScore(midgameScore);
		unmakeDataStore.setDiscardedEndgameScore(endgameScore);

		midgameScore -= state.getMidgameTables().getLocationValue(movingPiece, source);
		midgameScore += state.getMidgameTables().getLocationValue(movingPiece, target);
		endgameScore -= state.getEndgameTables().getLocationValue(movingPiece, source);
		endgameScore += state.getEndgameTables().getLocationValue(movingPiece, target);
		if (pieceWasRemoved) {
			midgameScore -= state.getMidgameTables().getLocationValue(removedPiece, target);
			endgameScore -= state.getEndgameTables().getLocationValue(removedPiece, target);
		}
		state.setMidgamePieceLocationEvaluation(midgameScore);
		state.setEndgamePieceLocationEvaluation(endgameScore);

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
				final BoardSquare newEnpassantSquare = BoardSquare.fromIndex(source.ordinal() + squareOrdinalDifference/2);
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

	static void updateCastlingRights(final StandardMove move, final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		if (state.getCastlingStatus().getCastlingRights().size() > 0) {
			final EnumSet<CastleZone> rightsRemoved = CastleRightsRemoval.getRightsRemovedBy(move);
			state.getCastlingStatus().getCastlingRights().removeAll(rightsRemoved);
			for (final CastleZone rightRemoved : rightsRemoved) {
				state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getCastleRightsFeature(rightRemoved));
			}
			unmakeDataStore.setDiscardedCastlingRights(rightsRemoved);
		}
		else if (unmakeDataStore.getDiscardedCastlingRights().size() > 0){
			unmakeDataStore.setDiscardedCastlingRights(EnumSet.noneOf(CastleZone.class));
		}
	}

	static void updateDevelopedPieces(final StandardMove move, final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final EnumSet<DevelopmentPiece> developedPieces = state.getDevelopedPieces();
		if (developedPieces.size() < 12) {
			final DevelopmentPiece potentialDevelopment = DevelopmentPiece.fromStartSquare(move.getSource());
			if (potentialDevelopment != null && !developedPieces.contains(potentialDevelopment)) {
				developedPieces.add(potentialDevelopment);
				unmakeDataStore.setPieceDeveloped(potentialDevelopment);
			}
			else {
				unmakeDataStore.setPieceDeveloped(null);
			}
		}
	}
}
