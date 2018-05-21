/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardStateImpl;
import jenjinn.engine.boardstate.ReverseMoveData;

/**
 * @author ThomasB
 */
public interface ChessMove
{
	/**
	 * Mutate the state of the parameter {@linkplain BoardStateImpl} according
	 * to this move. Store required data for reversing this move in the parameter
	 * {@linkplain ReverseMoveData} instance.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void makeMove(BoardStateImpl state, ReverseMoveData unmakeDataStore);

	/**
	 * Using the supplied {@linkplain ReverseMoveData} to mutate the state of the
	 * parameter {@linkplain BoardStateImpl} instance to reverse this move.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void reverseMove(BoardStateImpl state, ReverseMoveData unmakeDataStore);
}
