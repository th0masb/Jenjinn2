/**
 *
 */
package jenjinn.engine.movegeneration;

import java.util.List;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.moves.CastleMove;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.StandardMove;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author ThomasB
 */
public final class MoveGenerator {

	private MoveGenerator() {}

	public static List<ChessMove> getAvailableMoves(final BoardState state)
	{
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long whiteLocs = pieceLocs.getWhiteLocations(), blackLocs = pieceLocs.getBlackLocations();

		throw new RuntimeException();
	}

	static List<CastleMove> getCastleMoves(final BoardState state)
	{
		throw new RuntimeException();
//		if (state.getCastlingStatus().getStatusFor(state.getActiveSide()) == null) {
//			final Side activeSide = state.getActiveSide();
//			final Predicate<CastleZone> sideFilter = activeSide.isWhite() ? z -> z.isWhiteZone() : z -> !z.isWhiteZone();
//			final Flow<CastleZone> availableRights = Iterate.over(state.getCastlingStatus().getCastlingRights()).filter(sideFilter);
//
//			if (availableRights.hasNext()) {
////				long allPieces
//			}
//		}
//		else {
//			return Collections.emptyList();
//		}
	}

	static List<StandardMove> getStandardMoves(final BoardState state)
	{
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long whiteLocs = pieceLocs.getWhiteLocations(), blackLocs = pieceLocs.getBlackLocations();
		final List<ChessPiece> activePieces = ChessPieces.ofSide(state.getActiveSide());

		return Iterate.over(activePieces).flatten(piece -> {
			final long locs = pieceLocs.locationsOf(piece);
			return BitboardIterator.from(locs).flatten(loc -> convertBitboardToMoves(loc, piece.getMoves(loc, whiteLocs, blackLocs)));
		}).toList();
	}

	static Flow<StandardMove> convertBitboardToMoves(final BoardSquare source, final long bitboard)
	{
		return BitboardIterator.from(bitboard).map(target -> MoveCache.getMove(source, target));
	}
}