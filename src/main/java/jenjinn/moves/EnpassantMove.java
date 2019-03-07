/**
 *
 */
package jenjinn.moves;

import java.util.Set;

import jenjinn.base.CastleZone;
import jenjinn.base.DevelopmentPiece;
import jenjinn.base.Dir;
import jenjinn.base.Side;
import jenjinn.base.Square;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.MoveReversalData;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;

/**
 * @author ThomasB
 *
 */
public final class EnpassantMove extends AbstractChessMove
{
	private final Square enPassantSquare;

	public EnpassantMove(Square start, Square target)
	{
		super(start, target);
		enPassantSquare = target.getNextSquare(start.ordinal() - target.ordinal() > 0? Dir.N : Dir.S).get();
	}

	@Override
	void updatePieceLocations(BoardState state, MoveReversalData unmakeDataStore)
	{
		Side activeSide = state.getActiveSide();
		Piece activePawn = ChessPieces.of(activeSide).head();
		Piece passivePawn = ChessPieces.of(activeSide.otherSide()).head();

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
		Piece activePawn = ChessPieces.of(activeSide).head();
		state.getPieceLocations().removePieceAt(getTarget(), activePawn);
		state.getPieceLocations().addPieceAt(getSource(), activePawn);

		Piece passivePawn = ChessPieces.of(activeSide.otherSide()).head();
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
