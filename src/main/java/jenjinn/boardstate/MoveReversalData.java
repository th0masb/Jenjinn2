/**
 *
 */
package jenjinn.boardstate;

import java.util.EnumSet;
import java.util.Set;

import jenjinn.base.CastleZone;
import jenjinn.base.DevelopmentPiece;
import jenjinn.base.Square;
import jenjinn.pieces.Piece;

/**
 * @author ThomasB
 *
 */
public class MoveReversalData {

	/**
	 * Populating instance in forward direction sets consumed to false. Using
	 * instance to undo a move sets consumed to true.
	 *
	 * Can only be populated in forward direction if consumed. Can only be used to
	 * undo a move if not consumed.
	 */
	private boolean consumed = true;

	private Set<CastleZone> discardedCastlingRights = EnumSet.noneOf(CastleZone.class);
	private Piece pieceTaken;
	private DevelopmentPiece pieceDeveloped;
	private Square discardedEnpassantSquare;
	private long discardedHash;
	private int discardedHalfMoveClock;

	public MoveReversalData() {
	}

	public void reset()
	{
		consumed = true;
		discardedCastlingRights = EnumSet.noneOf(CastleZone.class);
		pieceTaken = null;
		pieceDeveloped = null;
		discardedEnpassantSquare = null;
		discardedHash = 0L;
		discardedHalfMoveClock = -1;
	}

	public boolean isConsumed()
	{
		return consumed;
	}

	public void setConsumed(boolean consumed)
	{
		this.consumed = consumed;
	}

	public Set<CastleZone> getDiscardedCastlingRights()
	{
		return discardedCastlingRights;
	}

	public void setDiscardedCastlingRights(Set<CastleZone> discardedCastlingRights)
	{
		this.discardedCastlingRights = discardedCastlingRights;
	}

	public Piece getPieceTaken()
	{
		return pieceTaken;
	}

	public void setPieceTaken(Piece pieceTaken)
	{
		this.pieceTaken = pieceTaken;
	}

	public DevelopmentPiece getPieceDeveloped()
	{
		return pieceDeveloped;
	}

	public void setPieceDeveloped(DevelopmentPiece pieceDeveloped)
	{
		this.pieceDeveloped = pieceDeveloped;
	}

	public Square getDiscardedEnpassantSquare()
	{
		return discardedEnpassantSquare;
	}

	public void setDiscardedEnpassantSquare(Square discardedEnpassantSquare)
	{
		this.discardedEnpassantSquare = discardedEnpassantSquare;
	}

	public long getDiscardedHash()
	{
		return discardedHash;
	}

	public void setDiscardedHash(long discardedHash)
	{
		this.discardedHash = discardedHash;
	}

	public int getDiscardedHalfMoveClockValue()
	{
		return discardedHalfMoveClock;
	}

	public void setDiscardedHalfMoveClock(int discardedHalfMoveClock)
	{
		this.discardedHalfMoveClock = discardedHalfMoveClock;
	}
}
