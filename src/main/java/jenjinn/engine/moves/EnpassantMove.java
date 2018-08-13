/**
 *
 */
package jenjinn.engine.moves;

import java.util.Set;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.CastleZone;
import jenjinn.engine.base.DevelopmentPiece;
import jenjinn.engine.base.Direction;
import jenjinn.engine.base.Side;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.MoveReversalData;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;

/**
 * @author ThomasB
 *
 */
public final class EnpassantMove extends AbstractChessMove
{
	private final BoardSquare enPassantSquare;

	public EnpassantMove(BoardSquare start, BoardSquare target)
	{
		super(start, target);
		enPassantSquare = target.getNextSquareInDirection(start.ordinal() - target.ordinal() > 0? Direction.N : Direction.S);
	}

	@Override
	void updatePieceLocations(BoardState state, MoveReversalData unmakeDataStore)
	{
		Side activeSide = state.getActiveSide();
		ChessPiece activePawn = ChessPieces.pawn(activeSide);
		ChessPiece passivePawn = ChessPieces.pawn(activeSide.otherSide());

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
	void updateDevelopedPieces(BoardState state, MoveReversalData unmakeDataStore)
	{
		unmakeDataStore.setPieceDeveloped(null);
	}

	@Override
	void resetPieceLocations(BoardState state, MoveReversalData unmakeDataStore)
	{
		Side activeSide = state.getActiveSide();
		ChessPiece activePawn = ChessPieces.pawn(activeSide);
		state.getPieceLocations().removePieceAt(getTarget(), activePawn);
		state.getPieceLocations().addPieceAt(getSource(), activePawn);

		ChessPiece passivePawn = ChessPieces.pawn(activeSide.otherSide());
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
