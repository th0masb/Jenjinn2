/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.EnumSet;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 *
 */
public final class BoardStateImpl
{
	private int halfMoveClock = 0;

	// Side to move
	private Side activeSide;

	// Piece locations
	private final DetailedPieceLocations pieceLocations;

	// Enpassant
	private BoardSquare enPassantSquare;

	// Castling
	private final CastlingStatus castlingStatus;

	// Developmental Status
	private final EnumSet<DevelopmentPiece> developedPieces;

	// Board identity hashing
	private final long[] recentHashes;

	// Evaluation status
	private int midgamePieceLocationEvaluation, endgamePieceLocationEvaluation;

	public BoardStateImpl(
			final int halfMoveClock,
			final Side activeSide,
			final DetailedPieceLocations pieceLocations,
			final BoardSquare enPassantSquare,
			final CastlingStatus castlingStatus,
			final EnumSet<DevelopmentPiece> developedPieces,
			final long[] recentHashes,
			final int midgamePieceLocationEvaluation,
			final int endgamePieceLocationEvaluation)
	{
		this.halfMoveClock = halfMoveClock;
		this.activeSide = activeSide;
		this.pieceLocations = pieceLocations;
		this.enPassantSquare = enPassantSquare;
		this.castlingStatus = castlingStatus;
		this.developedPieces = developedPieces;
		this.recentHashes = recentHashes;
		this.midgamePieceLocationEvaluation = midgamePieceLocationEvaluation;
		this.endgamePieceLocationEvaluation = endgamePieceLocationEvaluation;
	}

	public Side getActiveSide()
	{
		return activeSide;
	}

	public void setActiveSide(final Side activeSide)
	{
		this.activeSide = activeSide;
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

	public long[] getRecentHashes()
	{
		return recentHashes;
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

	public int getHalfMoveClock()
	{
		return halfMoveClock;
	}

	public void setHalfMoveClock(final int halfMoveClock)
	{
		this.halfMoveClock = halfMoveClock;
	}

	public DetailedPieceLocations getPieceLocations()
	{
		return pieceLocations;
	}

	public CastlingStatus getCastlingStatus()
	{
		return castlingStatus;
	}

	public static BoardStateImpl getStartBoard()
	{
		return new BoardStateImpl(
				0,
				Side.W,
				DetailedPieceLocations.getStartLocations(),
				null,
				CastlingStatus.getStartStatus(),
				EnumSet.noneOf(DevelopmentPiece.class),
				new long[4],
				0,
				0);
	}
}
