/**
 *
 */
package jenjinn.engine.eval;

import static java.lang.Long.bitCount;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.collections.FList;
import xawd.jflow.iterators.Flow;

/**
 * @author ThomasB
 *
 */
public final class KingSafetyEvaluator implements EvaluationComponent
{
	static final FList<ChessPiece> WKING_ATTACKERS = ChessPieces.black().drop(1).take(4).toList();
	static final FList<ChessPiece> BKING_ATTACKERS = ChessPieces.white().drop(1).take(4).toList();

	public KingSafetyEvaluator()
	{
	}

	@Override
	public int evaluate(BoardState state)
	{
		KingSafetyTable kst = KingSafetyTable.INSTANCE;
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();

		BoardSquare wKingLoc = pieceLocs.iterateLocs(ChessPiece.WHITE_KING).next();
		KingSafetyArea wSafetyArea = KingSafetyArea.get(wKingLoc);

		int bAttackUnits = 0;
		for (ChessPiece piece : WKING_ATTACKERS) {
			Flow<BoardSquare> locs = pieceLocs.iterateLocs(piece);
			while (locs.hasNext()) {
				long control = piece.getSquaresOfControl(locs.next(), white, black);
				bAttackUnits += bitCount(control & wSafetyArea.getOuterArea()) * kst.getOuterUnitValue(piece);
				bAttackUnits += bitCount(control & wSafetyArea.getInnerArea()) * kst.getInnerUnitValue(piece);
			}
		}

		BoardSquare bKingLoc = pieceLocs.iterateLocs(ChessPiece.BLACK_KING).next();
		KingSafetyArea bSafetyArea = KingSafetyArea.get(bKingLoc);

		int wAttackUnits = 0;
		for (ChessPiece piece : BKING_ATTACKERS) {
			Flow<BoardSquare> locs = pieceLocs.iterateLocs(piece);
			while (locs.hasNext()) {
				long control = piece.getSquaresOfControl(locs.next(), white, black);
				wAttackUnits += bitCount(control & bSafetyArea.getOuterArea()) * kst.getOuterUnitValue(piece);
				wAttackUnits += bitCount(control & bSafetyArea.getInnerArea()) * kst.getInnerUnitValue(piece);
			}
		}

		return kst.indexSafetyTable(wAttackUnits) - kst.indexSafetyTable(bAttackUnits);
	}
}
