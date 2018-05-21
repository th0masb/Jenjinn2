/**
 *
 */
package jenjinn.engine.boardstate;

import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;
import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.take;

/**
 * @author ThomasB
 *
 */
public final class DetailedPieceLocations
{
	private final long[] pieceLocations;
	private long whiteLocations, blackLocations;

	public DetailedPieceLocations(final long[] pieceLocations)
	{
		if (pieceLocations.length != 12) {
			throw new IllegalArgumentException();
		}
		this.pieceLocations = pieceLocations;
		whiteLocations = bitwiseOr(take(6, pieceLocations));
		blackLocations = bitwiseOr(drop(6, pieceLocations));
	}

	public long getWhiteLocations()
	{
		return whiteLocations;
	}

	public void setWhiteLocations(final long whiteLocations)
	{
		this.whiteLocations = whiteLocations;
	}

	public long getBlackLocations()
	{
		return blackLocations;
	}

	public void setBlackLocations(final long blackLocations)
	{
		this.blackLocations = blackLocations;
	}

	public long[] getPieceLocations()
	{
		return pieceLocations;
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
}
