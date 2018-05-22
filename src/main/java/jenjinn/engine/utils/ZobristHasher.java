/**
 *
 */
package jenjinn.engine.utils;

import java.util.List;
import java.util.Random;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.ChessPiece;
import xawd.jflow.iterators.construction.IterRange;

/**
 * @author ThomasB
 *
 */
public final class ZobristHasher
{
	private final List<long[]> boardSquareFeatures;
	private final long[] castleRightsFeatures;
	private final long[] enpassantFileFeatures;
	private final long blackToMoveFeature;

	public ZobristHasher(final Random numberGenerator)
	{
		boardSquareFeatures = BoardSquare.iterateAll().map(x -> randomArray(12, numberGenerator)).toImmutableList();
		castleRightsFeatures = randomArray(4, numberGenerator);
		enpassantFileFeatures = randomArray(8, numberGenerator);
		blackToMoveFeature = numberGenerator.nextLong();
	}

	private long[] randomArray(final int length, final Random numberGenerator)
	{
		return IterRange.to(length).mapToLong(i -> numberGenerator.nextLong()).toArray();
	}

	public long getSquarePieceFeature(final BoardSquare square, final ChessPiece piece)
	{
		return boardSquareFeatures.get(square.ordinal())[piece.ordinal()];
	}

	public long getCastleRightsFeature(final CastleZone zone)
	{
		return castleRightsFeatures[zone.ordinal()];
	}

	public long getEnpassantFileFeature(final BoardSquare enPassantSquare)
	{
		return enpassantFileFeatures[enPassantSquare.ordinal() % 8];
	}

	public long getBlackToMoveFeature()
	{
		return blackToMoveFeature;
	}
}
