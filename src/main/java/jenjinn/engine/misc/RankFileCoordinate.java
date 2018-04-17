package jenjinn.engine.misc;

import static jenjinn.engine.bitboarddatabase.Bitboards.fileBitboard;
import static jenjinn.engine.bitboarddatabase.Bitboards.rankBitboard;

import jenjinn.engine.enums.BoardSquare;

/**
 * A lightweight representation of a cartesian point representing a square on a
 * chess board. NOTE (0,0) correspond to the square a1.
 * 
 * @author TB
 * @date 22 Jan 2017
 */
public class RankFileCoordinate
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
}
