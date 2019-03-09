package com.github.maumay.jenjinn.base;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Optional;

import com.github.maumay.jflow.vec.Vec;

/**
 * Enum representing all the different directions the various chesspieces can
 * move on the board.
 *
 * @author t
 */
public enum Dir
{
	N(1, 0), E(0, -1), S(-1, 0), W(0, 1), NE(1, -1), SE(-1, -1), SW(-1, 1), NW(1, 1),
	NNE(2, -1), NEE(1, -2), SEE(-1, -2), SSE(-2, -1), SSW(-2, 1), SWW(-1, 2), NWW(1, 2),
	NNW(2, 1);

	public static final Vec<Dir> ALL = Vec.of(values());

	public final int rankIndexChange;
	public final int fileIndexChange;

	private Dir(int rankIndexChange, int fileIndexChange)
	{
		this.rankIndexChange = rankIndexChange;
		this.fileIndexChange = fileIndexChange;
	}

	public static Optional<Dir> ofLineBetween(Square start, Square end)
	{
		if (start == end) {
			return Optional.empty();
		}

		int rankChange = end.rank - start.rank, fileChange = end.file - start.file;
		int maxAbsChange = max(abs(rankChange), abs(fileChange)),
				minAbsChange = min(abs(rankChange), abs(fileChange));
		int normaliser = minAbsChange == 0 ? maxAbsChange : minAbsChange;

		if (maxAbsChange % normaliser != 0 || minAbsChange % normaliser != 0) {
			return Optional.empty();
		} else {
			int rankIndexChange = rankChange / normaliser,
					fileIndexChange = fileChange / normaliser;
			return ALL.findFirst(dir -> dir.rankIndexChange == rankIndexChange
					&& dir.fileIndexChange == fileIndexChange);
		}
	}
}
