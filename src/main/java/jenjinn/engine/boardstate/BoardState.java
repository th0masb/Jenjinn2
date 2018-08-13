/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.EnumSet;
import java.util.Set;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.DevelopmentPiece;
import jenjinn.engine.base.Side;
import jenjinn.engine.utils.BoardHasher;

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
			HashCache hashCache,
			DetailedPieceLocations pieceLocations,
			HalfMoveCounter gameClock,
			CastlingStatus castlingStatus,
			Set<DevelopmentPiece> developedPieces,
			Side activeSide,
			BoardSquare enPassantSquare)
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

	public void setEnPassantSquare(BoardSquare enPassantSquare)
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
