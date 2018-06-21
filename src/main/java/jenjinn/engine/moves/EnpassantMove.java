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
import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;

/**
 * @author ThomasB
 *
 */
public final class EnpassantMove extends AbstractChessMove
{
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
		final ChessPiece activePawn = ChessPieces.pawn(activeSide);
		final ChessPiece passivePawn = ChessPieces.pawn(activeSide.otherSide());

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
		final Side activeSide = state.getActiveSide();
		final ChessPiece activePawn = ChessPieces.pawn(activeSide);
		state.getPieceLocations().removePieceAt(getTarget(), activePawn);
		state.getPieceLocations().addPieceAt(getSource(), activePawn);

		final ChessPiece passivePawn = ChessPieces.pawn(activeSide.otherSide());
		state.getPieceLocations().addPieceAt(enPassantSquare, passivePawn);
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
		return new StringBuilder("E")
				.append(getSource().name())
				.append(getTarget().name())
				.toString();
	}
}
