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
import jenjinn.engine.utils.VisualGridGenerator;
import xawd.jflow.utilities.Strings;

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
			final List<String> encodedMoves = Strings.getAllMatches(pgn, PgnMoveBuilder.MOVE);
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
		final String pgn = "1.e4 e5 2.Bc4 Nf6 3.d4 exd4 4.e5 d5 5.Qxd4 dxc4 6.Qxd8+ Kxd8 7.exf6 gxf6 8.Nf3 Nc6 9.Be3 Bf5 10.c3 Ne5 11.Nxe5 fxe5 12.Nd2 Bd3 13.Bg5+ Kd7 0-1";// 14.O-O-O Bd6 15.g3 Rhg8 16.Be3 Rae8 17.f3 f6 18.Rde1 b5 19.Nb1 a5 20.Rhg1 b4 21.Bd2 Rb8 22.Rg2 c5 23.Rf2 a4 24.Rg2 a3 25.Rf2 axb2+ 26.Kxb2 bxc3+ 27.Kxc3 Bxb1 28.Bc1 Bd3 29.Bb2 Bc7  0-1";
		final List<ChessMove> moves = parse(pgn);
		final BoardState state = StartStateGenerator.createStartBoard();
		System.out.println(VisualGridGenerator.from(state.getPieceLocations()));
		for (final ChessMove mv : moves) {
			mv.makeMove(state);
			System.out.println(VisualGridGenerator.from(state.getPieceLocations()));
		}
	}
}
