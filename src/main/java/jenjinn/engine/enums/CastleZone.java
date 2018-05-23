/**
 *
 */
package jenjinn.engine.enums;

import static jenjinn.engine.enums.BoardSquare.A1;
import static jenjinn.engine.enums.BoardSquare.A8;
import static jenjinn.engine.enums.BoardSquare.B1;
import static jenjinn.engine.enums.BoardSquare.B8;
import static jenjinn.engine.enums.BoardSquare.C1;
import static jenjinn.engine.enums.BoardSquare.C8;
import static jenjinn.engine.enums.BoardSquare.E1;
import static jenjinn.engine.enums.BoardSquare.E8;
import static jenjinn.engine.enums.BoardSquare.F1;
import static jenjinn.engine.enums.BoardSquare.F8;
import static jenjinn.engine.enums.BoardSquare.G1;
import static jenjinn.engine.enums.BoardSquare.G8;
import static jenjinn.engine.enums.BoardSquare.H1;
import static jenjinn.engine.enums.BoardSquare.H8;

/**
 * @author ThomasB
 *
 */
public enum CastleZone
{
	WHITE_KINGSIDE(E1, G1, H1, F1),
	WHITE_QUEENSIDE(E1, B1, A1, C1),
	BLACK_KINGSIDE(E8, G8, H8, F8),
	BLACK_QUEENSIDE(E8, B8, A8, C8);

	private final BoardSquare kingSource, kingTarget, rookSource, rookTarget;

	private CastleZone(final BoardSquare kingSource, final BoardSquare kingTarget, final BoardSquare rookSource, final BoardSquare rookTarget)
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
}
