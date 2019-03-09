/**
 *
 */
package jenjinn.eval;

import com.github.maumay.jflow.vec.Vec;

import jenjinn.boardstate.BoardState;

/**
 * @author ThomasB
 */
public class StateEvaluator
{
	private final Vec<EvaluationComponent> components;

	public StateEvaluator(int pawnTableSize)
	{
		components = Vec.of(new DevelopmentEvaluator(), new KingSafetyEvaluator(),
				new PieceLocationEvaluator(), new PawnStructureEvaluator(pawnTableSize));
	}

	public int evaluate(BoardState state)
	{
		int signedScore = components.iter().mapToInt(c -> c.evaluate(state)).fold(0,
				(a, b) -> a + b);
		return (state.getActiveSide().isWhite() ? 1 : -1) * signedScore;
	}
}
