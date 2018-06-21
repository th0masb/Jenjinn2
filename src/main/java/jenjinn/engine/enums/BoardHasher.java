/**
 *
 */
package jenjinn.engine.enums;

import java.util.List;
import java.util.Random;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.boardstate.CastlingStatus;
import jenjinn.engine.boardstate.LocationTracker;
import xawd.jflow.iterators.factories.IterRange;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public enum BoardHasher
{
	INSTANCE(0x110894L);

	private final List<long[]> boardSquareFeatures;
	private final long[] castleRightsFeatures;
	private final long[] enpassantFileFeatures;
	private final long blackToMoveFeature;

	private BoardHasher(long seed)
	{
		if (!seedIsValid(seed)) {
			throw new IllegalArgumentException();
		}
		final Random numberGenerator = new Random(seed);
		boardSquareFeatures = BoardSquare.iterateAll().map(x -> randomArray(12, numberGenerator)).toImmutableList();
		castleRightsFeatures = randomArray(4, numberGenerator);
		enpassantFileFeatures = randomArray(8, numberGenerator);
		blackToMoveFeature = numberGenerator.nextLong();
	}

	private boolean seedIsValid(final long seed)
	{
		final Random r = new Random(seed);
		return IterRange.to(800).mapToObject(i -> r.nextLong()).toSet().size() == 800;
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

	public long hashPieceLocations(final List<LocationTracker> pieceLocations)
	{
		if (pieceLocations.size() != 12) {
			throw new IllegalArgumentException();
		}
		long hash = 0L;
		for (final ChessPiece piece : ChessPieces.all()) {
			hash ^= pieceLocations.get(piece.ordinal()).iterator()
					.mapToLong(loc -> getSquarePieceFeature(loc, piece))
					.reduce(0L, (a, b) -> a ^ b);
		}
		return hash;
	}

	public long hashNonPieceFeatures(final Side activeSide, final BoardSquare enpassantSquare, final CastlingStatus castlingStatus)
	{
		long hash = activeSide.isWhite()? 0L : getBlackToMoveFeature();
		hash ^= enpassantSquare == null? 0L : getEnpassantFileFeature(enpassantSquare);
		hash ^= Iterate.over(castlingStatus.getCastlingRights()).mapToLong(this::getCastleRightsFeature).reduce(0L, (a, b) -> a ^ b);
		return hash;
	}
}