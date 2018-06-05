/**
 *
 */
package jenjinn.engine.boardstate.propertycalculators;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.GameTermination;

/**
 * @author ThomasB
 */
public final class TerminationState {

	private TerminationState() {
	}

	public static GameTermination of(final BoardState state)
	{
		throw new RuntimeException();
//		if (state.getHalfMoveClock().getValue() > 50) {
//			return GameTermination.DRAW;
//		}
//		else if (state.getHashCache().containsThreeRepetitions()) {
//			return GameTermination.DRAW;
//		}
//		final Side active = state.getActiveSide();
//		final long activeControl = SquareControl.calculate(state, active);
	}
}
