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
		// TODO Auto-generated method stub
	}

	static void updatePieceLocations(final CastleMove move, final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Side currentActiveSide = state.getActiveSide(), nextActiveSide = currentActiveSide.otherSide();
		final boolean whiteActive = currentActiveSide.isWhite();

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
		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		state.getCastlingStatus();
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
