/**
 *
 */
package jenjinn.engine.movesearch;

import static jenjinn.engine.movesearch.QuiescentSearcher.DEPTH_CAP;
import static jenjinn.engine.utils.IntConstants.INITIAL_ALPHA;
import static jenjinn.engine.utils.IntConstants.INITIAL_BETA;

import java.util.Optional;

import jenjinn.engine.base.GameTermination;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.MoveReversalData;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.boardstate.calculators.TerminationState;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.movesearch.TranspositionTable.Entry;
import jenjinn.engine.utils.IntConstants;
import xawd.jflow.collections.FlowList;
import xawd.jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 */
public final class TreeSearcher
{
	private final QuiescentSearcher quiescent = new QuiescentSearcher();
	private final TranspositionTable table = new TranspositionTable(15);
	private final int maxDepth = 20;

	private final FlowList<MoveReversalData> moveReversers;
	private int bestFirstMoveIndex = -1;

	public TreeSearcher()
	{
		moveReversers = IterRange.to(maxDepth).mapToObject(i -> new MoveReversalData()).toList();
	}

	/**
	 * Given some root state this method heuristically calculates the 'best' move
	 * that the active side in the root state could take to improve their position.
	 * It is assumed that any calls to this method will be made on some dedicated
	 * 'calculation thread'.
	 *
	 * @param root
	 *            The state in which calculate the best move for the active side.
	 * @param timeLimit
	 *            The execution time limit (in milliseconds) for this method. When
	 *            the time limit is reached the best move which has currently been
	 *            calculated will be returned.
	 * @return Nothing if there are no legal moves, otherwise the 'best' move
	 *         available.
	 */
	public synchronized Optional<ChessMove> getBestMoveFrom(BoardState root, long timeLimit)
	{
		final Optional<ChessMove> legalMoves = LegalMoves.getAllMoves(root).safeNext();
		if (TerminationState.of(root, legalMoves.isPresent()).isTerminal()) {
			return Optional.empty();
		}
		bestFirstMoveIndex = -1;

		ChessMove bestMove;
		try {
			bestMove = getBestMoveFrom(root, 1);
		} catch (final InterruptedException ex) {
			throw new AssertionError("Interruption not possible here.");
		}

		createInterruptingTimerThread(timeLimit).start();

		for (int targetDepth = 2; targetDepth <= maxDepth; targetDepth++) {
			try {
				final ChessMove newBestMove = getBestMoveFrom(root, targetDepth);
				bestMove = newBestMove;
			} catch (final InterruptedException e) {
				Thread.interrupted();
				moveReversers.forEach(x -> x.reset());
				quiescent.resetMoveReversalData();
				break;
			}
		}
		return Optional.of(bestMove);
	}

	private Thread createInterruptingTimerThread(long timeLimit)
	{
		final Thread toInterrupt = Thread.currentThread();
		return new Thread(() -> {
			try {
				Thread.sleep(timeLimit);
				if (toInterrupt.isAlive()) {
					toInterrupt.interrupt();
				}
			} catch (final InterruptedException e) {
				throw new AssertionError("Interruption not possible here.");
			}
		});
	}

	private ChessMove getBestMoveFrom(BoardState root, int depth) throws InterruptedException
	{
		final FlowList<ChessMove> legalMoves = LegalMoves.getAllMoves(root).toList();
		final int[] indices = IterRange.to(legalMoves.size()).toArray();
		changeFirstIndex(indices, bestFirstMoveIndex);

		int alpha = INITIAL_ALPHA;
		for (final int index : indices) {
			final ChessMove mv = legalMoves.get(index);
			final MoveReversalData reversalData = moveReversers.get(depth);
			mv.makeMove(root, reversalData);
			final int bestReply = -negamax(root, -INITIAL_BETA, -alpha, depth - 1);
			mv.reverseMove(root, reversalData);
			if (bestReply > alpha) {
				alpha = bestReply;
				bestFirstMoveIndex = index;
			}
		}
		// System.out.println(alpha);
		return legalMoves.get(bestFirstMoveIndex);
	}

	private int negamax(BoardState root, int alpha, int beta, int depth) throws InterruptedException
	{
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}

		final Optional<ChessMove> firstMove = LegalMoves.getAllMoves(root).safeNext();
		final GameTermination termination = TerminationState.of(root, firstMove.isPresent());
		if (termination.isTerminal()) {
			return -Math.abs(termination.value);
		} else if (depth == 0) {
			final int qsearch = quiescent.search(root, INITIAL_ALPHA, INITIAL_BETA, DEPTH_CAP);
			return qsearch;
		}

		final long rootHash = root.calculateHash();
		final Entry tableEntry = table.get(rootHash);
		int recommendedFirstMoveIndex = -1;
		if (tableEntry != null && tableEntry.matches(rootHash)) {
			if (tableEntry.depthSearched >= depth) {
				switch (tableEntry.type) {
				case PRINCIPLE_VALUE:
					return tableEntry.score;
				case CUT:
					alpha = Math.max(alpha, tableEntry.score);
					break;
				case ALL:
					beta = Math.min(beta, tableEntry.score);
					break;
				}
				if (alpha >= beta) {
					return beta;
				}
			}
			recommendedFirstMoveIndex = tableEntry.notableMoveIndex;
		}

		final FlowList<ChessMove> legalMoves = LegalMoves.getAllMoves(root).toList();
		final int[] moveIndices = IterRange.to(legalMoves.size()).toArray();
		changeFirstIndex(moveIndices, recommendedFirstMoveIndex);

		int bestValue = -IntConstants.MAX_NEGATABLE_VALUE;
		int bestMoveIndex = -1, refutationMoveIndex = -1;
		for (final int i : moveIndices) {
			final ChessMove mv = legalMoves.get(i);
			final MoveReversalData reverser = moveReversers.get(depth);
			mv.makeMove(root, reverser);
			final int bestReply = -negamax(root, -beta, -alpha, depth - 1);
			mv.reverseMove(root, reverser);
			bestMoveIndex = bestReply > alpha ? i : bestMoveIndex;
			alpha = Math.max(alpha, bestReply);
			bestValue = Math.max(bestValue, bestReply);
			if (alpha >= beta) {
				refutationMoveIndex = i;
				break;
			}
		}
		if (bestValue <= alpha) {
			updateEntryToNewAllEntry(tableEntry, rootHash, bestValue, depth);
		} else if (bestValue >= beta) {
			updateEntryToNewCutEntry(tableEntry, rootHash, bestValue, refutationMoveIndex, depth);
		} else {
			updateEntryToNewPVEntry(tableEntry, rootHash, bestValue, bestMoveIndex, depth);
		}
		return Math.min(beta, Math.max(alpha, bestValue));
	}

	private void updateEntryToNewAllEntry(Entry currentEntry, long newHash, int bestValue, int depth)
	{
		if (currentEntry == null) {
			currentEntry = new Entry();
			table.set(newHash, currentEntry);
		}
		currentEntry.positionHash = newHash;
		currentEntry.score = bestValue;
		currentEntry.depthSearched = depth;
		currentEntry.type = TreeNodeType.ALL;
	}

	private void updateEntryToNewCutEntry(Entry currentEntry, long newHash, int bestValue, int refutationMoveIndex,
			int depth)
	{
		if (currentEntry == null) {
			currentEntry = new Entry();
			table.set(newHash, currentEntry);
		}
		currentEntry.positionHash = newHash;
		currentEntry.score = bestValue;
		currentEntry.depthSearched = depth;
		currentEntry.notableMoveIndex = refutationMoveIndex;
		currentEntry.type = TreeNodeType.CUT;
	}

	private void updateEntryToNewPVEntry(Entry currentEntry, long newHash, int bestValue, int bestMoveIndex, int depth)
	{
		if (currentEntry == null) {
			currentEntry = new Entry();
			table.set(newHash, currentEntry);
		}
		currentEntry.positionHash = newHash;
		currentEntry.score = bestValue;
		currentEntry.depthSearched = depth;
		currentEntry.notableMoveIndex = bestMoveIndex;
		currentEntry.type = TreeNodeType.PRINCIPLE_VALUE;
	}

	private void changeFirstIndex(final int[] indices, final int recommendedMoveIndex)
	{
		if (recommendedMoveIndex > -1) {
			final int tmp = indices[0];
			indices[0] = indices[recommendedMoveIndex];
			indices[recommendedMoveIndex] = tmp;
		}
	}
}
