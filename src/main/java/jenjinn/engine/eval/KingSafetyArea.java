/**
 *
 */
package jenjinn.engine.eval;

import static jenjinn.engine.bitboards.Bitboards.emptyBoardAttackset;

import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import xawd.jflow.collections.FlowList;

/**
 * @author ThomasB
 */
public final class KingSafetyArea
{
	private final long outer, inner, kingLoc;

	private KingSafetyArea(BoardSquare src)
	{
		this.kingLoc = src.asBitboard();
		final ChessPiece king = ChessPiece.WHITE_KING;
		this.inner = emptyBoardAttackset(king, src);
		this.outer = BitboardIterator.from(inner)
				.mapToLong(sq -> emptyBoardAttackset(king, sq))
				.reduce(0L, (a, b) -> a | b) & ~(kingLoc | inner);
	}

	public long getOuterArea()
	{
		return outer;
	}

	public long getInnerArea()
	{
		return inner;
	}

	public long getCheckArea()
	{
		return kingLoc;
	}

	private static final FlowList<KingSafetyArea> CACHE =
			BoardSquare.iterateAll().map(KingSafetyArea::new).toList();

	public static KingSafetyArea get(BoardSquare src)
	{
		return CACHE.get(src.ordinal());
	}
}
