/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.EnumSet;

import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import jenjinn.engine.stringutils.VisualGridGenerator;
import jenjinn.engine.utils.DefaultHasher;

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
				PieceSquareTables.endgame(),
				DefaultHasher.get());
	}

	public static BoardState getStartBoard()
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

	public static void main(final String[] args)
	{
		// VisualGridGenerator.from("Test", new
		// HashMap<>()).getGridLines().forEach(System.out::println);

		// System.out.println(StringifyBoard.formatGrid(VisualGridGenerator.from("Test",
		// new HashMap<>())));

		System.out.println(VisualGridGenerator.from(getStartLocations()));
	}
}
