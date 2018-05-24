/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;

/**
 * @author ThomasB
 */
public interface ChessMove
{
	BoardSquare getSource();

	BoardSquare getTarget();

	/**
	 * Mutate the state of the parameter {@linkplain BoardState} according
	 * to this move. Store required data for reversing this move in the parameter
	 * {@linkplain DataForReversingMove} instance. Upon mutation of the state of
	 * the board in this forward direction a {@linkplain ChessMove} <b>must
	 * update all</b> the parameters in the {@linkplain DataForReversingMove} instance.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void makeMove(BoardState state, DataForReversingMove unmakeDataStore);

	/**
	 * Using the supplied {@linkplain DataForReversingMove} to mutate the state of the
	 * parameter {@linkplain BoardState} instance to reverse this move.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void reverseMove(BoardState state, DataForReversingMove unmakeDataStore);
}
