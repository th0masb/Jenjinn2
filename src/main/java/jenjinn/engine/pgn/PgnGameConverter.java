/**
 *
 */
package jenjinn.engine.pgn;

import static jenjinn.engine.pgn.PgnMoveBuilder.STANDARD_MOVE;

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
	private static final String GAME_STRING = "(^1\\." + STANDARD_MOVE + ")" + ".*" + "(((1\\-0)|(0\\-1)|(1/2\\-1/2)|(\\*))$)";

	private PgnGameConverter()
	{
	}

	public static List<ChessMove> parse(String pgnInput) throws BadPgnException
	{
		final String pgn = pgnInput.trim();
		if (pgn.matches(GAME_STRING)) {
			final BoardState state = StartStateGenerator.getStartBoard();
			final List<String> encodedMoves = StringUtils.getAllMatches(pgn, PgnMoveBuilder.MOVE);
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

	public static void main(String[] args)
	{
		final String game = "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.Nc3 dxc4 5.a4 Bf5 6.e3 e6 7.Bxc4 Bb4 8.O-O O-O 9.Ne2 Nbd7 1/2-1/2";
		System.out.println(game.matches(GAME_STRING));
		System.out.println(StringUtils.getAllMatches(game, PgnMoveBuilder.MOVE));
	}
}
