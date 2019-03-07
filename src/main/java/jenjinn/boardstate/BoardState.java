/**
 *
 */
package jenjinn.boardstate;

import java.util.EnumSet;
import java.util.Set;

import jenjinn.base.DevelopmentPiece;
import jenjinn.base.Side;
import jenjinn.base.Square;
import jenjinn.utils.BoardHasher;

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
	private Square enpassantSquare;

	public BoardState(
			HashCache hashCache,
			DetailedPieceLocations pieceLocations,
			HalfMoveCounter gameClock,
			CastlingStatus castlingStatus,
			Set<DevelopmentPiece> developedPieces,
			Side activeSide,
			Square enPassantSquare)
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

	public Square getEnPassantSquare()
	{
		return enpassantSquare;
	}

	public void setEnPassantSquare(Square enPassantSquare)
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

	public boolean hasEnpassantAvailable()
	{
		return enpassantSquare != null;
	}

	public long calculateHash()
	{
		return getPieceLocations().getSquarePieceFeatureHash()
				^ BoardHasher.INSTANCE.hashNonPieceFeatures(activeSide, enpassantSquare, castlingStatus);
	}

	public BoardState copy()
	{
		return new BoardState(
				hashCache.copy(), pieceLocations.copy(), gameClock.copy(), castlingStatus.copy(),
				EnumSet.copyOf(developedPieces), activeSide, enpassantSquare);
	}
}
