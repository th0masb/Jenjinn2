/**
 *
 */
package jenjinn.engine.movesearch;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.Optional;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.boardstate.calculators.SquareControl;
import jenjinn.engine.boardstate.calculators.TerminationState;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.GameTermination;
import jenjinn.engine.enums.Side;
import jenjinn.engine.eval.PieceValues;
import jenjinn.engine.eval.StateEvaluator;
import jenjinn.engine.eval.StaticExchangeEvaluator;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnpassantMove;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.collections.FlowList;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 */
public final class QuiescentSearcher
{
	static final int DEPTH_CAP = 15;

	static final FlowList<DataForReversingMove> MOVE_REVERSERS = IterRange.to(DEPTH_CAP)
			.mapToObject(i -> new DataForReversingMove()).toImmutableList();

	static final int DP_SAFETY_MARGIN = 200;

	static final int BIG_DELTA = calculateBigDelta();

	private static int calculateBigDelta()
	{
		final int leastValuable = PieceValues.MIDGAME.valueOf(ChessPiece.WHITE_PAWN);
		final int mostValuable = PieceValues.MIDGAME.valueOf(ChessPiece.WHITE_QUEEN);
		return 2 * mostValuable - leastValuable;
	}

	private QuiescentSearcher()
	{
	}

	public static int search(BoardState root, int alpha, int beta, int depth)
	{
		Flow<ChessMove> movesToProbe = LegalMoves.getMoves(root);
		final Optional<ChessMove> firstMove = movesToProbe.safeNext();
		final GameTermination terminalState = TerminationState.of(root, firstMove.isPresent());

		if (terminalState.isTerminal()) {
			return -Math.abs(terminalState.value);
		}
		final Side active = root.getActiveSide(), passive = active.otherSide();
		final DetailedPieceLocations pieceLocs = root.getPieceLocations();
		final long passiveControl = SquareControl.calculate(root, passive);

		final boolean inCheck = bitboardsIntersect(pieceLocs.locationOverviewOf(ChessPieces.king(active)),
				passiveControl);

		if (inCheck) {
			if (depth == 0) {
				/*
				 * I think this is sound, basically we reason that if we are in check then we
				 * assume that it's not better than anything we've already found.
				 */
				return alpha;
			}
			movesToProbe = movesToProbe.insert(firstMove.get());
		} else {
			final int standPat = StateEvaluator.INSTANCE.evaluate(root);

			if (standPat >= beta) {
				return beta;
			} else if (depth == 0) {
				/*
				 * We return the maximum under the assumption that there exists at least one
				 * move that can improve our position which is sound.
				 */
				return Math.max(alpha, standPat);
			}

			if (standPat < alpha - BIG_DELTA) {
				/*
				 * We return here if there is no way we can raise alpha by taking enemy
				 * material.
				 */
				return alpha;
			}

			alpha = Math.max(alpha, standPat);
			final int finalizedAlpha = alpha;
			movesToProbe = LegalMoves.getAttacks(root).filter(mv -> filterMove(root, mv, standPat, finalizedAlpha));
		}

		while (movesToProbe.hasNext()) {
			final ChessMove nextMove = movesToProbe.next();
			final DataForReversingMove reversingdata = MOVE_REVERSERS.get(depth - 1);
			nextMove.makeMove(root, reversingdata);
			final int score = -search(root, -beta, -alpha, depth - 1);
			nextMove.reverseMove(root, reversingdata);

			if (score >= beta) {
				return beta;
			}
			alpha = Math.max(alpha, score);
		}

		return alpha;
	}

	private static boolean filterMove(BoardState root, ChessMove move, int standPat, int alpha)
	{
		if (move instanceof EnpassantMove
				&& standPat >= alpha - (PieceValues.MIDGAME.valueOfPawn() + DP_SAFETY_MARGIN)) {
			return true;
		} else {
			final Side active = root.getActiveSide(), passive = active.otherSide();
			final BoardSquare source = move.getSource(), target = move.getTarget();
			final int targVal = PieceValues.MIDGAME.valueOf(root.getPieceLocations().getPieceAt(target, passive));
			return standPat >= alpha - (targVal + DP_SAFETY_MARGIN)
					&& StaticExchangeEvaluator.INSTANCE.isGoodExchange(target, source, root);
		}
	}
}
