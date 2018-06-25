/**
 *
 */
package jenjinn.engine.eval;

import static java.lang.Long.bitCount;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.Bitboards.fileBitboard;
import static jenjinn.engine.bitboards.Bitboards.rankBitboard;

import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardHasher;
import jenjinn.engine.eval.PawnTable.Entry;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 *
 */
public final class PawnStructureEvaluator implements EvaluationComponent
{
	public static final int SEMIOPEN_FILE_BONUS = 300;

	public static final int CHAIN_BONUS = 100;
	public static final int PASSED_BONUS = 800;
	public static final int[] PHALANX_BONUSES = {0, 0, 700, 500, 50, 0, 0, 0, 0};

	public static final int DOUBLED_PENALTY = 700;
	public static final int ISOLATED_PENALTY = 600;
	public static final int BACKWARD_PENALTY = 500;

	private final PawnTable cachedEvaluations;

	public PawnStructureEvaluator(int tableSize)
	{
		cachedEvaluations = new PawnTable(tableSize);
	}

	@Override
	public int evaluate(BoardState state)
	{
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long wpawns = pieceLocs.locationOverviewOf(ChessPiece.WHITE_PAWN);
		final long bpawns = pieceLocs.locationOverviewOf(ChessPiece.BLACK_PAWN);
		final long pawnHash = calculatePawnPositionHash(wpawns, bpawns);

		final PawnTable.Entry cached = cachedEvaluations.get(pawnHash);
		if (cached == null) {
			final Entry newEntry = new PawnTable.Entry(pawnHash, calculateOverallScore(wpawns, bpawns));
			cachedEvaluations.set(newEntry);
			return newEntry.eval;
		}
		else if (cached.hash == pawnHash) {
			return cached.eval;
		}
		else {
			cached.hash = pawnHash;
			cached.eval = calculateOverallScore(wpawns, bpawns);
			return cached.eval;
		}
	}

	public static int calculateOverallScore(long wpawns, long bpawns)
	{
		int score = evaluateBackwardPawns(wpawns, bpawns);
		score += evaluateDoubledPawns(wpawns, bpawns);
		score += evaluateIsolatedPawns(wpawns, bpawns);
		score += evaluatePassedPawns(wpawns, bpawns);
		score += evaluatePawnChains(wpawns, bpawns);
		score += evaluatePhalanxFormations(wpawns);
		score -= evaluatePhalanxFormations(bpawns);
		return score;
	}

	private long calculatePawnPositionHash(long wpawns, long bpawns)
	{
		final ChessPiece wp = ChessPiece.WHITE_PAWN, bp = ChessPiece.BLACK_PAWN;

		final long whash = BitboardIterator.from(wpawns)
				.mapToLong(sq -> BoardHasher.INSTANCE.getSquarePieceFeature(sq, wp))
				.fold(0L, (a, b) -> a ^ b);

		final long bhash = BitboardIterator.from(bpawns)
				.mapToLong(sq -> BoardHasher.INSTANCE.getSquarePieceFeature(sq, bp))
				.fold(0L, (a, b) -> a ^ b);

		return whash ^ bhash;
	}

	public static int evaluatePawnChains(long wpawns, long bpawns)
	{
		final long hfile = fileBitboard(0), afile = fileBitboard(7);

		final long wpawnLeft = (wpawns & ~afile) << 9, wpawnRight = (wpawns & ~hfile) << 7;
		final long bpawnLeft = (bpawns & ~afile) >>> 7, bpawnRight = (bpawns & ~hfile) >>> 9;

		return CHAIN_BONUS *(bitCount(wpawnLeft & wpawns)
				+ bitCount(wpawnRight & wpawns)
				- bitCount(bpawnLeft & bpawns)
				- bitCount(bpawnRight & bpawns));
	}

	public static int evaluatePhalanxFormations(long pawns)
	{
		int score = 0;
		for (int i = 0; i < 8; i++) {
			final long pawnsOnIthRank = rankBitboard(i) & pawns;
			if (bitCount(pawnsOnIthRank) > 1) {
				int phalanxCount = 0;
				for (int j = 0; j < 8; j++) {
					final long file = fileBitboard(j);
					if (bitboardsIntersect(file, pawnsOnIthRank)) {
						phalanxCount++;
					}
					else {
						score += PHALANX_BONUSES[phalanxCount];
						phalanxCount = 0;
					}
				}
				score += PHALANX_BONUSES[phalanxCount];
			}
		}
		return score;
	}

	public static int evaluateDoubledPawns(long wpawns, long bpawns)
	{
		int score = 0;

		for (int i = 0; i < 8; i++) {
			final long file = fileBitboard(i);
			final long wfile = wpawns & file, bfile = bpawns & file;
			int wFoundIndex = -1, bFoundIndex = -1;
			for (int j = 0; j < 8; j++) {
				final long rank = rankBitboard(j);
				if (bitboardsIntersect(rank, wfile)) {
					if (wFoundIndex > -1 && j - wFoundIndex < 3) {
						score -= DOUBLED_PENALTY;
					}
					wFoundIndex = j;
				}
				else if (bitboardsIntersect(rank, bfile)) {
					if (bFoundIndex > -1 && j - bFoundIndex < 3) {
						score += DOUBLED_PENALTY;
					}
					bFoundIndex = j;
				}
			}
		}

		return score;
	}

	static long getAdjacentFiles(int fileIndex)
	{
		if (fileIndex == 0) {
			return fileBitboard(1);
		}
		else if (fileIndex == 7) {
			return fileBitboard(6);
		}
		else {
			return fileBitboard(fileIndex + 1) | fileBitboard(fileIndex - 1);
		}
	}

	public static int evaluateBackwardPawns(long wpawns, long bpawns)
	{
		int score = 0;

		for (int i = 0; i < 8; i++) {
			final long file = fileBitboard(i);
			final long adjacentFiles = getAdjacentFiles(i);

			final long wfile = wpawns & file, wadj = wpawns & adjacentFiles;
			if (bitCount(wfile) > 0) {
				for (int j = 1; j < 7; j++) {
					final long rank = rankBitboard(j);
					if (bitboardsIntersect(rank, wadj)) {
						break;
					}
					else if (bitboardsIntersect(rank, wfile)) {
						score -= BACKWARD_PENALTY;
					}
				}
			}
			final long bfile = bpawns & file, badj = bpawns & adjacentFiles;
			if (bitCount(bfile) > 0) {
				for (int j = 6; j > 0; j--) {
					final long rank = rankBitboard(j);
					if (bitboardsIntersect(rank, badj)) {
						break;
					}
					else if (bitboardsIntersect(rank, bfile)) {
						score += BACKWARD_PENALTY;
					}
				}
			}
		}

		return score;
	}

	public static int evaluatePassedPawns(long wpawns, long bpawns)
	{
		int score = 0;

		for (int i = 0; i < 8; i++) {
			final long file = fileBitboard(i);
			final long adjacentFiles = getAdjacentFiles(i) | file;

			final long wfile = wpawns & file;
			if (bitCount(wfile) > 0) {
				long remainingRanksToPromotion = rankBitboard(7);
				for (int j = 6; j > 0; j--) {
					final long rank = rankBitboard(j);
					if (!bitboardsIntersect(rank, wfile)) {
						remainingRanksToPromotion |= rank;
					}
					else {
						break;
					}
				}
				if (!bitboardsIntersect(adjacentFiles & remainingRanksToPromotion, bpawns)) {
					score += PASSED_BONUS;
				}
			}
			final long bfile = bpawns & file;
			if (bitCount(bfile) > 0) {
				long remainingRanksToPromotion = rankBitboard(0);
				for (int j = 1; j < 7; j++) {
					final long rank = rankBitboard(j);
					if (!bitboardsIntersect(rank, bfile)) {
						remainingRanksToPromotion |= rank;
					}
					else {
						break;
					}
				}
				if (!bitboardsIntersect(adjacentFiles & remainingRanksToPromotion, wpawns)) {
					score -= PASSED_BONUS;
				}
			}
		}

		return score;
	}

	public static int evaluateIsolatedPawns(long wpawns, long bpawns)
	{
		/*
		 * wIsolatedRight, for example, marks the files which have no white pawns on the
		 * file to the immediate right
		 */
		int wIsolatedRight = 0b00000001, bIsolatedRight = 0b00000001;
		int wIsolatedLeft = 0b10000000, bIsolatedLeft = 0b10000000;

		for (int i = 0; i < 7; i++) {
			final long fileFromRight = fileBitboard(i), fileFromLeft = fileBitboard(7 - i);
			if (!bitboardsIntersect(fileFromRight, wpawns)) {
				wIsolatedRight |= 1 << (i + 1);
			}
			if (!bitboardsIntersect(fileFromLeft, wpawns)) {
				wIsolatedLeft |= 1 << (6 - i);
			}
			if (!bitboardsIntersect(fileFromRight, bpawns)) {
				bIsolatedRight |= 1 << (i + 1);
			}
			if (!bitboardsIntersect(fileFromLeft, bpawns)) {
				bIsolatedLeft |= 1 << (6 - i);
			}
		}

		final int wIsolated = wIsolatedRight & wIsolatedLeft;
		final int bIsolated = bIsolatedRight & bIsolatedLeft;

		int score = 0;
		for (int i = 0; i < 8; i++) {
			final long file = fileBitboard(i);
			if (bitboardsIntersect(wIsolated, 1L << i)) {
				final boolean semiOpen = bitboardsIntersect(file, bpawns);
				score -= bitCount(wpawns & file) * (ISOLATED_PENALTY + (semiOpen? SEMIOPEN_FILE_BONUS : 0));
			}
			if (bitboardsIntersect(bIsolated, 1L << i)) {
				final boolean semiOpen = bitboardsIntersect(file, wpawns);
				score += bitCount(bpawns & file) * (ISOLATED_PENALTY + (semiOpen? SEMIOPEN_FILE_BONUS : 0));
			}
		}

		return score;
	}
}
