/**
 *
 */
package jenjinn.engine.utils;

import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.BitboardUtils.getSetBitIndices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.misc.PieceLocations;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author ThomasB
 *
 */
public final class VisualGridGenerator
{
	private VisualGridGenerator() {
	}

	public static List<TitledVisualGrid> fromDetailPieceLocations(final DetailedPieceLocations locations)
	{
		final Map<BoardSquare, CharPair> pieceMapping = new HashMap<>();
		ChessPieces.iterate().forEach(piece -> {
			Iterate.over(getSetBitIndices(locations.getPieceLocations(piece)))
			.mapToObject(BoardSquare::fromIndex)
			.forEach(square -> {
				pieceMapping.put(square, CharPair.from(piece));
			});
		});

		return asList(
				new TitledVisualGrid("Pieces", pieceMapping),
				fromBitboard("White pieces", locations.getWhiteLocations()),
				fromBitboard("Black pieces", locations.getBlackLocations())
				);
	}

	public static TitledVisualGrid fromBitboard(final String title, final long bitboard)
	{
		return new TitledVisualGrid(
				title,
				Iterate.over(getSetBitIndices(bitboard)).toMap(BoardSquare::fromIndex, x -> new CharPair('X', 'X'))
				);
	}

	public static TitledVisualGrid fromBitboard(final long bitboard)
	{
		return fromBitboard("", bitboard);
	}

	public static TitledVisualGrid fromPieceLocations(final String title, final PieceLocations locations)
	{
		final int[] whiteLocs = getSetBitIndices(locations.getWhite()), blackLocs = getSetBitIndices(locations.getBlack());
		final Map<BoardSquare, CharPair> locs = Iterate.over(whiteLocs).toMap(BoardSquare::fromIndex, i -> new CharPair('X', 'W'));
		locs.putAll(Iterate.over(blackLocs).toMap(BoardSquare::fromIndex, i -> new CharPair('X', 'B')));

		return new TitledVisualGrid(title, locs);
	}

	public static TitledVisualGrid fromPieceLocations(final PieceLocations locations)
	{
		return fromPieceLocations("", locations);
	}
}
