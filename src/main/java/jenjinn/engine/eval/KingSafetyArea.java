/**
 *
 */
package jenjinn.engine.eval;

import static jenjinn.engine.bitboards.Bitboards.emptyBoardAttackset;

import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.pieces.ChessPiece;
import xawd.jflow.collections.FlowList;

/**
 * @author ThomasB
 */
public final class KingSafetyArea
{
	private final long outer, inner, kingLoc, all;

	private KingSafetyArea(BoardSquare src)
	{
		this.kingLoc = src.asBitboard();
		final ChessPiece king = ChessPiece.WHITE_KING;
		this.inner = emptyBoardAttackset(king, src);
		this.outer = BitboardIterator.from(inner)
				.mapToLong(sq -> emptyBoardAttackset(king, sq))
				.reduce(0L, (a, b) -> a | b) & ~(kingLoc | inner);
		this.all = outer | inner | kingLoc;
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

	public long getTotalArea()
	{
		return all;
	}

	private static final FlowList<KingSafetyArea> CACHE =
			BoardSquare.iterateAll().map(KingSafetyArea::new).toList();

	public static KingSafetyArea get(BoardSquare src)
	{
		return CACHE.get(src.ordinal());
	}

//	public static void main(String[] args)
//	{
//		final KingSafetyArea a = get(BoardSquare.G1);
//		System.out.println(VisualGridGenerator.from(a.outer, a.inner, a.kingLoc, a.all));
//	}
}
