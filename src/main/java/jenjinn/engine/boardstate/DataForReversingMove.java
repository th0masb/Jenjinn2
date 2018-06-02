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

	/**
	 * Populating instance in forward direction sets consumed to false. Using
	 * instance to undo a move sets consumed to true.
	 *
	 * Can only be populated in forward direction if consumed. Can only be used to
	 * undo a move if not consumed.
	 */
	private boolean consumed = true;

	private Set<CastleZone> discardedCastlingRights;
	private ChessPiece pieceTaken;
	private DevelopmentPiece pieceDeveloped;
	private BoardSquare discardedEnpassantSquare;
	private long discardedHash;
	private int discardedHalfMoveClock;

	public DataForReversingMove() {
	}

	public boolean isConsumed()
	{
		return consumed;
	}

	public void setConsumed(final boolean consumed)
	{
		this.consumed = consumed;
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

	public int getDiscardedHalfMoveClockValue()
	{
		return discardedHalfMoveClock;
	}

	public void setDiscardedHalfMoveClock(final int discardedHalfMoveClock)
	{
		this.discardedHalfMoveClock = discardedHalfMoveClock;
	}
}
