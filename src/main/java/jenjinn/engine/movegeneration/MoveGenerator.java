/**
 *
 */
package jenjinn.engine.movegeneration;

import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.StandardMove;

/**
 * @author ThomasB
 *
 */
public final class MoveGenerator {

	private MoveGenerator() {}

	public static List<ChessMove> getAvailableMoves(final BoardState state)
	{

		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long whiteLocs = pieceLocs.getWhiteLocations(), blackLocs = pieceLocs.getBlackLocations();

		throw new RuntimeException();
	}

	static List<StandardMove> getStandardMoves(final BoardState state)
	{
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long whiteLocs = pieceLocs.getWhiteLocations(), blackLocs = pieceLocs.getBlackLocations();

		throw new RuntimeException();
	}
}