/**
 *
 */
package jenjinn.engine.movesearch;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.Optional;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.GameTermination;
import jenjinn.engine.base.Side;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.boardstate.MoveReversalData;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.boardstate.calculators.SquareControl;
import jenjinn.engine.boardstate.calculators.TerminationState;
import jenjinn.engine.eval.PieceValues;
import jenjinn.engine.eval.StateEvaluator;
import jenjinn.engine.eval.StaticExchangeEvaluator;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnpassantMove;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.collections.FList;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 */
public final class QuiescentSearcher
{
	public static final int DEPTH_CAP = 20;

	private final FList<MoveReversalData> moveReversers;

	private final int deltaPruneSafetyMargin  = 200;
	private final int bigDelta                = calculateBigDelta();
	private final StateEvaluator evaluator    = new StateEvaluator(10);
	private final StaticExchangeEvaluator see = new StaticExchangeEvaluator();

	public QuiescentSearcher()
	{
		moveReversers = IterRange.to(DEPTH_CAP).mapToObject(i -> new MoveReversalData()).toList();
	}

	void resetMoveReversalData()
	{
		moveReversers.forEach(x -> x.reset());
	}

	public int search(BoardState root) throws InterruptedException
	{
		return search(root, IntConstants.INITIAL_ALPHA, IntConstants.INITIAL_BETA, DEPTH_CAP);
	}

	private int search(BoardState root, int alpha, int beta, int depth) throws InterruptedException
	{
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}

		Flow<ChessMove> movesToProbe = LegalMoves.getAllMoves(root);
		Optional<ChessMove> firstMove = movesToProbe.safeNext();
		GameTermination terminalState = TerminationState.of(root, firstMove.isPresent());

		if (terminalState.isTerminal()) {
			return -Math.abs(terminalState.value);
		}
		Side active = root.getActiveSide(), passive = active.otherSide();
		DetailedPieceLocations pieceLocs = root.getPieceLocations();
		long passiveControl = SquareControl.calculate(root, passive);

		boolean inCheck = bitboardsIntersect(pieceLocs.locationsOf(ChessPieces.king(active)),
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
			int standPat = evaluator.evaluate(root);

			if (standPat >= beta) {
				return beta;
			} else if (depth == 0) {
				/*
				 * We return the maximum under the assumption that there exists at least one
				 * move that can improve our position which is sound.
				 */
				return Math.max(alpha, standPat);
			}

			/*
			 * /!\ Watch numeric overflow here
			 */
			if (standPat < alpha - bigDelta) {
				/*
				 * We return here if there is no way we can raise alpha by taking enemy
				 * material.
				 */
				return alpha;
			}

			alpha = Math.max(alpha, standPat);
			int finalizedAlpha = alpha;
			movesToProbe = LegalMoves.getAttacks(root).filter(mv -> filterMove(root, mv, standPat, finalizedAlpha));
		}

		while (movesToProbe.hasNext()) {
			ChessMove nextMove = movesToProbe.next();
			MoveReversalData reversingdata = moveReversers.get(depth - 1);
			nextMove.makeMove(root, reversingdata);
			int score = -search(root, -beta, -alpha, depth - 1);
			nextMove.reverseMove(root, reversingdata);

			if (score >= beta) {
				return beta;
			}
			alpha = Math.max(alpha, score);
		}

		return alpha;
	}

	private boolean filterMove(BoardState root, ChessMove move, int standPat, int alpha)
	{
		if (move instanceof EnpassantMove) {
			return standPat >= alpha - (PieceValues.MIDGAME.valueOfPawn() + deltaPruneSafetyMargin);
		} else {
			Side active = root.getActiveSide(), passive = active.otherSide();
			BoardSquare source = move.getSource(), target = move.getTarget();
			int targVal = PieceValues.MIDGAME.valueOf(root.getPieceLocations().getPieceAt(target, passive));
			return standPat >= alpha - (targVal + deltaPruneSafetyMargin) && see.isGoodExchange(source, target, root);
		}
	}

	private int calculateBigDelta()
	{
		int leastValuable = PieceValues.MIDGAME.valueOf(ChessPiece.WHITE_PAWN);
		int mostValuable = PieceValues.MIDGAME.valueOf(ChessPiece.WHITE_QUEEN);
		return 2 * mostValuable - leastValuable;
	}
}
