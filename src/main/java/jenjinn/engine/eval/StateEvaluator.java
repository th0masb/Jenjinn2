package jenjinn.engine.eval;

import jenjinn.engine.boardstate.BoardState;
import xawd.jflow.collections.FlowList;

/**
 * @author ThomasB
 */
public final class StateEvaluator
{
	private final FlowList<EvaluationComponent> components;

	public StateEvaluator(FlowList<EvaluationComponent> components)
	{
		this.components = components.flow().toList();
	}

	public int evaluate(final BoardState state)
	{
		final int signedScore = components.mapToInt(c -> c.evaluate(state)).reduce(0, (a, b) -> a + b);
		return (state.getActiveSide().isWhite()? 1 : -1)*signedScore;
	}
}
