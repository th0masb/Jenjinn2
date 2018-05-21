/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.EnumSet;

import jenjinn.engine.enums.CastleZone;

/**
 * @author ThomasB
 *
 */
public final class CastlingStatus
{
	private final EnumSet<CastleZone> castlingRights;
	private final CastleZone whiteCastlingStatus, blackCastlingStatus;

	public CastlingStatus(final EnumSet<CastleZone> castlingRights, final CastleZone whiteCastlingStatus, final CastleZone blackCastlingStatus)
	{
		this.castlingRights = castlingRights;
		this.whiteCastlingStatus = whiteCastlingStatus;
		this.blackCastlingStatus = blackCastlingStatus;
	}

	public EnumSet<CastleZone> getCastlingRights()
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

	public static CastlingStatus getStartStatus()
	{
		return new CastlingStatus(EnumSet.allOf(CastleZone.class), null, null);
	}
}
