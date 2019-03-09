/**
 *
 */
package jenjinn.eval;

import static java.lang.Long.bitCount;

import com.github.maumay.jflow.iterators.EnhancedIterator;
import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.Square;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.DetailedPieceLocations;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;

/**
 * @author ThomasB
 *
 */
public final class KingSafetyEvaluator implements EvaluationComponent
{
	static final Vec<Piece> WKING_ATTACKERS = ChessPieces.BLACK.skip(1).take(4);
	static final Vec<Piece> BKING_ATTACKERS = ChessPieces.WHITE.skip(1).take(4);

	public KingSafetyEvaluator()
	{
	}

	@Override
	public int evaluate(BoardState state)
	{
		KingSafetyTable kst = KingSafetyTable.INSTANCE;
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();

		Square wKingLoc = pieceLocs.iterateLocs(Piece.WHITE_KING).next();
		KingSafetyArea wSafetyArea = KingSafetyArea.get(wKingLoc);

		int bAttackUnits = 0;
		for (Piece piece : WKING_ATTACKERS) {
			EnhancedIterator<Square> locs = pieceLocs.iterateLocs(piece);
			while (locs.hasNext()) {
				long control = piece.getSquaresOfControl(locs.next(), white, black);
				bAttackUnits += bitCount(control & wSafetyArea.getOuterArea())
						* kst.getOuterUnitValue(piece);
				bAttackUnits += bitCount(control & wSafetyArea.getInnerArea())
						* kst.getInnerUnitValue(piece);
			}
		}

		Square bKingLoc = pieceLocs.iterateLocs(Piece.BLACK_KING).next();
		KingSafetyArea bSafetyArea = KingSafetyArea.get(bKingLoc);

		int wAttackUnits = 0;
		for (Piece piece : BKING_ATTACKERS) {
			EnhancedIterator<Square> locs = pieceLocs.iterateLocs(piece);
			while (locs.hasNext()) {
				long control = piece.getSquaresOfControl(locs.next(), white, black);
				wAttackUnits += bitCount(control & bSafetyArea.getOuterArea())
						* kst.getOuterUnitValue(piece);
				wAttackUnits += bitCount(control & bSafetyArea.getInnerArea())
						* kst.getInnerUnitValue(piece);
			}
		}

		return kst.indexSafetyTable(wAttackUnits) - kst.indexSafetyTable(bAttackUnits);
	}
}
