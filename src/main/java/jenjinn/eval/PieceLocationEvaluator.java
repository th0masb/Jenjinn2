/**
 *
 */
package jenjinn.eval;

import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.DetailedPieceLocations;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;

/**
 * This evaluating component measures the material balance on the board as well
 * as taking into account the positions of the material. It performs an
 * interpolation between midgame and endgame scores by looking at the total
 * amount of material on the board to calculate a 'game phase' which is then
 * used in the interpolation between the two scores. Not that the positional
 * eval is incrementally updated during the making/unmaking of moves so we
 * don't have to perform the full calculation here.
 *
 * @author ThomasB
 */
public class PieceLocationEvaluator implements EvaluationComponent
{
	private static final int[] PIECE_PHASE_VALUES = { 0, 1, 1, 2, 4, 0 };

	public PieceLocationEvaluator()
	{
	}

	@Override
	public int evaluate(BoardState state)
	{
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		int gamePhase = calculateGamePhase(pieceLocs);
		int midgameEval = pieceLocs.getMidgameEval(), endgameEval = pieceLocs.getEndgameEval();
		return ((midgameEval * (256 - gamePhase)) + endgameEval * gamePhase) / 256;
	}

	private int calculateGamePhase(DetailedPieceLocations plocs)
	{
		int piecePhase = 24;
		for (int i = 1; i < 5; i++) {
			Piece wp = ChessPieces.fromIndex(i), bp = ChessPieces.fromIndex(i + 6);
			int totalPieces = plocs.pieceCountOf(wp) + plocs.pieceCountOf(bp);
			piecePhase -= totalPieces * PIECE_PHASE_VALUES[i];
		}
		return (piecePhase * 256 + 12) / 24;
	}
}
