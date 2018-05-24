/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 *
 */
public final class CastleMove extends AbstractChessMove
{
	private final CastleZone wrappedZone;

	public CastleMove(final CastleZone wrappedZone)
	{
		super(wrappedZone.getKingSource(), wrappedZone.getKingTarget());
		this.wrappedZone = wrappedZone;
	}

	@Override
	void updateDevelopedPieces(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		unmakeDataStore.setPieceDeveloped(null);
	}

	@Override
	void updatePieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Side currentActiveSide = state.getActiveSide();
		final boolean whiteActive = currentActiveSide.isWhite();

		final ChessPiece king = whiteActive ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
		state.getPieceLocations().removePieceAt(wrappedZone.getKingSource(), king);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(wrappedZone.getKingSource(), king));
		state.getPieceLocations().addPieceAt(wrappedZone.getKingTarget(), king);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(wrappedZone.getKingTarget(), king));

		final ChessPiece rook = whiteActive ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK;
		state.getPieceLocations().removePieceAt(wrappedZone.getRookSource(), rook);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(wrappedZone.getRookSource(), rook));
		state.getPieceLocations().addPieceAt(wrappedZone.getRookTarget(), rook);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(wrappedZone.getRookTarget(), rook));

		unmakeDataStore.setPieceTaken(null);

		// handle enpassant
		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		// handle half move clock
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		state.getHalfMoveClock().incrementValue();
	}

	@Override
	void resetPieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		// Reset half move clock
		state.getHalfMoveClock().setValue(unmakeDataStore.getDiscardedHalfMoveClockValue());
		// Reset enpassant stuff
		state.setEnPassantSquare(unmakeDataStore.getDiscardedEnpassantSquare());
		// Reset locations
		final boolean whiteActive = state.getActiveSide().isWhite();
		final ChessPiece king = whiteActive ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
		state.getPieceLocations().removePieceAt(wrappedZone.getKingTarget(), king);
		state.getPieceLocations().addPieceAt(wrappedZone.getKingSource(), king);

		final ChessPiece rook = whiteActive ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK;
		state.getPieceLocations().removePieceAt(wrappedZone.getRookTarget(), rook);
		state.getPieceLocations().addPieceAt(wrappedZone.getRookSource(), rook);
	}

	public CastleZone getWrappedZone()
	{
		return wrappedZone;
	}
}
