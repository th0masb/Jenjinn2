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

	public static List<ChessMove> parse(String pgnInput) throws BadPgnException
	{
		String pgn = pgnInput.trim();
		if (pgn.matches(GAME_STRING)) {
			BoardState state = StartStateGenerator.createStartBoard();
			List<String> encodedMoves = Strings.allMatches(pgn, PgnMoveBuilder.MOVE).toList();
			List<ChessMove> decodedMoves = new ArrayList<>(encodedMoves.size());
			for (String encodedMove : encodedMoves) {
				ChessMove decodedMove = PgnMoveBuilder.convertPgnCommand(state, encodedMove);
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
		String pgn = "1.e4 e5 2.Nc3 Nc6 3.Bc4 Nf6 4.d3 Bc5 5.Bg5 h6 6.Bh4 d6 7.Na4 Bb6 8.Nxb6 axb6 9.c3 Be6 10.Bxe6 fxe6 11.Bxf6 Qxf6 12.Ne2 Ne7 13.Qd2 Ng6 14.O-O Nf4 15.Nxf4 exf4 16.e5 dxe5 17.Rfe1 O-O-O 18.Re4 Rd5 19.Qe2 Rhd8 20.Rd1 Qg5 21.c4 f3 22.Qf1 Rxd3 23.Rxd3 Rxd3 24.h4 Qg6 25.Re1 Rd4 26.g3 Rxh4 27.Rxe5 Re4 28.Rb5 Re2 29.Rb3 Qe4 30.Kh2 Re1 31.Qd3 Qxd3 32.Rxd3 Re5 33.g4 Re4 34.Kg3 Rxc4 35.Rxf3 Rc2 36.Rf7 e5 37.Re7 Rxb2 38.Re6 Rxa2  0-1"; //39.Kxg6
		List<ChessMove> moves = parse(pgn);
		BoardState state = StartStateGenerator.createStartBoard();
		System.out.println(VisualGridGenerator.from(state.getPieceLocations()));
		for (ChessMove mv : moves) {
			mv.makeMove(state);
			System.out.println(VisualGridGenerator.from(state.getPieceLocations()));
		}
	}
}
