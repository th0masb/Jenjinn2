package jenjinn.engine.enums;

import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.Bitboards.singleOccupancyBitboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.misc.RankFileCoordinate;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.construction.Iterate;

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
	H1, G1, F1, E1, D1, C1, B1, A1,
	H2, G2, F2, E2, D2, C2, B2, A2,
	H3, G3, F3, E3, D3, C3, B3, A3,
	H4, G4, F4, E4, D4, C4, B4, A4,
	H5, G5, F5, E5, D5, C5, B5, A5,
	H6, G6, F6, E6, D6, C6, B6, A6,
	H7, G7, F7, E7, D7, C7, B7, A7,
	H8, G8, F8, E8, D8, C8, B8, A8;


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

	public List<BoardSquare> getAllSquaresInDirections(final Iterable<Direction> directions, final int maxSquares)
	{
		final List<BoardSquare> allSquares = new ArrayList<>();
		for (final Direction direction : directions)
		{
			BoardSquare nextSq = getNextSquareInDirection(direction);
			int lengthCounter = 0;
			while (nextSq != null && lengthCounter < maxSquares)
			{
				allSquares.add(nextSq);
				nextSq = nextSq.getNextSquareInDirection(direction);
				lengthCounter++;
			}
		}
		return allSquares;
	}

	public List<BoardSquare> getAllSquaresInDirections(final Direction direction, final int maxSquares)
	{
		return getAllSquaresInDirections(asList(direction), maxSquares);
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

	//	public boolean isLightSquare()
	//	{
	//		final RankFileCoordinate asPoint = asRankFileCoord();
	//		final boolean rankStartsWithLightSquare = (asPoint.fileIndex % 2) == 0;
	//		return (asPoint.rankIndex % 2) == (rankStartsWithLightSquare ? 0 : 1);
	//	}

	public static BoardSquare fromIndex(final int index)
	{
		return values()[index];
	}

	public static BoardSquare fromRankAndFileIndices(final int rankIndex, final int fileIndex)
	{
		return values()[fileIndex + 8*rankIndex];
	}

	public static Flow<BoardSquare> iterateAll()
	{
		return Iterate.over(Arrays.asList(values()));
	}
}
