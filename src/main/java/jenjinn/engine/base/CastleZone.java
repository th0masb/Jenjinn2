/**
 *
 */
package jenjinn.engine.base;

import static java.util.Arrays.asList;
import static jenjinn.engine.base.BoardSquare.A1;
import static jenjinn.engine.base.BoardSquare.A8;
import static jenjinn.engine.base.BoardSquare.C1;
import static jenjinn.engine.base.BoardSquare.C8;
import static jenjinn.engine.base.BoardSquare.D1;
import static jenjinn.engine.base.BoardSquare.D8;
import static jenjinn.engine.base.BoardSquare.E1;
import static jenjinn.engine.base.BoardSquare.E8;
import static jenjinn.engine.base.BoardSquare.F1;
import static jenjinn.engine.base.BoardSquare.F8;
import static jenjinn.engine.base.BoardSquare.G1;
import static jenjinn.engine.base.BoardSquare.G8;
import static jenjinn.engine.base.BoardSquare.H1;
import static jenjinn.engine.base.BoardSquare.H8;

import jenjinn.engine.pgn.CommonRegex;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public enum CastleZone
{
	// Don't change order
	WHITE_KINGSIDE(E1, G1, H1, F1), 
	WHITE_QUEENSIDE(E1, C1, A1, D1), 
	BLACK_KINGSIDE(E8, G8, H8, F8), 
	BLACK_QUEENSIDE(E8, C8, A8, D8);

	private final BoardSquare kingSource, kingTarget, rookSource, rookTarget;

	private CastleZone(BoardSquare kingSource, BoardSquare kingTarget, BoardSquare rookSource,
			BoardSquare rookTarget)
	{
		this.kingSource = kingSource;
		this.kingTarget = kingTarget;
		this.rookSource = rookSource;
		this.rookTarget = rookTarget;
	}

	public BoardSquare getKingSource()
	{
		return kingSource;
	}

	public BoardSquare getKingTarget()
	{
		return kingTarget;
	}

	public BoardSquare getRookSource()
	{
		return rookSource;
	}

	public BoardSquare getRookTarget()
	{
		return rookTarget;
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
			long requiredFreeSquares = kingSource.asBitboard() >>> 1;
			requiredFreeSquares |= requiredFreeSquares >>> 1;
			return requiredFreeSquares;
		} else {
			long requiredFreeSquares = kingSource.asBitboard() << 1;
			requiredFreeSquares |= requiredFreeSquares << 1;
			requiredFreeSquares |= requiredFreeSquares << 1;
			return requiredFreeSquares;
		}
	}

	public long getRequiredUncontrolledSquares()
	{
		if (isKingsideZone()) {
			long requiredFreeSquares = kingSource.asBitboard();
			requiredFreeSquares |= requiredFreeSquares >>> 1;
			requiredFreeSquares |= requiredFreeSquares >>> 1;
			return requiredFreeSquares;
		}
		else {
			long requiredFreeSquares = kingSource.asBitboard();
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
			return iterateAll().filter(z -> z.getSimpleIdentifier().equals(id)).next();
		} else {
			throw new IllegalArgumentException(identifier);
		}
	}

	public static Flow<CastleZone> iterateAll()
	{
		return Iterate.over(asList(values()));
	}
}
