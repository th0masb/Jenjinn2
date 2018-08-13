/**
 *
 */
package jenjinn.engine.moves;

import static java.util.Arrays.asList;

import java.util.List;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.CastleZone;
import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 */
public final class MoveCache {

	private MoveCache() {}

	private static final List<CastleMove> CASTLE_MOVE_CACHE = CastleZone.iterateAll().map(CastleMove::new).toList();
	private static final List<StandardMove[]> STANDARD_MOVE_CACHE = createStandardMoveCache();

	static List<StandardMove[]> createStandardMoveCache()
	{
		List<StandardMove[]> moveCache = BoardSquare.iterateAll().map(i -> new StandardMove[64]).toList();

		for (ChessPiece piece : asList(ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_QUEEN)) {
			BoardSquare.iterateAll().forEach(square ->
			{
				BitboardIterator.from(piece.getSquaresOfControl(square, 0L, 0L))
				.forEach(loc -> moveCache.get(square.ordinal())[loc.ordinal()] = new StandardMove(square, loc));
			});
		}
		return moveCache;
	}

	public static StandardMove getMove(BoardSquare source, BoardSquare target)
	{
		assert STANDARD_MOVE_CACHE.get(source.ordinal())[target.ordinal()] != null : "Requested impossible move or my logic is wrong.";
		return STANDARD_MOVE_CACHE.get(source.ordinal())[target.ordinal()];
	}

	public static CastleMove getMove(CastleZone zone)
	{
		return CASTLE_MOVE_CACHE.get(zone.ordinal());
	}
}
