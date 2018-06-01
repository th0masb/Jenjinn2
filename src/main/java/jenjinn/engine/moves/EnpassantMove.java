/**
 *
 */
package jenjinn.engine.moves;

import static java.util.Collections.unmodifiableSet;

import java.util.EnumSet;
import java.util.Set;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 *
 */
public final class EnpassantMove extends AbstractChessMove
{
	private static final Set<CastleZone> EMPTY_RIGHTS = unmodifiableSet(EnumSet.noneOf(CastleZone.class));

	private final BoardSquare enPassantSquare;

	public EnpassantMove(final BoardSquare start, final BoardSquare target)
	{
		super(start, target);
		enPassantSquare = target.getNextSquareInDirection(start.ordinal() - target.ordinal() > 0? Direction.N : Direction.S);
	}

	@Override
	void updatePieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Side activeSide = state.getActiveSide();
		final ChessPiece activePawn = activeSide.isWhite() ? ChessPiece.WHITE_PAWN : ChessPiece.BLACK_PAWN;
		final ChessPiece passivePawn = activeSide.isWhite() ? ChessPiece.BLACK_PAWN : ChessPiece.WHITE_PAWN;

		state.getPieceLocations().removePieceAt(getSource(), activePawn);
		state.getPieceLocations().addPieceAt(getTarget(), activePawn);
		state.getPieceLocations().removePieceAt(enPassantSquare, passivePawn);

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
		// Reset locations
		final boolean whiteActive = state.getActiveSide().isWhite();
		final ChessPiece activePawn = whiteActive ? ChessPiece.WHITE_PAWN : ChessPiece.BLACK_PAWN;
		state.getPieceLocations().removePieceAt(getTarget(), activePawn);
		state.getPieceLocations().addPieceAt(getSource(), activePawn);

		final ChessPiece passivePawn = whiteActive ? ChessPiece.BLACK_PAWN : ChessPiece.WHITE_PAWN;
		state.getPieceLocations().addPieceAt(enPassantSquare, passivePawn);
	}

	@Override
	Set<CastleZone> getAllRightsToBeRemoved()
	{
		return EMPTY_RIGHTS;
	}

	@Override
	DevelopmentPiece getPieceDeveloped()
	{
		return null;
	}
}
