/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.EnumSet;

import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.stringutils.StringifyBoard;
import jenjinn.engine.stringutils.VisualGridGenerator;

/**
 * @author ThomasB
 *
 */
public final class StartStateGenerator
{
	private StartStateGenerator() {
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
		return new DetailedPieceLocations(startLocs);
	}

	public static BoardState getStartBoard()
	{
		return new BoardState(
				Side.W,
				getStartLocations(),
				null,
				getStartStatus(),
				EnumSet.noneOf(DevelopmentPiece.class));
	}

	public static void main(String[] args)
	{
		//		VisualGridGenerator.from("Test", new HashMap<>()).getGridLines().forEach(System.out::println);

		//		System.out.println(StringifyBoard.formatGrid(VisualGridGenerator.from("Test", new HashMap<>())));

		System.out.println(StringifyBoard.formatGrids(VisualGridGenerator.from(getStartLocations())));
	}
}
