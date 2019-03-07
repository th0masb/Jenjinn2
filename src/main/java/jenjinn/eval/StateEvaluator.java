/**
 *
 */
package jenjinn.eval;

import jenjinn.boardstate.BoardState;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
public class StateEvaluator
{
	private final Seq<EvaluationComponent> components;

	public StateEvaluator(int pawnTableSize)
	{
		components = Seq.of(
				new DevelopmentEvaluator(),
				new KingSafetyEvaluator(),
				new PieceLocationEvaluator(),
				new PawnStructureEvaluator(pawnTableSize));
	}

	public int evaluate(BoardState state)
	{
		int signedScore = components.flow().mapToInt(c -> c.evaluate(state)).fold(0, (a, b) -> a + b);
		return (state.getActiveSide().isWhite() ? 1 : -1) * signedScore;
	}
}
