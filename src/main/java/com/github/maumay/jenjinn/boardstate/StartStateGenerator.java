/**
 *
 */
package com.github.maumay.jenjinn.boardstate;

import java.util.EnumSet;

import com.github.maumay.jenjinn.base.CastleZone;
import com.github.maumay.jenjinn.base.DevelopmentPiece;
import com.github.maumay.jenjinn.base.Side;
import com.github.maumay.jenjinn.eval.piecesquaretables.PieceSquareTables;

/**
 * @author ThomasB
 *
 */
public final class StartStateGenerator
{
	private StartStateGenerator()
	{
	}

	public static CastlingStatus getStartStatus()
	{
		return new CastlingStatus(EnumSet.allOf(CastleZone.class), null, null);
	}

	public static DetailedPieceLocations getStartLocations()
	{
		final long[] startLocs = new long[] {
				0b11111111L << 8,
				0b01000010L,
				0b00100100L,
				0b10000001L,
				0b00010000L,
				0b00001000L,

				0b11111111L << 48,
				0b01000010L << 56,
				0b00100100L << 56,
				0b10000001L << 56,
				0b00010000L << 56,
				0b00001000L << 56,
				};

		return new DetailedPieceLocations(startLocs,
				PieceSquareTables.midgame(),
				PieceSquareTables.endgame());
	}

	public static BoardState createStartBoard()
	{
		return new BoardState(
				new HashCache(),
				getStartLocations(),
				new HalfMoveCounter(),
				getStartStatus(),
				EnumSet.noneOf(DevelopmentPiece.class),
				Side.WHITE,
				null);
	}
}
