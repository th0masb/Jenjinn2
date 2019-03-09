/**
 *
 */
package jenjinn.moves;

import static java.util.Arrays.asList;

import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.CastleZone;
import jenjinn.base.Square;
import jenjinn.bitboards.BitboardIterator;
import jenjinn.pieces.Piece;

/**
 * @author ThomasB
 */
public final class MoveCache
{

	private MoveCache()
	{
	}

	private static final Vec<CastleMove> CASTLE_MOVE_CACHE = CastleZone.ALL
			.map(CastleMove::new);
	private static final Vec<StandardMove[]> STANDARD_MOVE_CACHE = createStandardMoveCache();

	static Vec<StandardMove[]> createStandardMoveCache()
	{
		Vec<StandardMove[]> moveCache = Square.ALL.map(i -> new StandardMove[64]);

		for (Piece piece : asList(Piece.WHITE_KNIGHT, Piece.WHITE_QUEEN)) {
			Square.ALL.forEach(square -> {
				BitboardIterator.from(piece.getSquaresOfControl(square, 0L, 0L))
						.forEach(loc -> moveCache.get(square.ordinal())[loc
								.ordinal()] = new StandardMove(square, loc));
			});
		}
		return moveCache;
	}

	public static StandardMove getMove(Square source, Square target)
	{
		assert STANDARD_MOVE_CACHE.get(source.ordinal())[target
				.ordinal()] != null : "Requested impossible move or my logic is wrong.";
		return STANDARD_MOVE_CACHE.get(source.ordinal())[target.ordinal()];
	}

	public static CastleMove getMove(CastleZone zone)
	{
		return CASTLE_MOVE_CACHE.get(zone.ordinal());
	}
}
