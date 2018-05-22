/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.enums.BoardSquare;

/**
 * @author ThomasB
 *
 */
public abstract class AbstractChessMove implements ChessMove
{
	protected final BoardSquare start, target;

	public AbstractChessMove(final BoardSquare start, final BoardSquare target) {
		this.start = start;
		this.target = target;
	}

//	/* (non-Javadoc)
//	 * @see jenjinn.engine.moves.ChessMove#makeMove(jenjinn.engine.boardstate.BoardStateImpl, jenjinn.engine.boardstate.ReverseMoveData)
//	 */
//	@Override
//	public void makeMove(final BoardStateImpl state, final ReverseMoveData unmakeDataStore) {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see jenjinn.engine.moves.ChessMove#reverseMove(jenjinn.engine.boardstate.BoardStateImpl, jenjinn.engine.boardstate.ReverseMoveData)
//	 */
//	@Override
//	public void reverseMove(final BoardStateImpl state, final ReverseMoveData unmakeDataStore) {
//		// TODO Auto-generated method stub
//
//	}

}
