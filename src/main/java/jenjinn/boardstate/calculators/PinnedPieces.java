/**
 *
 */
package jenjinn.boardstate.calculators;

import static java.lang.Long.bitCount;
import static jenjinn.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.bitboards.Bitboards.emptyBoardAttackset;
import static jenjinn.moves.MoveCache.getMove;

import jenjinn.base.Side;
import jenjinn.base.Square;
import jenjinn.bitboards.BitboardIterator;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.DetailedPieceLocations;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;

/**
 * @author ThomasB
 */
public final class PinnedPieces
{
	private PinnedPieces()
	{
	}

	/**
	 * Given a board state this function calculates a List of pinned active pieces.
	 * That is those pieces who have extra constraints on their legal moves in order
	 * that the active side cannot move into check.
	 *
	 * @param state
	 *            The game state to calculate the active pinned pieces of.
	 * @return A list of the pinned piece locations as well as their constrained
	 *         move areas.
	 */
	public static PinnedPieceCollection in(BoardState state)
	{
		Side active = state.getActiveSide(), passive = active.otherSide();
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		long activeLocs = pieceLocs.getSideLocations(active), allLocs = pieceLocs.getAllLocations();

		Piece activeKing = ChessPieces.of(active).last();
		Square kingLoc = pieceLocs.iterateLocs(activeKing).next();
		long kloc = kingLoc.bitboard;

		return ChessPieces.pinnersOn(passive).flow()
				/*
				 * Map piece to a Flow of its locations at which it would attack king on empty
				 * board and flatten (we no longer need piece info).
				 */
				.flatMap(piece -> pieceLocs.iterateLocs(piece)
						.filter(loc -> bitboardsIntersect(emptyBoardAttackset(piece, loc), kloc)))
				/*
				 * Map each location to the cord connecting it and the king location with king
				 * location removed.
				 */
				.mapToLong(loc -> getMove(loc, kingLoc).getInducedCord() ^ kloc)
				/* Choose only those cords which have exactly one (active) piece on */
				.filter(cord -> bitCount(cord & activeLocs) == 1 && bitCount(cord & allLocs) == 2)
				/* This active piece must therefore be pinned. */
				.mapToObject(cord -> {
					Square pieceLoc = BitboardIterator.from(cord & activeLocs).next();
					return new PinnedPiece(pieceLoc, cord);
				})
				.build(PinnedPieceCollection::new);
	}
}
