/**
 *
 */
package jenjinn.moves;

import java.util.Set;

import jenjinn.base.CastleZone;
import jenjinn.base.DevelopmentPiece;
import jenjinn.base.Side;
import jenjinn.base.Square;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.MoveReversalData;
import jenjinn.pieces.Piece;
import jflow.iterators.Flow;
import jflow.iterators.factories.Iter;

/**
 * @author ThomasB
 *
 */
public final class PromotionMove extends AbstractChessMove
{
	private final PromotionResult promotionResult;

	public PromotionMove(Square start, Square target, PromotionResult promotionResult)
	{
		super(start, target);
		this.promotionResult = promotionResult;
	}

	public static Flow<ChessMove> generateAllPossibilities(Square start, Square target)
	{
		return Iter.over(PromotionResult.values()).map(res -> new PromotionMove(start, target, res));
	}

	public PromotionResult getPromotionResult()
	{
		return promotionResult;
	}

	@Override
	void updatePieceLocations(BoardState state, MoveReversalData unmakeDataStore)
	{
		Side activeSide = state.getActiveSide();
		state.getPieceLocations().removePieceAt(getSource(), activeSide.isWhite() ? Piece.WHITE_PAWN : Piece.BLACK_PAWN);
		state.getPieceLocations().addPieceAt(getTarget(), promotionResult.toPiece(activeSide));

		Piece removedPiece = state.getPieceLocations().getPieceAt(getTarget(), activeSide.otherSide());
		if (removedPiece != null) {
			state.getPieceLocations().removePieceAt(getTarget(), removedPiece);
			unmakeDataStore.setPieceTaken(removedPiece);
		}
		else {
			unmakeDataStore.setPieceTaken(null);
		}

		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		state.getHalfMoveClock().resetValue();
	}

	@Override
	void resetPieceLocations(BoardState state, MoveReversalData unmakeDataStore)
	{
		Side activeSide = state.getActiveSide();
		state.getPieceLocations().removePieceAt(getTarget(), promotionResult.toPiece(activeSide));
		state.getPieceLocations().addPieceAt(getSource(), activeSide.isWhite() ? Piece.WHITE_PAWN : Piece.BLACK_PAWN);

		Piece pieceToReplace = unmakeDataStore.getPieceTaken();
		if (pieceToReplace != null) {
			state.getPieceLocations().addPieceAt(getTarget(), pieceToReplace);
		}
	}

	@Override
	Set<CastleZone> getAllRightsToBeRemoved()
	{
		return MoveConstants.EMPTY_RIGHTS_SET;
	}

	@Override
	DevelopmentPiece getPieceDeveloped()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return new StringBuilder(getClass().getSimpleName())
				.append("[source=")
				.append(getSource().name().toLowerCase())
				.append("|target=")
				.append(getTarget().name().toLowerCase())
				.append("|result=")
				.append(promotionResult.name())
				.append("]")
				.toString();
	}

	@Override
	public String toCompactString()
	{
		return new StringBuilder("P")
				.append(getSource().name())
				.append(getTarget().name())
				.append(promotionResult.name())
				.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (super.equals(obj)) {
			PromotionMove other = (PromotionMove) obj;
			return promotionResult.equals(other.promotionResult);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return 31*super.hashCode() + promotionResult.hashCode();
	}
}
