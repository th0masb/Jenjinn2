/**
 *
 */
package jenjinn.engine.boardstate;

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
	private final ZobristHasher stateHasher;// = BoardStateHasher.getDefault();

	private final StateHashCache hashCache;// = StateHashCache.getGameStartCache();
	private final DetailedPieceLocations pieceLocations;
	private final HalfMoveClock gameClock;// = new HalfMoveClock(0);
	private final CastlingStatus castlingStatus;
	private final Set<DevelopmentPiece> developedPieces;

	private Side activeSide;
	private BoardSquare enPassantSquare;

	public BoardState(
			ZobristHasher stateHasher,
			StateHashCache hashCache,
			DetailedPieceLocations pieceLocations,
			HalfMoveClock gameClock,
			CastlingStatus castlingStatus,
			Set<DevelopmentPiece> developedPieces,
			Side activeSide,
			BoardSquare enPassantSquare)
	{
		this.stateHasher = stateHasher;
		this.hashCache = hashCache;
		this.pieceLocations = pieceLocations;
		this.gameClock = gameClock;
		this.castlingStatus = castlingStatus;
		this.developedPieces = developedPieces;
		this.activeSide = activeSide;
		this.enPassantSquare = enPassantSquare;
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
