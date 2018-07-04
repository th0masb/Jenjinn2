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
import xawd.jflow.collections.FlowList;
import xawd.jflow.iterators.Flow;

/**
 * @author ThomasB
 *
 */
public final class KingSafetyEvaluator implements EvaluationComponent
{
	static final FlowList<ChessPiece> WKING_ATTACKERS = ChessPieces.black().drop(1).take(4).toList();
	static final FlowList<ChessPiece> BKING_ATTACKERS = ChessPieces.white().drop(1).take(4).toList();

	public KingSafetyEvaluator()
	{
	}

	@Override
	public int evaluate(BoardState state)
	{
		final KingSafetyTable kst = KingSafetyTable.INSTANCE;
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();

		final BoardSquare wKingLoc = pieceLocs.iterateLocs(ChessPiece.WHITE_KING).next();
		final KingSafetyArea wSafetyArea = KingSafetyArea.get(wKingLoc);

		int bAttackUnits = 0;
		for (final ChessPiece piece : WKING_ATTACKERS) {
			final Flow<BoardSquare> locs = pieceLocs.iterateLocs(piece);
			while (locs.hasNext()) {
				final long control = piece.getSquaresOfControl(locs.next(), white, black);
				bAttackUnits += bitCount(control & wSafetyArea.getOuterArea()) * kst.getOuterUnitValue(piece);
				bAttackUnits += bitCount(control & wSafetyArea.getInnerArea()) * kst.getInnerUnitValue(piece);
//				bAttackUnits += bitCount(control & wSafetyArea.getCheckArea()) * kst.getCheckUnitValue(piece);
			}
		}

		final BoardSquare bKingLoc = pieceLocs.iterateLocs(ChessPiece.BLACK_KING).next();
		final KingSafetyArea bSafetyArea = KingSafetyArea.get(bKingLoc);

		int wAttackUnits = 0;
		for (final ChessPiece piece : BKING_ATTACKERS) {
			final Flow<BoardSquare> locs = pieceLocs.iterateLocs(piece);
			while (locs.hasNext()) {
				final long control = piece.getSquaresOfControl(locs.next(), white, black);
				wAttackUnits += bitCount(control & bSafetyArea.getOuterArea()) * kst.getOuterUnitValue(piece);
				wAttackUnits += bitCount(control & bSafetyArea.getInnerArea()) * kst.getInnerUnitValue(piece);
//				wAttackUnits += bitCount(control & bSafetyArea.getCheckArea()) * kst.getCheckUnitValue(piece);
			}
		}

		return kst.indexSafetyTable(wAttackUnits) - kst.indexSafetyTable(bAttackUnits);
	}
}
