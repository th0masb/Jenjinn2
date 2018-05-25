/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.EnumSet;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.utils.BoardStateHasher;
import jenjinn.engine.utils.ZobristHasher;

/**
 * @author ThomasB
 */
public final class BoardState
{
	private final ZobristHasher stateHasher = BoardStateHasher.getDefault();

	private final HalfMoveClock gameClock = new HalfMoveClock();
	private final StateHashCache hashCache = StateHashCache.getGameStartCache();
	private final DetailedPieceLocations pieceLocations;
	private final CastlingStatus castlingStatus;
	private final EnumSet<DevelopmentPiece> developedPieces;

	private Side activeSide;
	private BoardSquare enPassantSquare;

	public BoardState(
			final Side activeSide,
			final DetailedPieceLocations pieceLocations,
			final BoardSquare enPassantSquare,
			final CastlingStatus castlingStatus,
			final EnumSet<DevelopmentPiece> developedPieces)
	{
		this.activeSide = activeSide;
		this.pieceLocations = pieceLocations;
		this.enPassantSquare = enPassantSquare;
		this.castlingStatus = castlingStatus;
		this.developedPieces = developedPieces;
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
		return enPassantSquare;
	}

	public void setEnPassantSquare(final BoardSquare enPassantSquare)
	{
		this.enPassantSquare = enPassantSquare;
	}

	public EnumSet<DevelopmentPiece> getDevelopedPieces()
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

	public ZobristHasher getStateHasher()
	{
		return stateHasher;
	}

	public HalfMoveClock getHalfMoveClock()
	{
		return gameClock;
	}

	public StateHashCache getHashCache()
	{
		return hashCache;
	}
}
