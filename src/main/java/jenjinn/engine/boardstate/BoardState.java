/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.EnumSet;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import jenjinn.engine.utils.BoardStateHasher;
import jenjinn.engine.utils.ZobristHasher;

/**
 * @author ThomasB
 */
public final class BoardState
{
	private final ZobristHasher stateHasher = BoardStateHasher.getDefault();
	private final PieceSquareTables midgameTables = null, endGameTables = null;

	private final HalfMoveClock gameClock = new HalfMoveClock();
	private final StateHashCache hashCache = StateHashCache.getGameStartCache();
	private final DetailedPieceLocations pieceLocations;
	private final CastlingStatus castlingStatus;
	private final EnumSet<DevelopmentPiece> developedPieces;

	private Side activeSide;
	private BoardSquare enPassantSquare;
	private int midgamePieceLocationEvaluation, endgamePieceLocationEvaluation;

	public BoardState(
			final Side activeSide,
			final DetailedPieceLocations pieceLocations,
			final BoardSquare enPassantSquare,
			final CastlingStatus castlingStatus,
			final EnumSet<DevelopmentPiece> developedPieces,
			final int midgamePieceLocationEvaluation,
			final int endgamePieceLocationEvaluation)
	{
		this.activeSide = activeSide;
		this.pieceLocations = pieceLocations;
		this.enPassantSquare = enPassantSquare;
		this.castlingStatus = castlingStatus;
		this.developedPieces = developedPieces;
		this.midgamePieceLocationEvaluation = midgamePieceLocationEvaluation;
		this.endgamePieceLocationEvaluation = endgamePieceLocationEvaluation;
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

	public int getMidgamePieceLocationEvaluation()
	{
		return midgamePieceLocationEvaluation;
	}

	public void setMidgamePieceLocationEvaluation(final int midgamePieceLocationEvaluation)
	{
		this.midgamePieceLocationEvaluation = midgamePieceLocationEvaluation;
	}

	public int getEndgamePieceLocationEvaluation()
	{
		return endgamePieceLocationEvaluation;
	}

	public void setEndgamePieceLocationEvaluation(final int endgamePieceLocationEvaluation)
	{
		this.endgamePieceLocationEvaluation = endgamePieceLocationEvaluation;
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

	public PieceSquareTables getMidgameTables()
	{
		return midgameTables;
	}

	public PieceSquareTables getEndgameTables()
	{
		return endGameTables;
	}

	public HalfMoveClock getHalfMoveClock()
	{
		return gameClock;
	}

	public StateHashCache getHashCache()
	{
		return hashCache;
	}

	// Move to own file?
	public static BoardState getStartBoard()
	{
		return new BoardState(
				Side.W,
				DetailedPieceLocations.getStartLocations(),
				null,
				CastlingStatus.getStartStatus(),
				EnumSet.noneOf(DevelopmentPiece.class),
				0,
				0);
	}
}
