/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.enums;

import static jenjinn.engine.bitboarddatabase.Bitboards.singleOccupancyBitboard;

import java.util.List;

import com.google.common.collect.ImmutableList;

import jenjinn.engine.misc.RankFileCoordinate;

/**
 * Enumeration of the 64 chess squares on a chess board.
 * Ordered the same as the order in which the single
 * occupancy bitboards are generated.
 *
 * @author TB
 * @date 30 Nov 2016
 */
public enum BoardSquare
{
	// DON'T CHANGE ORDER
	h1, g1, f1, e1, d1, c1, b1, a1,
	h2, g2, f2, e2, d2, c2, b2, a2,
	h3, g3, f3, e3, d3, c3, b3, a3,
	h4, g4, f4, e4, d4, c4, b4, a4,
	h5, g5, f5, e5, d5, c5, b5, a5,
	h6, g6, f6, e6, d6, c6, b6, a6,
	h7, g7, f7, e7, d7, c7, b7, a7,
	h8, g8, f8, e8, d8, c8, b8, a8;

	
	public int getNumberOfSquaresLeftInDirection(final Direction direction)
	{
		int ans = 0;
		BoardSquare nextSq = getNextSquareInDirection(direction);
		while (nextSq != null) {
			ans++;
			nextSq = nextSq.getNextSquareInDirection(direction);
		}
		return ans;
	}

	public BoardSquare getNextSquareInDirection(final Direction direction)
	{
		final RankFileCoordinate startSq = this.asRankFileCoord();
		final int newRank = startSq.rankIndex + direction.rankIndexChange;
		final int newFile = startSq.fileIndex + direction.fileIndexChange;
		
		if (0 <= newRank && newRank < 8 && 0 <= newFile && newFile < 8) {
			return fromRankAndFileIndices(newRank, newFile);
		}
		else {
			return null;
		}
	}

	public List<BoardSquare> getAllSquaresInDirection(final Direction direction, final boolean includeStart, final int maxSquares)
	{
		final ImmutableList.Builder<BoardSquare> builder = ImmutableList.builder();
		if (includeStart) {
			builder.add(this);
		}
		BoardSquare nextSq = getNextSquareInDirection(direction);
		int lengthCounter = includeStart ? 1 : 0;
		while (nextSq != null && lengthCounter < maxSquares)
		{
			builder.add(nextSq);
			nextSq = nextSq.getNextSquareInDirection(direction);
			lengthCounter++;
		}
		return builder.build();
	}

	public List<BoardSquare> getAllSquaresInDirection(final Direction direction, final boolean includeStart)
	{
		return getAllSquaresInDirection(direction, includeStart, 10);
	}

	public static BoardSquare fromIndex(final int index)
	{
		return values()[index];
	}

	public static BoardSquare fromRankAndFileIndices(final int rankIndex, final int fileIndex)
	{
		return values()[fileIndex + 8*rankIndex];
	}

	public RankFileCoordinate asRankFileCoord()
	{
		final int index = ordinal();
		return new RankFileCoordinate(index / 8, index % 8);
	}

	public long asBitboard()
	{
		return singleOccupancyBitboard(ordinal());
	}
	
	public boolean isLightSquare()
	{
		final RankFileCoordinate asPoint = asRankFileCoord();
		final boolean rankStartsWithLightSquare = (asPoint.fileIndex % 2) == 0;
		return (asPoint.rankIndex % 2) == (rankStartsWithLightSquare ? 0 : 1);
	}
}
