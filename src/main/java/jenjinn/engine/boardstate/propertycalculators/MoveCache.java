/**
 *
 */
package jenjinn.engine.boardstate.propertycalculators;

import static java.util.Arrays.asList;

import java.util.List;

import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.moves.CastleMove;
import jenjinn.engine.moves.StandardMove;

/**
 * @author ThomasB
 */
public final class MoveCache {

	private MoveCache() {}

	private static final List<CastleMove> CASTLE_MOVE_CACHE = CastleZone.iterateAll().map(CastleMove::new).toImmutableList();
	private static final List<StandardMove[]> STANDARD_MOVE_CACHE = createStandardMoveCache();

	static List<StandardMove[]> createStandardMoveCache()
	{
		final List<StandardMove[]> moveCache = BoardSquare.iterateAll().map(i -> new StandardMove[64]).toImmutableList();

		for (final ChessPiece piece : asList(ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_QUEEN)) {
			BoardSquare.iterateAll().forEach(square ->
			{
				BitboardIterator.from(piece.getSquaresOfControl(square, 0L, 0L))
				.forEach(loc -> moveCache.get(square.ordinal())[loc.ordinal()] = new StandardMove(square, loc));
			});
		}
		return moveCache;
	}

	public static StandardMove getMove(final BoardSquare source, final BoardSquare target)
	{
		assert STANDARD_MOVE_CACHE.get(source.ordinal())[target.ordinal()] != null : "Requested impossible move or my logic is wrong.";
		return STANDARD_MOVE_CACHE.get(source.ordinal())[target.ordinal()];
	}

	public static CastleMove getMove(final CastleZone zone)
	{
		return CASTLE_MOVE_CACHE.get(zone.ordinal());
	}
}
