/**
 *
 */
package jenjinn.moves;

import static jenjinn.moves.MoveConstants.BLACK_CASTLE_REMOVALS;
import static jenjinn.moves.MoveConstants.WHITE_CASTLE_REMOVALS;

import java.util.Set;

import jenjinn.base.CastleZone;
import jenjinn.base.DevelopmentPiece;
import jenjinn.base.Side;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.MoveReversalData;
import jenjinn.pieces.Piece;

/**
 * @author ThomasB
 *
 */
public final class CastleMove extends AbstractChessMove
{
	private final CastleZone wrappedZone;
	private final Set<CastleZone> rightsRemovedByThisMove;

	public CastleMove(CastleZone wrappedZone)
	{
		super(wrappedZone.kingSource, wrappedZone.kingTarget);
		this.wrappedZone = wrappedZone;
		this.rightsRemovedByThisMove =
				wrappedZone.isWhiteZone()? WHITE_CASTLE_REMOVALS : BLACK_CASTLE_REMOVALS;
	}

	@Override
	void updateDevelopedPieces(BoardState state, MoveReversalData unmakeDataStore)
	{
		unmakeDataStore.setPieceDeveloped(null);
	}

	@Override
	void updatePieceLocations(BoardState state, MoveReversalData unmakeDataStore)
	{
		Side currentActiveSide = state.getActiveSide();
		boolean whiteActive = currentActiveSide.isWhite();

		Piece king = whiteActive ? Piece.WHITE_KING : Piece.BLACK_KING;
		state.getPieceLocations().removePieceAt(wrappedZone.kingSource, king);
		state.getPieceLocations().addPieceAt(wrappedZone.kingTarget, king);

		Piece rook = whiteActive ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
		state.getPieceLocations().removePieceAt(wrappedZone.rookSource, rook);
		state.getPieceLocations().addPieceAt(wrappedZone.rookTarget, rook);

		unmakeDataStore.setPieceTaken(null);

		// handle enpassant
		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		// handle half move clock
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		state.getHalfMoveClock().incrementValue();
	}

	@Override
	void updateCastlingStatus(BoardState state, MoveReversalData unmakeDataStore)
	{
		super.updateCastlingStatus(state, unmakeDataStore);
		state.getCastlingStatus().setCastlingStatus(wrappedZone);
	}

	@Override
	public void reverseMove(BoardState state, MoveReversalData unmakeDataStore)
	{
		super.reverseMove(state, unmakeDataStore);
		state.getCastlingStatus().removeCastlingStatus(wrappedZone);
	}

	@Override
	void resetPieceLocations(BoardState state, MoveReversalData unmakeDataStore)
	{
		boolean whiteActive = state.getActiveSide().isWhite();
		Piece king = whiteActive ? Piece.WHITE_KING : Piece.BLACK_KING;
		state.getPieceLocations().removePieceAt(wrappedZone.kingTarget, king);
		state.getPieceLocations().addPieceAt(wrappedZone.kingSource, king);

		Piece rook = whiteActive ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
		state.getPieceLocations().removePieceAt(wrappedZone.rookTarget, rook);
		state.getPieceLocations().addPieceAt(wrappedZone.rookSource, rook);
	}

	@Override
	Set<CastleZone> getAllRightsToBeRemoved()
	{
		return rightsRemovedByThisMove;
	}

	@Override
	DevelopmentPiece getPieceDeveloped()
	{
		return null;
	}

	public CastleZone getWrappedZone()
	{
		return wrappedZone;
	}

	@Override
	public String toString()
	{
		return new StringBuilder("CastleMove[zone=")
				.append(wrappedZone.name())
				.append("]")
				.toString();
	}

	@Override
	public String toCompactString()
	{
		return wrappedZone.getSimpleIdentifier();
	}
}
