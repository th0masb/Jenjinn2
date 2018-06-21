/**
 *
 */
package jenjinn.engine.stringutils;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.misc.PieceLocations;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public final class VisualGridGenerator
{
	private VisualGridGenerator() {
	}

	public static String from(final String title, final Map<BoardSquare, ChessPiece> locations)
	{
		return StringifyBoard.formatGrid(
				new TitledVisualGrid(title, Iterate.over(locations.keySet()).toMap(x -> x, x -> CharPair.from(locations.get(x))))
				);
	}

	public static String from(final DetailedPieceLocations locations)
	{
		final Map<BoardSquare, CharPair> pieceMapping = new HashMap<>();
		ChessPieces.iterate().forEach(piece -> {
			BitboardIterator.from(locations.locationOverviewOf(piece))
			.forEach(square -> pieceMapping.put(square, CharPair.from(piece)));
		});

		final List<TitledVisualGrid> grids = asList(
				new TitledVisualGrid("Pieces", pieceMapping),
				TitledVisualGrid.from("White pieces", locations.getWhiteLocations()),
				TitledVisualGrid.from("Black pieces", locations.getBlackLocations())
				);

		return StringifyBoard.formatGrids(grids);
	}

	public static String from(final String title, final long bitboard)
	{
		return StringifyBoard.formatGrid(TitledVisualGrid.from(title, bitboard));
	}

	public static String from(final long... bitboards)
	{
		return StringifyBoard.formatGrids(Iterate.overLongs(bitboards).mapToObject(TitledVisualGrid::from).toList());
	}

	public static TitledVisualGrid from(final String title, final PieceLocations locations)
	{
		final Map<BoardSquare, CharPair> locs = BitboardIterator.from(locations.getWhite()).toMap(x -> x, i -> new CharPair('X', 'W'));
		locs.putAll(BitboardIterator.from(locations.getBlack()).toMap(x -> x, i -> new CharPair('X', 'B')));
		return new TitledVisualGrid(title, locs);
	}

	public static TitledVisualGrid fromPieceLocations(final PieceLocations locations)
	{
		return from("", locations);
	}
}
