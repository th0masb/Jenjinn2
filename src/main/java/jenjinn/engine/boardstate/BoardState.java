/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.EnumSet;
import java.util.Set;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.utils.ZobristHasher;

/**
 * @author ThomasB
 */
public final class BoardState
{
	private final HashCache hashCache;

	private final DetailedPieceLocations pieceLocations;
	private final HalfMoveCounter gameClock;
	private final CastlingStatus castlingStatus;
	private final Set<DevelopmentPiece> developedPieces;

	private Side activeSide;
	private BoardSquare enpassantSquare;

	public BoardState(
			final HashCache hashCache,
			final DetailedPieceLocations pieceLocations,
			final HalfMoveCounter gameClock,
			final CastlingStatus castlingStatus,
			final Set<DevelopmentPiece> developedPieces,
			final Side activeSide,
			final BoardSquare enPassantSquare)
	{
		this.hashCache = hashCache;
		this.pieceLocations = pieceLocations;
		this.gameClock = gameClock;
		this.castlingStatus = castlingStatus;
		this.developedPieces = developedPieces;
		this.activeSide = activeSide;
		this.enpassantSquare = enPassantSquare;
	}

	public Side getActiveSide()
	{
		return activeSide;
	}

	public void switchActiveSide()
	{
		this.activeSide = activeSide.otherSide();
	}

	public BoardSquare getEnPassantSquare()
	{
		return enpassantSquare;
	}

	public void setEnPassantSquare(final BoardSquare enPassantSquare)
	{
		this.enpassantSquare = enPassantSquare;
	}

	public Set<DevelopmentPiece> getDevelopedPieces()
	{
		return developedPieces;
	}

	public DetailedPieceLocations getPieceLocations()
	{
		return pieceLocations;
	}

	public CastlingStatus getCastlingStatus()
	{
		return castlingStatus;
	}

	public HalfMoveCounter getHalfMoveClock()
	{
		return gameClock;
	}

	public HashCache getHashCache()
	{
		return hashCache;
	}

	public long calculateHash()
	{
		final ZobristHasher stateHasher = getPieceLocations().getHashFeatureProvider();
		return getPieceLocations().getSquarePieceFeatureHash()
				^ stateHasher.hashNonPieceFeatures(activeSide, enpassantSquare, castlingStatus);
	}

	public BoardState copy()
	{
		return new BoardState(
				hashCache.copy(), pieceLocations.copy(), gameClock.copy(), castlingStatus.copy(),
				EnumSet.copyOf(developedPieces), activeSide, enpassantSquare);
	}
}
