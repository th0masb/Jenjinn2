/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 *
 */
public final class EnPassantMove extends AbstractChessMove
{
	private final BoardSquare enPassantSquare;

	public EnPassantMove(final BoardSquare start, final BoardSquare target)
	{
		super(start, target);
		enPassantSquare = target.getNextSquareInDirection(start.ordinal() - target.ordinal() > 0? Direction.N : Direction.S);
	}

	@Override
	void updatePieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Side activeSide = state.getActiveSide(), passiveSide = activeSide.otherSide();
		final ChessPiece activePawn = state.getPieceLocations().getPieceAt(getSource(), activeSide);
		final ChessPiece passivePawn = state.getPieceLocations().getPieceAt(enPassantSquare, passiveSide);
		assert activePawn.isPawn() && passivePawn.isPawn();

		state.getPieceLocations().removePieceAt(getSource(), activePawn);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(getSource(), activePawn));
		state.getPieceLocations().addPieceAt(getTarget(), activePawn);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(getTarget(), activePawn));
		state.getPieceLocations().removePieceAt(enPassantSquare, passivePawn);
		state.getHashCache().xorFeatureWithCurrentHash(state.getStateHasher().getSquarePieceFeature(enPassantSquare, passivePawn));

		unmakeDataStore.setPieceTaken(passivePawn);
		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		state.getHalfMoveClock().resetValue();
	}

	@Override
	void updateDevelopedPieces(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		unmakeDataStore.setPieceDeveloped(null);
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
		final ChessPiece activePawn = whiteActive ? ChessPiece.WHITE_PAWN : ChessPiece.BLACK_PAWN;
		state.getPieceLocations().removePieceAt(getTarget(), activePawn);
		state.getPieceLocations().addPieceAt(getSource(), activePawn);

		final ChessPiece passivePawn = whiteActive ? ChessPiece.BLACK_PAWN : ChessPiece.WHITE_PAWN;
		state.getPieceLocations().addPieceAt(enPassantSquare, passivePawn);
	}
}
