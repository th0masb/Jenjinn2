/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;

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
		// TODO Auto-generated method stub
	}

	@Override
	public void reverseMove(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		// TODO Auto-generated method stub
	}
}
