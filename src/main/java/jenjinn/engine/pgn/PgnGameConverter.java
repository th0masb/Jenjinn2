/**
 *
 */
package jenjinn.engine.pgn;

import static jenjinn.engine.pgn.PgnMoveBuilder.GAME_START;
import static jenjinn.engine.pgn.PgnMoveBuilder.GAME_TERMINATION;
import static jenjinn.engine.pgn.PgnMoveBuilder.MOVE;
import static jenjinn.engine.pgn.PgnMoveBuilder.PRECEEDING_MOVE;
import static jenjinn.engine.pgn.PgnMoveBuilder.PROCEEDING_MOVE;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.moves.ChessMove;
import xawd.jflow.utilities.StringUtils;

/**
 * @author t
 *
 */
public final class PgnGameConverter
{
	private static final String TOTAL_MOVE = "(" + PRECEEDING_MOVE + MOVE + PROCEEDING_MOVE + ")";
	private static final String GAME_STRING = GAME_START + TOTAL_MOVE + "+" + GAME_TERMINATION;

	private PgnGameConverter()
	{
	}

	public static List<ChessMove> parse(String pgnInput) throws BadPgnException
	{
		final String pgn = pgnInput.trim();
		if (pgn.matches(GAME_STRING)) {
			final BoardState state = StartStateGenerator.getStartBoard();
			final List<String> encodedMoves = StringUtils.getAllMatches(pgn, PgnMoveBuilder.MOVE_EXTRACTOR);
			final List<ChessMove> decodedMoves = new ArrayList<>(encodedMoves.size());
			for (final String encodedMove : encodedMoves) {
				final ChessMove decodedMove = PgnMoveBuilder.convertPgnCommand(state, encodedMove);
				decodedMoves.add(decodedMove);
				decodedMove.makeMove(state);
			}
			return decodedMoves;
		}
		else {
			throw new BadPgnException(pgn);
		}

	}

}
