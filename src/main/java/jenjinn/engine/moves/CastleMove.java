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
	public void makeMove(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		unmakeDataStore.setDiscardedHash(state.getHashCache().incrementHalfMoveCount());
		unmakeDataStore.setPieceDeveloped(null);
		updateCastlingRights(state, unmakeDataStore);
		updatePieceLocations(this, state, unmakeDataStore);
		state.switchActiveSide();
	}

	static void updatePieceLocations(final CastleMove move, final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Side currentActiveSide = state.getActiveSide();
		final boolean whiteActive = currentActiveSide.isWhite();

		unmakeDataStore.setDiscardedMidgameScore(state.getPieceLocations().getMidgameEval());
		unmakeDataStore.setDiscardedEndgameScore(state.getPieceLocations().getEndgameEval());

		final ChessPiece king = whiteActive ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
		state.getPieceLocations().removePieceAt(move.getWrappedZone().getKingSource(), king);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(move.getWrappedZone().getKingSource(), king));
		state.getPieceLocations().addPieceAt(move.getWrappedZone().getKingTarget(), king);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(move.getWrappedZone().getKingTarget(), king));

		final ChessPiece rook = whiteActive ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK;
		state.getPieceLocations().removePieceAt(move.getWrappedZone().getRookSource(), rook);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(move.getWrappedZone().getRookSource(), rook));
		state.getPieceLocations().addPieceAt(move.getWrappedZone().getRookTarget(), rook);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(move.getWrappedZone().getRookTarget(), rook));

		unmakeDataStore.setPieceTaken(null);

		// handle enpassant
		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		// handle half move clock
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		state.getHalfMoveClock().incrementValue();
	}

	@Override
	public void reverseMove(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		// TODO Auto-generated method stub
	}

	public CastleZone getWrappedZone()
	{
		return wrappedZone;
	}
}
