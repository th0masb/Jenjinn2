/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 *
 */
public final class StandardMove extends AbstractChessMove {

	public StandardMove(final BoardSquare start, final BoardSquare target)
	{
		super(start, target);
	}

	@Override
	public void makeMove(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Side currentActiveSide = state.getActiveSide(), nextActiveSide = currentActiveSide.otherSide();

		final ChessPiece movingPiece = state.getPieceLocations().getPieceAt(start, currentActiveSide);
		final ChessPiece removedPiece = state.getPieceLocations().getPieceAt(target, nextActiveSide);
		unmakeDataStore.setPieceTaken(removedPiece);


	}

	@Override
	public void reverseMove(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		// TODO Auto-generated method stub
	}
}
