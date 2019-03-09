/**
 *
 */
package com.github.maumay.jenjinn.moves;

import java.util.Set;

import com.github.maumay.jenjinn.base.CastleZone;
import com.github.maumay.jenjinn.base.DevelopmentPiece;
import com.github.maumay.jenjinn.base.Dir;
import com.github.maumay.jenjinn.base.Side;
import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.MoveReversalData;
import com.github.maumay.jenjinn.pieces.ChessPieces;
import com.github.maumay.jenjinn.pieces.Piece;

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
		enPassantSquare = target.next(start.ordinal() - target.ordinal() > 0? Dir.N : Dir.S).get();
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
