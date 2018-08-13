/**
 *
 */
package jenjinn.engine.boardstate.calculators;

import jenjinn.engine.base.GameTermination;
import jenjinn.engine.base.Side;
import jenjinn.engine.bitboards.BitboardUtils;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.pieces.ChessPieces;

/**
 * @author ThomasB
 */
public final class TerminationState
{

	private TerminationState()
	{
	}

	/**
	 * Function which calculates the termination status of a given state of play under the
	 * assumption that the knowledge about a legal move existing is already known..
	 *
	 * @param state
	 *            The state to check the termination state of.
	 * @param legalMoveAvailable
	 *            A flag to indicate whether the active side has any legal moves.
	 * @return The termination status of the given state.
	 */
	public static GameTermination of(BoardState state, boolean legalMoveAvailable)
	{
		if (state.getHalfMoveClock().getValue() > 50) {
			return GameTermination.DRAW;
		} else if (state.getHashCache().containsThreeRepetitions()) {
			return GameTermination.DRAW;
		}

		if (legalMoveAvailable) {
			return GameTermination.NOT_TERMINAL;
		} else {
			Side active = state.getActiveSide(), passive = active.otherSide();
			long passiveControl = SquareControl.calculate(state, passive);
			long kingLoc = state.getPieceLocations().locationsOf(ChessPieces.king(active));
			boolean inCheck = BitboardUtils.bitboardsIntersect(passiveControl, kingLoc);
			return inCheck ? GameTermination.getWinFor(passive) : GameTermination.DRAW;
		}
	}
}
