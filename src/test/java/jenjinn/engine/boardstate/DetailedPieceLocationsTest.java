/**
 *
 */
package jenjinn.engine.boardstate;

import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;
import static jenjinn.engine.eval.piecesquaretables.TestingPieceSquareTables.getEndgameTables;
import static jenjinn.engine.eval.piecesquaretables.TestingPieceSquareTables.getMidgameTables;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;

/**
 * @author t
 */
class DetailedPieceLocationsTest
{
	@Test
	void testAddPieceAt()
	{
		final Random squarePicker = new Random(0L);
		final List<BoardSquare> locationsToAddPieceAt = ChessPieces.iterate().map(p -> BoardSquare.of(squarePicker.nextInt(64))).toList();
		final PieceSquareTables midTables = getMidgameTables(), endTables = getEndgameTables();
		final DetailedPieceLocations locations = new DetailedPieceLocations(new long[12], midTables, endTables);

		int runningMidgameEval = 0, runningEndgameEval = 0;
		assertEquals(runningMidgameEval, locations.getMidgameEval());
		assertEquals(runningEndgameEval, locations.getEndgameEval());

		for (final ChessPiece piece : ChessPieces.white()) {
			final BoardSquare loc = locationsToAddPieceAt.get(piece.ordinal());
			locations.addPieceAt(loc, piece);
			runningMidgameEval += midTables.getLocationValue(piece, loc);
			runningEndgameEval += endTables.getLocationValue(piece, loc);
			assertEquals(bitwiseOr(asList(loc)), locations.locationsOf(piece));
			assertEquals(bitwiseOr(take(piece.ordinal() + 1, locationsToAddPieceAt)), locations.getWhiteLocations());
			assertEquals(0L, locations.getBlackLocations());
			assertEquals(runningMidgameEval, locations.getMidgameEval());
			assertEquals(runningEndgameEval, locations.getEndgameEval());
		}

		for (final ChessPiece piece : ChessPieces.black()) {
			final BoardSquare loc = locationsToAddPieceAt.get(piece.ordinal());
			locations.addPieceAt(loc, piece);
			runningMidgameEval += midTables.getLocationValue(piece, loc);
			runningEndgameEval += endTables.getLocationValue(piece, loc);
			assertEquals(bitwiseOr(asList(loc)), locations.locationsOf(piece));
			assertEquals(bitwiseOr(take(6, locationsToAddPieceAt)), locations.getWhiteLocations());
			assertEquals(bitwiseOr(locationsToAddPieceAt.subList(6, piece.ordinal() + 1)), locations.getBlackLocations());
			assertEquals(runningMidgameEval, locations.getMidgameEval());
			assertEquals(runningEndgameEval, locations.getEndgameEval());
		}
	}
}
