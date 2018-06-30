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
import jenjinn.engine.stringutils.VisualGridGenerator;
import xawd.jflow.utilities.StringUtils;

/**
 * @author t
 *
 */
public final class PgnGameConverter
{
	public static final String GAME_START = "(^1\\." + STANDARD_MOVE + ")";
	public static final String GAME_TERMINATION = "(((1\\-0)|(0\\-1)|(1/2\\-1/2)|(\\*))$)";
	private static final String GAME_STRING = GAME_START + ".*" + GAME_TERMINATION;

	private PgnGameConverter()
	{
	}

	public static List<ChessMove> parse(final String pgnInput) throws BadPgnException
	{
		final String pgn = pgnInput.trim();
		if (pgn.matches(GAME_STRING)) {
			final BoardState state = StartStateGenerator.createStartBoard();
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

	public static void main(String[] args) throws BadPgnException
	{
		final String pgn = "1.e4 e5 2.Bc4 d5 3.Bxd5 Be6 4.Bxb7 Nf6 5.Bxa8 Nxe4 6.Bxe4 Qe7 7.Nc3 g5 8.d4 f5 9.d5 Bh6 10.dxe6 fxe4 11.Nd5 Qd6 12.Nf6+ Ke7 13.Qxd6+ 1-0";//Kxf6 14.Qxc7 Rc8 15.Qf7+  1-0";
		final List<ChessMove> moves = parse(pgn);
		final BoardState state = StartStateGenerator.createStartBoard();
		System.out.println(VisualGridGenerator.from(state.getPieceLocations()));
		for (final ChessMove mv : moves) {
			mv.makeMove(state);
			System.out.println(VisualGridGenerator.from(state.getPieceLocations()));
		}
	}
}
