/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.Set;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.DevelopmentPiece;

/**
 * @author ThomasB
 *
 */
public class DataForReversingMove {

	private Set<CastleZone> discardedCastlingRights;
	private ChessPiece pieceTaken;
	private DevelopmentPiece pieceDeveloped;
	private BoardSquare discardedEnpassantSquare;
	private long discardedHash;
	private int discardedMidgameScore, discardedEndgameScore, discardedHalfMoveClock;

	public DataForReversingMove() {
	}

	public Set<CastleZone> getDiscardedCastlingRights()
	{
		return discardedCastlingRights;
	}

	public void setDiscardedCastlingRights(final Set<CastleZone> discardedCastlingRights)
	{
		this.discardedCastlingRights = discardedCastlingRights;
	}

	public ChessPiece getPieceTaken()
	{
		return pieceTaken;
	}

	public void setPieceTaken(final ChessPiece pieceTaken)
	{
		this.pieceTaken = pieceTaken;
	}

	public DevelopmentPiece getPieceDeveloped()
	{
		return pieceDeveloped;
	}

	public void setPieceDeveloped(final DevelopmentPiece pieceDeveloped)
	{
		this.pieceDeveloped = pieceDeveloped;
	}

	public BoardSquare getDiscardedEnpassantSquare()
	{
		return discardedEnpassantSquare;
	}

	public void setDiscardedEnpassantSquare(final BoardSquare discardedEnpassantSquare)
	{
		this.discardedEnpassantSquare = discardedEnpassantSquare;
	}

	public long getDiscardedHash()
	{
		return discardedHash;
	}

	public void setDiscardedHash(final long discardedHash)
	{
		this.discardedHash = discardedHash;
	}

	public int getDiscardedMidgameScore()
	{
		return discardedMidgameScore;
	}

	public void setDiscardedMidgameScore(final int discardedMidgameScore)
	{
		this.discardedMidgameScore = discardedMidgameScore;
	}

	public int getDiscardedEndgameScore()
	{
		return discardedEndgameScore;
	}

	public void setDiscardedEndgameScore(final int discardedEndgameScore)
	{
		this.discardedEndgameScore = discardedEndgameScore;
	}

	public int getDiscardedHalfMoveClockValue()
	{
		return discardedHalfMoveClock;
	}

	public void setDiscardedHalfMoveClock(final int discardedHalfMoveClock)
	{
		this.discardedHalfMoveClock = discardedHalfMoveClock;
	}
}
