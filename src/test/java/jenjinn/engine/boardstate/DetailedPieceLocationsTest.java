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

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Side;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import jenjinn.engine.utils.BoardHasher;
import xawd.jflow.iterators.factories.IterRange;
import xawd.jflow.iterators.factories.Iterate;

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

		int runningMidgameEval = locations.getMidgameEval(), runningEndgameEval = locations.getEndgameEval();
		long runningHash = locations.getSquarePieceFeatureHash();

		for (final ChessPiece piece : ChessPieces.white()) {
			final BoardSquare loc = locationsToAddPieceAt.get(piece.ordinal());
			locations.addPieceAt(loc, piece);
			assertEquals(loc.asBitboard(), locations.locationsOf(piece));
			assertEquals(bitwiseOr(take(piece.ordinal() + 1, locationsToAddPieceAt)), locations.getWhiteLocations());
			assertEquals(0L, locations.getBlackLocations());

			runningMidgameEval += midTables.getLocationValue(piece, loc);
			assertEquals(runningMidgameEval, locations.getMidgameEval());
			runningEndgameEval += endTables.getLocationValue(piece, loc);
			assertEquals(runningEndgameEval, locations.getEndgameEval());
			runningHash ^= BoardHasher.INSTANCE.getSquarePieceFeature(loc, piece);
			assertEquals(runningHash, locations.getSquarePieceFeatureHash());
		}

		for (final ChessPiece piece : ChessPieces.black()) {
			final BoardSquare loc = locationsToAddPieceAt.get(piece.ordinal());
			locations.addPieceAt(loc, piece);
			assertEquals(loc.asBitboard(), locations.locationsOf(piece));
			assertEquals(bitwiseOr(take(6, locationsToAddPieceAt)), locations.getWhiteLocations());
			assertEquals(bitwiseOr(locationsToAddPieceAt.subList(6, piece.ordinal() + 1)), locations.getBlackLocations());

			runningMidgameEval += midTables.getLocationValue(piece, loc);
			assertEquals(runningMidgameEval, locations.getMidgameEval());
			runningEndgameEval += endTables.getLocationValue(piece, loc);
			assertEquals(runningEndgameEval, locations.getEndgameEval());
			runningHash ^= BoardHasher.INSTANCE.getSquarePieceFeature(loc, piece);
			assertEquals(runningHash, locations.getSquarePieceFeatureHash());
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
		long runningHash = locations.getSquarePieceFeatureHash();

		for (final ChessPiece piece : ChessPieces.white()) {
			final BoardSquare loc = locationsToAddPieceAt.get(piece.ordinal());
			locations.removePieceAt(loc, piece);
			assertEquals(0L, locations.locationsOf(piece));
			assertEquals(bitwiseOr(locationsToAddPieceAt.subList(piece.ordinal() + 1, 6)), locations.getWhiteLocations());
			assertEquals(bitwiseOr(drop(6, locationsToAddPieceAt)), locations.getBlackLocations());

			runningMidgameEval -= midTables.getLocationValue(piece, loc);
			assertEquals(runningMidgameEval, locations.getMidgameEval());
			runningEndgameEval -= endTables.getLocationValue(piece, loc);
			assertEquals(runningEndgameEval, locations.getEndgameEval());
			runningHash ^= BoardHasher.INSTANCE.getSquarePieceFeature(loc, piece);
			assertEquals(runningHash, locations.getSquarePieceFeatureHash());
		}

		for (final ChessPiece piece : ChessPieces.black()) {
			final BoardSquare loc = locationsToAddPieceAt.get(piece.ordinal());
			locations.removePieceAt(loc, piece);
			assertEquals(0L, locations.locationsOf(piece));
			assertEquals(0L, locations.getWhiteLocations());
			assertEquals(bitwiseOr(locationsToAddPieceAt.subList(piece.ordinal() + 1, 12)), locations.getBlackLocations());

			runningMidgameEval -= midTables.getLocationValue(piece, loc);
			assertEquals(runningMidgameEval, locations.getMidgameEval());
			runningEndgameEval -= endTables.getLocationValue(piece, loc);
			assertEquals(runningEndgameEval, locations.getEndgameEval());
			runningHash ^= BoardHasher.INSTANCE.getSquarePieceFeature(loc, piece);
			assertEquals(runningHash, locations.getSquarePieceFeatureHash());
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
		return IterRange.between(0, 64, 4).take(12).mapToObject(BoardSquare::of).toList();
	}
}
