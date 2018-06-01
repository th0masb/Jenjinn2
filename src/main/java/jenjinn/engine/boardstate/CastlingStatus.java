/**
 *
 */
package jenjinn.engine.boardstate;

import static xawd.jflow.utilities.CollectionUtil.str;

import java.util.EnumSet;
import java.util.Set;

import jenjinn.engine.enums.CastleZone;

/**
 * @author ThomasB
 */
public final class CastlingStatus
{
	private final Set<CastleZone> castlingRights;
	private CastleZone whiteCastlingStatus, blackCastlingStatus;

	public CastlingStatus(final Set<CastleZone> castlingRights, final CastleZone whiteCastlingStatus, final CastleZone blackCastlingStatus)
	{
		this.castlingRights = castlingRights;
		this.whiteCastlingStatus = whiteCastlingStatus;
		this.blackCastlingStatus = blackCastlingStatus;
	}

	public Set<CastleZone> getCastlingRights()
	{
		return castlingRights;
	}

	public CastleZone getWhiteCastlingStatus()
	{
		return whiteCastlingStatus;
	}

	public CastleZone getBlackCastlingStatus()
	{
		return blackCastlingStatus;
	}

	public void setCastlingStatus(final CastleZone newStatus)
	{
		if (newStatus.isWhiteZone()) {
			whiteCastlingStatus = newStatus;
		}
		else {
			blackCastlingStatus = newStatus;
		}
	}

	@Override
	public String toString()
	{
		return new StringBuilder("CastlingStatus[Castling rights: ")
				.append(str(castlingRights))
				.append(", White status: ")
				.append(str(whiteCastlingStatus))
				.append(", Black status: ")
				.append(str(blackCastlingStatus))
				.append("]")
				.toString();
	}

	public CastlingStatus copy()
	{
		return new CastlingStatus(EnumSet.copyOf(castlingRights), whiteCastlingStatus, blackCastlingStatus);
	}

	/*
	 * Generated by Eclipse.
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blackCastlingStatus == null) ? 0 : blackCastlingStatus.hashCode());
		result = prime * result + ((castlingRights == null) ? 0 : castlingRights.hashCode());
		result = prime * result + ((whiteCastlingStatus == null) ? 0 : whiteCastlingStatus.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CastlingStatus other = (CastlingStatus) obj;
		if (blackCastlingStatus != other.blackCastlingStatus)
			return false;
		if (castlingRights == null) {
			if (other.castlingRights != null)
				return false;
		} else if (!castlingRights.equals(other.castlingRights))
			return false;
		if (whiteCastlingStatus != other.whiteCastlingStatus)
			return false;
		return true;
	}
}
