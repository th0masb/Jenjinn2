/**
 *
 */
package jenjinn.engine.stringutils;

import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.BitboardUtils.getSetBitIndices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
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

	public static TitledVisualGrid from(String title, Map<BoardSquare, ChessPiece> locations)
	{
		return new TitledVisualGrid(title, Iterate.over(locations.keySet()).toMap(x -> x, x -> CharPair.from(locations.get(x))));
	}

	public static List<TitledVisualGrid> from(final DetailedPieceLocations locations)
	{
		final Map<BoardSquare, CharPair> pieceMapping = new HashMap<>();
		ChessPieces.iterate().forEach(piece -> {
			Iterate.over(getSetBitIndices(locations.locationsOf(piece)))
			.mapToObject(BoardSquare::of)
			.forEach(square -> {
				pieceMapping.put(square, CharPair.from(piece));
			});
		});

		return asList(
				new TitledVisualGrid("Pieces", pieceMapping),
				from("White pieces", locations.getWhiteLocations()),
				from("Black pieces", locations.getBlackLocations())
				);
	}

	public static TitledVisualGrid from(final String title, final long bitboard)
	{
		return new TitledVisualGrid(
				title,
				Iterate.over(getSetBitIndices(bitboard)).toMap(BoardSquare::of, x -> new CharPair('X', 'X'))
				);
	}

	public static TitledVisualGrid from(final long bitboard)
	{
		return from("", bitboard);
	}

	public static TitledVisualGrid from(final String title, final PieceLocations locations)
	{
		final int[] whiteLocs = getSetBitIndices(locations.getWhite()), blackLocs = getSetBitIndices(locations.getBlack());
		final Map<BoardSquare, CharPair> locs = Iterate.over(whiteLocs).toMap(BoardSquare::of, i -> new CharPair('X', 'W'));
		locs.putAll(Iterate.over(blackLocs).toMap(BoardSquare::of, i -> new CharPair('X', 'B')));

		return new TitledVisualGrid(title, locs);
	}

	public static TitledVisualGrid fromPieceLocations(final PieceLocations locations)
	{
		return from("", locations);
	}
}
