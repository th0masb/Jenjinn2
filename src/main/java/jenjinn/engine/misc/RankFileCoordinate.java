package jenjinn.engine.misc;

import static jenjinn.engine.bitboards.Bitboards.fileBitboard;
import static jenjinn.engine.bitboards.Bitboards.rankBitboard;

import jenjinn.engine.enums.BoardSquare;

/**
 * A lightweight representation of a cartesian point representing a square on a
 * chess board. NOTE (0,0) correspond to the square a1.
 * 
 * @author TB
 * @date 22 Jan 2017
 */
public final class RankFileCoordinate
{
	public final int rankIndex;
	public final int fileIndex;

	public RankFileCoordinate(final int rankIndex, final int fileIndex)
	{
		this.rankIndex = rankIndex;
		this.fileIndex = fileIndex;
	}

	public BoardSquare toSq()
	{
		return BoardSquare.fromRankAndFileIndices(rankIndex, fileIndex);
	}

	public long toBitboard()
	{
		return fileBitboard(rankIndex) & rankBitboard(fileIndex);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fileIndex;
		result = prime * result + rankIndex;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RankFileCoordinate other = (RankFileCoordinate) obj;
		if (fileIndex != other.fileIndex)
			return false;
		if (rankIndex != other.rankIndex)
			return false;
		return true;
	}
}
