/**
 *
 */
package jenjinn.engine.eval;

import jenjinn.engine.boardstate.BoardState;
import xawd.jflow.collections.FlowList;
import xawd.jflow.collections.Lists;

/**
 * @author ThomasB
 */
public class StateEvaluator
{
	private final FlowList<EvaluationComponent> components;

	public StateEvaluator(int pawnTableSize)
	{
		components = Lists.build(
				new DevelopmentEvaluator(),
				new KingSafetyEvaluator(),
				new PieceLocationEvaluator(),
				new PawnStructureEvaluator(pawnTableSize));
	}

	public int evaluate(BoardState state)
	{
		final int signedScore = components.mapToInt(c -> c.evaluate(state)).fold(0, (a, b) -> a + b);
		return (state.getActiveSide().isWhite() ? 1 : -1) * signedScore;
	}
}
