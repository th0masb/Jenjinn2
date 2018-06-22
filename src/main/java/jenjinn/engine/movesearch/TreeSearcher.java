/**
 *
 */
package jenjinn.engine.movesearch;

import java.util.Optional;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.boardstate.calculators.TerminationState;
import jenjinn.engine.enums.GameTermination;
import jenjinn.engine.enums.TreeNodeType;
import jenjinn.engine.misc.Infinity;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.movesearch.TranspositionTable.Entry;
import jenjinn.engine.stringutils.VisualGridGenerator;
import xawd.jflow.collections.FlowList;
import xawd.jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 *
 */
public enum TreeSearcher
{
	INSTANCE;

	private final TranspositionTable table = new TranspositionTable(15);
	private final int maxDepth = 20;
	private final FlowList<DataForReversingMove> moveReversers =
			IterRange.to(maxDepth)
			.mapToObject(i -> new DataForReversingMove()).toList();

	private int bestFirstMoveIndex = -1;

	public Optional<ChessMove> getBestMoveFrom(BoardState root)
	{
		final Optional<ChessMove> legalMoves = LegalMoves.getMoves(root).safeNext();
		if (TerminationState.of(root, legalMoves.isPresent()).isTerminal()) {
			return Optional.empty();
		}
		bestFirstMoveIndex = -1;

		throw new RuntimeException();
	}

	private int negamax(BoardState root, int alpha, int beta, int depth) throws InterruptedException
	{
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}

		// Check whether this is a terminal node of the game tree.
		final Optional<ChessMove> firstMove = LegalMoves.getMoves(root).safeNext();
		final GameTermination termination = TerminationState.of(root, firstMove.isPresent());
		if (termination.isTerminal()) {
			return -Math.abs(termination.value);
		}
		else if (depth == 0) {
			BoardState cpy = root.copy();
			try {
				return QuiescentSearcher.search(root, Infinity.IC_ALPHA, Infinity.IC_BETA, QuiescentSearcher.DEPTH_CAP);
			}
			catch (final Throwable t) {
				System.out.println(cpy.getActiveSide());
				System.out.println(VisualGridGenerator.from(cpy.getPieceLocations()));
				throw t;
			}
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

		final FlowList<ChessMove> legalMoves = LegalMoves.getMoves(root).toList();
		final int[] moveIndices = IterRange.to(legalMoves.size()).toArray();
		changeFirstIndex(moveIndices, recommendedFirstMoveIndex);

		int bestValue = -Infinity.INT_INFINITY;
		int bestMoveIndex = -1, refutationMoveIndex = -1;
		for (final int i : moveIndices) {
			final ChessMove mv = legalMoves.get(i);
			final DataForReversingMove reverser = moveReversers.get(depth);
			mv.makeMove(root, reverser);
			final int bestReply = -negamax(root, -beta, -alpha, depth - 1);
			mv.reverseMove(root, reverser);
			bestMoveIndex = bestReply > alpha? i : bestMoveIndex;
			alpha = Math.max(alpha, bestReply);
			bestValue = Math.max(bestValue, bestReply);
			if (alpha >= beta) {
				refutationMoveIndex = i;
				break;
			}
		}
		if (bestValue <= alpha) {
			updateEntryToNewAllEntry(tableEntry, rootHash, bestValue, depth);
		}
		else if (bestValue >= beta) {
			updateEntryToNewCutEntry(tableEntry, rootHash, bestValue, refutationMoveIndex, depth);
		}
		else{
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

	private void updateEntryToNewCutEntry(Entry currentEntry, long newHash, int bestValue, int refutationMoveIndex, int depth)
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

	public static void main(String[] args) throws InterruptedException
	{
		System.out.println(INSTANCE.negamax(StartStateGenerator.createStartBoard(), Infinity.IC_ALPHA, Infinity.IC_BETA, 6));
	}
}
