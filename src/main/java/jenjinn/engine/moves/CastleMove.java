/**
 *
 */
package jenjinn.engine.moves;

import static jenjinn.engine.moves.MoveConstants.BLACK_CASTLE_REMOVALS;
import static jenjinn.engine.moves.MoveConstants.WHITE_CASTLE_REMOVALS;

import java.util.Set;

import jenjinn.engine.base.CastleZone;
import jenjinn.engine.base.DevelopmentPiece;
import jenjinn.engine.base.Side;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.MoveReversalData;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 *
 */
public final class CastleMove extends AbstractChessMove
{
	private final CastleZone wrappedZone;
	private final Set<CastleZone> rightsRemovedByThisMove;

	public CastleMove(final CastleZone wrappedZone)
	{
		super(wrappedZone.getKingSource(), wrappedZone.getKingTarget());
		this.wrappedZone = wrappedZone;
		this.rightsRemovedByThisMove =
				wrappedZone.isWhiteZone()? WHITE_CASTLE_REMOVALS : BLACK_CASTLE_REMOVALS;
	}

	@Override
	void updateDevelopedPieces(final BoardState state, final MoveReversalData unmakeDataStore)
	{
		unmakeDataStore.setPieceDeveloped(null);
	}

	@Override
	void updatePieceLocations(final BoardState state, final MoveReversalData unmakeDataStore)
	{
		final Side currentActiveSide = state.getActiveSide();
		final boolean whiteActive = currentActiveSide.isWhite();

		final ChessPiece king = whiteActive ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
		state.getPieceLocations().removePieceAt(wrappedZone.getKingSource(), king);
		state.getPieceLocations().addPieceAt(wrappedZone.getKingTarget(), king);

		final ChessPiece rook = whiteActive ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK;
		state.getPieceLocations().removePieceAt(wrappedZone.getRookSource(), rook);
		state.getPieceLocations().addPieceAt(wrappedZone.getRookTarget(), rook);

		unmakeDataStore.setPieceTaken(null);

		// handle enpassant
		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		// handle half move clock
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		state.getHalfMoveClock().incrementValue();
	}

	@Override
	void updateCastlingStatus(final BoardState state, final MoveReversalData unmakeDataStore)
	{
		super.updateCastlingStatus(state, unmakeDataStore);
		state.getCastlingStatus().setCastlingStatus(wrappedZone);
	}

	@Override
	public void reverseMove(final BoardState state, final MoveReversalData unmakeDataStore)
	{
		super.reverseMove(state, unmakeDataStore);
		state.getCastlingStatus().removeCastlingStatus(wrappedZone);
	}

	@Override
	void resetPieceLocations(final BoardState state, final MoveReversalData unmakeDataStore)
	{
		final boolean whiteActive = state.getActiveSide().isWhite();
		final ChessPiece king = whiteActive ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
		state.getPieceLocations().removePieceAt(wrappedZone.getKingTarget(), king);
		state.getPieceLocations().addPieceAt(wrappedZone.getKingSource(), king);

		final ChessPiece rook = whiteActive ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK;
		state.getPieceLocations().removePieceAt(wrappedZone.getRookTarget(), rook);
		state.getPieceLocations().addPieceAt(wrappedZone.getRookSource(), rook);
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
