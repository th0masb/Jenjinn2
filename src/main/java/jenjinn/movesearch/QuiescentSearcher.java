/**
 *
 */
package jenjinn.movesearch;

import static jenjinn.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.Optional;

import jenjinn.base.GameTermination;
import jenjinn.base.Side;
import jenjinn.base.Square;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.DetailedPieceLocations;
import jenjinn.boardstate.MoveReversalData;
import jenjinn.boardstate.calculators.LegalMoves;
import jenjinn.boardstate.calculators.SquareControl;
import jenjinn.boardstate.calculators.TerminationState;
import jenjinn.eval.PieceValues;
import jenjinn.eval.StateEvaluator;
import jenjinn.eval.StaticExchangeEvaluator;
import jenjinn.moves.ChessMove;
import jenjinn.moves.EnpassantMove;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;
import jflow.iterators.Flow;
import jflow.iterators.factories.IterRange;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
public final class QuiescentSearcher
{
	public static final int DEPTH_CAP = 20;

	private final Seq<MoveReversalData> moveReversers;

	private final int deltaPruneSafetyMargin  = 200;
	private final int bigDelta                = calculateBigDelta();
	private final StateEvaluator evaluator    = new StateEvaluator(10);
	private final StaticExchangeEvaluator see = new StaticExchangeEvaluator();

	public QuiescentSearcher()
	{
		moveReversers = IterRange.to(DEPTH_CAP).mapToObject(i -> new MoveReversalData()).toSeq();
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
		Optional<ChessMove> firstMove = movesToProbe.nextOption();
		GameTermination terminalState = TerminationState.of(root, firstMove.isPresent());

		if (terminalState.isTerminal()) {
			return -Math.abs(terminalState.value);
		}
		Side active = root.getActiveSide(), passive = active.otherSide();
		DetailedPieceLocations pieceLocs = root.getPieceLocations();
		long passiveControl = SquareControl.calculate(root, passive);

		long activeKingLoc = pieceLocs.locationsOf(ChessPieces.of(active).last());
		boolean inCheck = bitboardsIntersect(activeKingLoc, passiveControl);

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
			Square source = move.getSource(), target = move.getTarget();
			int targVal = PieceValues.MIDGAME.valueOf(root.getPieceLocations().getPieceAt(target, passive));
			return standPat >= alpha - (targVal + deltaPruneSafetyMargin) && see.isGoodExchange(source, target, root);
		}
	}

	private int calculateBigDelta()
	{
		int leastValuable = PieceValues.MIDGAME.valueOf(Piece.WHITE_PAWN);
		int mostValuable = PieceValues.MIDGAME.valueOf(Piece.WHITE_QUEEN);
		return 2 * mostValuable - leastValuable;
	}
}
