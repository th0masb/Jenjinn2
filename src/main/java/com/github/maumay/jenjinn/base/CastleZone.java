/**
 *
 */
package com.github.maumay.jenjinn.base;

import static com.github.maumay.jenjinn.base.Square.A1;
import static com.github.maumay.jenjinn.base.Square.A8;
import static com.github.maumay.jenjinn.base.Square.C1;
import static com.github.maumay.jenjinn.base.Square.C8;
import static com.github.maumay.jenjinn.base.Square.D1;
import static com.github.maumay.jenjinn.base.Square.D8;
import static com.github.maumay.jenjinn.base.Square.E1;
import static com.github.maumay.jenjinn.base.Square.E8;
import static com.github.maumay.jenjinn.base.Square.F1;
import static com.github.maumay.jenjinn.base.Square.F8;
import static com.github.maumay.jenjinn.base.Square.G1;
import static com.github.maumay.jenjinn.base.Square.G8;
import static com.github.maumay.jenjinn.base.Square.H1;
import static com.github.maumay.jenjinn.base.Square.H8;
import static com.github.maumay.jflow.vec.Vec.vec;

import com.github.maumay.jenjinn.pgn.CommonRegex;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 *
 */
public enum CastleZone
{
	// Don't change order
	WHITE_KINGSIDE(E1, G1, H1, F1), WHITE_QUEENSIDE(E1, C1, A1, D1),
	BLACK_KINGSIDE(E8, G8, H8, F8), BLACK_QUEENSIDE(E8, C8, A8, D8);

	public static final Vec<CastleZone> ALL = vec(values());

	public final Square kingSource, kingTarget, rookSource, rookTarget;

	private CastleZone(Square kingSource, Square kingTarget, Square rookSource,
			Square rookTarget)
	{
		this.kingSource = kingSource;
		this.kingTarget = kingTarget;
		this.rookSource = rookSource;
		this.rookTarget = rookTarget;
	}

	public boolean isWhiteZone()
	{
		return ordinal() < 2;
	}

	public boolean isKingsideZone()
	{
		return ordinal() % 2 == 0;
	}

	/**
	 * @return a bitboard representing the squares which must be clear in order for
	 *         a player to castle in this zone.
	 */
	public long getRequiredFreeSquares()
	{
		if (isKingsideZone()) {
			long requiredFreeSquares = kingSource.bitboard >>> 1;
			requiredFreeSquares |= requiredFreeSquares >>> 1;
			return requiredFreeSquares;
		} else {
			long requiredFreeSquares = kingSource.bitboard << 1;
			requiredFreeSquares |= requiredFreeSquares << 1;
			requiredFreeSquares |= requiredFreeSquares << 1;
			return requiredFreeSquares;
		}
	}

	public long getRequiredUncontrolledSquares()
	{
		if (isKingsideZone()) {
			long requiredFreeSquares = kingSource.bitboard;
			requiredFreeSquares |= requiredFreeSquares >>> 1;
			requiredFreeSquares |= requiredFreeSquares >>> 1;
			return requiredFreeSquares;
		} else {
			long requiredFreeSquares = kingSource.bitboard;
			requiredFreeSquares |= requiredFreeSquares << 1;
			requiredFreeSquares |= requiredFreeSquares << 1;
			return requiredFreeSquares;
		}
	}

	public String getSimpleIdentifier()
	{
		String[] split = name().toLowerCase().split("_");
		return new String(new char[] { split[0].charAt(0), split[1].charAt(0) });
	}

	public static CastleZone fromSimpleIdentifier(String identifier)
	{
		String id = identifier.trim().toLowerCase();
		if (id.matches(CommonRegex.CASTLE_ZONE)) {
			return ALL.findFirst(z -> z.getSimpleIdentifier().equals(id)).get();
		} else {
			throw new IllegalArgumentException(identifier);
		}
	}
}
