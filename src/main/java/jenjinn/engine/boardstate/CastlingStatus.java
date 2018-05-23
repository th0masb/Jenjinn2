/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.EnumSet;
import java.util.Set;

import jenjinn.engine.enums.CastleZone;

/**
 * @author ThomasB
 *
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

	public void setCastlingStatus(CastleZone newStatus)
	{
		if (newStatus.isWhiteZone()) {
			whiteCastlingStatus = newStatus;
		}
		else {
			blackCastlingStatus = newStatus;
		}
	}

	public static CastlingStatus getStartStatus()
	{
		return new CastlingStatus(EnumSet.allOf(CastleZone.class), null, null);
	}
}
