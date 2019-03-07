/**
 *
 */
package jenjinn.eval;

import jenjinn.boardstate.BoardState;

/**
 * Abstraction of a judgement function on some aspect of the state of a chess
 * game.
 *
 * @author ThomasB
 */
@FunctionalInterface
public interface EvaluationComponent
{
	/**
	 * Calculates a signed score for some aspect of the state of play in a chess
	 * game.
	 *
	 * @param state
	 *            The state to judge the score of.
	 * @return A signed integer score where a positive score represents a better
	 *         position for white and vice versa for black. The larger the absolute
	 *         value of the score the more the evaluation is tipped in favour of the
	 *         corresponding side.
	 */
	int evaluate(BoardState state);
}
