/**
 *
 */
package jenjinn.engine.moves;

import java.util.Set;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 *
 */
public final class PromotionMove extends AbstractChessMove
{
	public PromotionMove(final BoardSquare start, final BoardSquare target)
	{
		super(start, target);
	}

	@Override
	void updatePieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Side activeSide = state.getActiveSide();
		state.getPieceLocations().removePieceAt(getSource(), activeSide.isWhite() ? ChessPiece.WHITE_PAWN : ChessPiece.BLACK_PAWN);
		state.getPieceLocations().addPieceAt(getTarget(), activeSide.isWhite() ? ChessPiece.WHITE_QUEEN : ChessPiece.BLACK_QUEEN);

		final ChessPiece removedPiece = state.getPieceLocations().getPieceAt(getTarget(), activeSide.otherSide());
		if (removedPiece != null) {
			state.getPieceLocations().removePieceAt(getTarget(), removedPiece);
			unmakeDataStore.setPieceTaken(removedPiece);
		}
		else {
			unmakeDataStore.setPieceTaken(null);
		}

		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		state.getHalfMoveClock().resetValue();
	}

	@Override
	void resetPieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Side activeSide = state.getActiveSide();
		state.getPieceLocations().removePieceAt(getTarget(), activeSide.isWhite()? ChessPiece.WHITE_QUEEN : ChessPiece.BLACK_QUEEN);
		state.getPieceLocations().addPieceAt(getSource(), activeSide.isWhite() ? ChessPiece.WHITE_PAWN : ChessPiece.BLACK_PAWN);

		final ChessPiece pieceToReplace = unmakeDataStore.getPieceTaken();
		if (pieceToReplace != null) {
			state.getPieceLocations().addPieceAt(getTarget(), pieceToReplace);
		}
	}

	@Override
	Set<CastleZone> getAllRightsToBeRemoved()
	{
		return MoveConstants.EMPTY_RIGHTS_SET;
	}

	@Override
	DevelopmentPiece getPieceDeveloped()
	{
		return null;
	}

	@Override
	public String toCompactString()
	{
		return new StringBuilder("P")
				.append(getSource().name())
				.append(getTarget().name())
				.toString();
	}
}
