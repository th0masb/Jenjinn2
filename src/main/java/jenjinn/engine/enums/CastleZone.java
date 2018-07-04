/**
 *
 */
package jenjinn.engine.enums;

import static java.util.Arrays.asList;
import static jenjinn.engine.enums.BoardSquare.A1;
import static jenjinn.engine.enums.BoardSquare.A8;
import static jenjinn.engine.enums.BoardSquare.C1;
import static jenjinn.engine.enums.BoardSquare.C8;
import static jenjinn.engine.enums.BoardSquare.D1;
import static jenjinn.engine.enums.BoardSquare.D8;
import static jenjinn.engine.enums.BoardSquare.E1;
import static jenjinn.engine.enums.BoardSquare.E8;
import static jenjinn.engine.enums.BoardSquare.F1;
import static jenjinn.engine.enums.BoardSquare.F8;
import static jenjinn.engine.enums.BoardSquare.G1;
import static jenjinn.engine.enums.BoardSquare.G8;
import static jenjinn.engine.enums.BoardSquare.H1;
import static jenjinn.engine.enums.BoardSquare.H8;

import jenjinn.engine.pgn.CommonRegex;
import jenjinn.engine.utils.VisualGridGenerator;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public enum CastleZone
{
	// Don't change order
	WHITE_KINGSIDE(E1, G1, H1, F1), WHITE_QUEENSIDE(E1, C1, A1, D1), BLACK_KINGSIDE(E8, G8, H8, F8), BLACK_QUEENSIDE(E8,
			C8, A8, D8);

	private final BoardSquare kingSource, kingTarget, rookSource, rookTarget;

	private CastleZone(final BoardSquare kingSource, final BoardSquare kingTarget, final BoardSquare rookSource,
			final BoardSquare rookTarget)
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
		final String[] split = name().toLowerCase().split("_");
		return new String(new char[] { split[0].charAt(0), split[1].charAt(0) });
	}

	public static CastleZone fromSimpleIdentifier(final String identifier)
	{
		final String id = identifier.trim().toLowerCase();
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

	public static void main(final String[] args)
	{
		System.out.println(VisualGridGenerator.from(BLACK_QUEENSIDE.getRequiredFreeSquares()));
	}
}
