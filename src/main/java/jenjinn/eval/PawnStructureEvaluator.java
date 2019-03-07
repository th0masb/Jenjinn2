/**
 *
 */
package jenjinn.eval;

import static java.lang.Long.bitCount;
import static jenjinn.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.bitboards.Bitboards.fileBitboard;
import static jenjinn.bitboards.Bitboards.rankBitboard;

import jenjinn.bitboards.BitboardIterator;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.DetailedPieceLocations;
import jenjinn.eval.PawnTable.Entry;
import jenjinn.pieces.Piece;
import jenjinn.utils.BoardHasher;

/**
 * @author ThomasB
 *
 */
public final class PawnStructureEvaluator implements EvaluationComponent
{
	public static final int SEMIOPEN_FILE_BONUS = 10;

	public static final int CHAIN_BONUS = 10;
	public static final int PASSED_BONUS = 150;
	public static final int[] PHALANX_BONUSES = {0, 0, 70, 50, 50, 0, 0, 0, 0};

	public static final int DOUBLED_PENALTY = 55;
	public static final int ISOLATED_PENALTY = 55;
	public static final int BACKWARD_PENALTY = 20;

	private final PawnTable cachedEvaluations;

	public PawnStructureEvaluator(int tableSize)
	{
		cachedEvaluations = new PawnTable(tableSize);
	}

	@Override
	public int evaluate(BoardState state)
	{
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		long wpawns = pieceLocs.locationsOf(Piece.WHITE_PAWN);
		long bpawns = pieceLocs.locationsOf(Piece.BLACK_PAWN);
		long pawnHash = calculatePawnPositionHash(wpawns, bpawns);

		PawnTable.Entry cached = cachedEvaluations.get(pawnHash);
		if (cached == null) {
			Entry newEntry = new PawnTable.Entry(pawnHash, calculateOverallScore(wpawns, bpawns));
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
		Piece wp = Piece.WHITE_PAWN, bp = Piece.BLACK_PAWN;

		long whash = BitboardIterator.from(wpawns)
				.mapToLong(sq -> BoardHasher.INSTANCE.getSquarePieceFeature(sq, wp))
				.fold(0L, (a, b) -> a ^ b);

		long bhash = BitboardIterator.from(bpawns)
				.mapToLong(sq -> BoardHasher.INSTANCE.getSquarePieceFeature(sq, bp))
				.fold(0L, (a, b) -> a ^ b);

		return whash ^ bhash;
	}

	public static int evaluatePawnChains(long wpawns, long bpawns)
	{
		long hfile = fileBitboard(0), afile = fileBitboard(7);

		long wpawnLeft = (wpawns & ~afile) << 9, wpawnRight = (wpawns & ~hfile) << 7;
		long bpawnLeft = (bpawns & ~afile) >>> 7, bpawnRight = (bpawns & ~hfile) >>> 9;

		return CHAIN_BONUS *(bitCount(wpawnLeft & wpawns)
				+ bitCount(wpawnRight & wpawns)
				- bitCount(bpawnLeft & bpawns)
				- bitCount(bpawnRight & bpawns));
	}

	public static int evaluatePhalanxFormations(long pawns)
	{
		int score = 0;
		for (int i = 0; i < 8; i++) {
			long pawnsOnIthRank = rankBitboard(i) & pawns;
			if (bitCount(pawnsOnIthRank) > 1) {
				int phalanxCount = 0;
				for (int j = 0; j < 8; j++) {
					long file = fileBitboard(j);
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
			long file = fileBitboard(i);
			long wfile = wpawns & file, bfile = bpawns & file;
			int wFoundIndex = -1, bFoundIndex = -1;
			for (int j = 0; j < 8; j++) {
				long rank = rankBitboard(j);
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
			long file = fileBitboard(i);
			long adjacentFiles = getAdjacentFiles(i);

			long wfile = wpawns & file, wadj = wpawns & adjacentFiles;
			if (bitCount(wfile) > 0) {
				for (int j = 1; j < 7; j++) {
					long rank = rankBitboard(j);
					if (bitboardsIntersect(rank, wadj)) {
						break;
					}
					else if (bitboardsIntersect(rank, wfile)) {
						score -= BACKWARD_PENALTY;
					}
				}
			}
			long bfile = bpawns & file, badj = bpawns & adjacentFiles;
			if (bitCount(bfile) > 0) {
				for (int j = 6; j > 0; j--) {
					long rank = rankBitboard(j);
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
			long file = fileBitboard(i);
			long adjacentFiles = getAdjacentFiles(i) | file;

			long wfile = wpawns & file, badj = bpawns & adjacentFiles;
			int wFileCount = bitCount(wfile), bAdjCount = bitCount(badj);
			if (wFileCount > 0) {
				if (bAdjCount == 0) {
					score += wFileCount * PASSED_BONUS;
				}
				else {
					for (int rankIndex = 7; rankIndex > 1; rankIndex--) {
						long workingRank = rankBitboard(rankIndex);
						if (bitboardsIntersect(workingRank, wfile)) {
							score += PASSED_BONUS;
						}
						if (bitboardsIntersect(workingRank, badj)) {
							break;
						}
					}
				}
			}
			long bfile = bpawns & file, wadj = wpawns & adjacentFiles;
			int bFileCount = bitCount(bfile), wAdjCount = bitCount(wadj);
			if (bFileCount > 0) {
				if (wAdjCount == 0) {
					score -= bFileCount * PASSED_BONUS;
				}
				else {
					for (int rankIndex = 1; rankIndex < 7; rankIndex++) {
						long workingRank = rankBitboard(rankIndex);
						if (bitboardsIntersect(workingRank, bfile)) {
							score -= PASSED_BONUS;
						}
						if (bitboardsIntersect(workingRank, wadj)) {
							break;
						}
					}
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
			long fileFromRight = fileBitboard(i), fileFromLeft = fileBitboard(7 - i);
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

		int wIsolated = wIsolatedRight & wIsolatedLeft;
		int bIsolated = bIsolatedRight & bIsolatedLeft;

		int score = 0;
		for (int i = 0; i < 8; i++) {
			long file = fileBitboard(i);
			if (bitboardsIntersect(wIsolated, 1L << i)) {
				boolean semiOpen = !bitboardsIntersect(file, bpawns);
				score -= bitCount(wpawns & file) * (ISOLATED_PENALTY + (semiOpen? SEMIOPEN_FILE_BONUS : 0));
			}
			if (bitboardsIntersect(bIsolated, 1L << i)) {
				boolean semiOpen = !bitboardsIntersect(file, wpawns);
				score += bitCount(bpawns & file) * (ISOLATED_PENALTY + (semiOpen? SEMIOPEN_FILE_BONUS : 0));
			}
		}

		return score;
	}
}
