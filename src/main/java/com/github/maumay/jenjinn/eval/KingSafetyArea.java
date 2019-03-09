/**
 *
 */
package com.github.maumay.jenjinn.eval;

import static com.github.maumay.jenjinn.bitboards.Bitboards.emptyBoardAttackset;

import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.bitboards.BitboardIterator;
import com.github.maumay.jenjinn.pieces.Piece;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 */
public final class KingSafetyArea
{
	private final long outer, inner, all;

	private KingSafetyArea(Square src)
	{
		final Piece king = Piece.WHITE_KING;
		this.inner = emptyBoardAttackset(king, src);
		this.outer = BitboardIterator.from(inner)
				.mapToLong(sq -> emptyBoardAttackset(king, sq)).fold(0L, (a, b) -> a | b)
				& ~(src.bitboard | inner);
		this.all = outer | inner;
	}

	public long getOuterArea()
	{
		return outer;
	}

	public long getInnerArea()
	{
		return inner;
	}

	public long getTotalArea()
	{
		return all;
	}

	private static final Vec<KingSafetyArea> CACHE = Square.ALL.map(KingSafetyArea::new);

	public static KingSafetyArea get(Square src)
	{
		return CACHE.get(src.ordinal());
	}
}
