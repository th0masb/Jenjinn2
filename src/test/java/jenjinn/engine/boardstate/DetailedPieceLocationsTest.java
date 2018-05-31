/**
 *
 */
package jenjinn.engine.boardstate;

import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;
import static jenjinn.engine.eval.piecesquaretables.TestingPieceSquareTables.getEndgameTables;
import static jenjinn.engine.eval.piecesquaretables.TestingPieceSquareTables.getMidgameTables;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.List;

import org.junit.jupiter.api.Test;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import xawd.jflow.iterators.construction.IterRange;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author t
 */
class DetailedPieceLocationsTest
{
	@Test
	void testAddPieceAt()
	{
		final List<BoardSquare> locationsToAddPieceAt = getLocationSquares();
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
			assertEquals(loc.asBitboard(), locations.locationsOf(piece));
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
			assertEquals(loc.asBitboard(), locations.locationsOf(piece));
			assertEquals(bitwiseOr(take(6, locationsToAddPieceAt)), locations.getWhiteLocations());
			assertEquals(bitwiseOr(locationsToAddPieceAt.subList(6, piece.ordinal() + 1)), locations.getBlackLocations());
			assertEquals(runningMidgameEval, locations.getMidgameEval());
			assertEquals(runningEndgameEval, locations.getEndgameEval());
		}
	}

	@Test
	void testRemovePieceAt()
	{
		final List<BoardSquare> locationsToAddPieceAt = getLocationSquares();
		final PieceSquareTables midTables = getMidgameTables(), endTables = getEndgameTables();
		final long[] initialLocations = Iterate.over(locationsToAddPieceAt).mapToLong(BoardSquare::asBitboard).toArray();
		final DetailedPieceLocations locations = new DetailedPieceLocations(initialLocations, midTables, endTables);
		int runningMidgameEval = locations.getMidgameEval(), runningEndgameEval = locations.getEndgameEval();

		for (final ChessPiece piece : ChessPieces.white()) {
			final BoardSquare loc = locationsToAddPieceAt.get(piece.ordinal());
			locations.removePieceAt(loc, piece);
			runningMidgameEval -= midTables.getLocationValue(piece, loc);
			runningEndgameEval -= endTables.getLocationValue(piece, loc);
			assertEquals(0L, locations.locationsOf(piece));
			assertEquals(bitwiseOr(locationsToAddPieceAt.subList(piece.ordinal() + 1, 6)), locations.getWhiteLocations());
			assertEquals(bitwiseOr(drop(6, locationsToAddPieceAt)), locations.getBlackLocations());
			assertEquals(runningMidgameEval, locations.getMidgameEval());
			assertEquals(runningEndgameEval, locations.getEndgameEval());
		}

		for (final ChessPiece piece : ChessPieces.black()) {
			final BoardSquare loc = locationsToAddPieceAt.get(piece.ordinal());
			locations.removePieceAt(loc, piece);
			runningMidgameEval -= midTables.getLocationValue(piece, loc);
			runningEndgameEval -= endTables.getLocationValue(piece, loc);
			assertEquals(0L, locations.locationsOf(piece));
			assertEquals(0L, locations.getWhiteLocations());
			assertEquals(bitwiseOr(locationsToAddPieceAt.subList(piece.ordinal() + 1, 12)), locations.getBlackLocations());
			assertEquals(runningMidgameEval, locations.getMidgameEval());
			assertEquals(runningEndgameEval, locations.getEndgameEval());
		}
	}

	@Test
	void testGetPieceAt()
	{
		final List<BoardSquare> locationsToAddPieceAt = getLocationSquares();
		final long[] initialLocations = Iterate.over(locationsToAddPieceAt).mapToLong(BoardSquare::asBitboard).toArray();
		final DetailedPieceLocations locations = new DetailedPieceLocations(initialLocations, getMidgameTables(), getEndgameTables());

		for (final ChessPiece piece : ChessPieces.all()) {
			final BoardSquare loc = locationsToAddPieceAt.get(piece.ordinal());
			assertEquals(piece, locations.getPieceAt(loc));
			assertEquals(piece, locations.getPieceAt(loc, piece.getSide()));
			assertNull(locations.getPieceAt(loc, piece.getSide().otherSide()), piece.name());
		}

		final BoardSquare emptySquare = BoardSquare.of(63);
		assertNull(locations.getPieceAt(emptySquare));
		assertNull(locations.getPieceAt(emptySquare, Side.WHITE));
		assertNull(locations.getPieceAt(emptySquare, Side.BLACK));
	}

	private List<BoardSquare> getLocationSquares()
	{
		return IterRange.between(0, 64, 4).take(12).mapToObject(BoardSquare::of).toImmutableList();
	}
}
