/**
 *
 */
package jenjinn.engine.boardstate.propertycalculators;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.List;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.moves.ChessMove;

/**
 * @author ThomasB
 *
 */
public final class LegalMoves {

	private LegalMoves() {}

	public static List<ChessMove> forState(final BoardState state)
	{
		final Side active = state.getActiveSide(), passive = active.otherSide();
		final long passiveControl = SquareControl.calculate(state, passive);
		final long activeKingLoc = state.getPieceLocations().locationOverviewOf(ChessPieces.king(active));
		if (bitboardsIntersect(passiveControl, activeKingLoc)) {
			return getMovesOutOfCheck(state, passiveControl);
		}
		else {
			return getLegalMoves(state, passiveControl);
		}
	}

	private static List<ChessMove> getLegalMoves(final BoardState state, final long passiveControl)
	{
		/*
		 *  First find list of pieces and their squares causing check, if one then move, take or block
		 *  is possible. If two then we have to try and move.
		 */
		throw new RuntimeException();
	}

	private static List<ChessMove> getMovesOutOfCheck(final BoardState state, final long passiveControl)
	{
		 /*
		  * We must take into account possible pins, Standard sliding moves induce 'cord' bitboards which
		  * can be used to check for pins.
		  */
		throw new RuntimeException();
	}
}
