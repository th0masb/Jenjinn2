/**
 *
 */
package jenjinn.eval;

import static java.lang.Math.max;
import static jenjinn.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.bitboards.Bitboards.emptyBoardAttackset;

import jenjinn.base.Side;
import jenjinn.base.Square;
import jenjinn.bitboards.BitboardIterator;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.DetailedPieceLocations;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;
import jflow.iterators.Flow;

/**
 * @author ThomasB
 *
 */
public class StaticExchangeEvaluator
{
	private long target, source, attadef, xrays;

	public boolean isGoodExchange(Square sourceSquare, Square targetSquare, BoardState state)
	{
		// Make sure all instance variables set correctly first
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		source = sourceSquare.bitboard;
		target = targetSquare.bitboard;
		generateAttackDefenseInfo(pieceLocs);
		long knightLocs = pieceLocs.locationsOf(Piece.WHITE_KNIGHT)
				| pieceLocs.locationsOf(Piece.BLACK_KNIGHT);

		int d = 0;
		int[] gain = new int[32];
		gain[d] = PieceValues.MIDGAME.valueOf(pieceLocs.getPieceAt(targetSquare));
		Piece attPiece = pieceLocs.getPieceAt(source);

		Side activeSide = state.getActiveSide();
		do {
			d++;
			activeSide = activeSide.otherSide();
			gain[d] = PieceValues.MIDGAME.valueOf(attPiece) - gain[d - 1];
			if (max(-gain[d - 1], gain[d]) < 0) {
				break;
			}

			attadef ^= source;
			// If a knight moves to attack or defend it can't open an x-ray.
			if (!bitboardsIntersect(source, knightLocs)) {
				updateXrays(pieceLocs);
			}
			source = getLeastValuablePiece(state.getPieceLocations(), activeSide);
			attPiece = pieceLocs.getPieceAt(source);
		} while (source != 0);

		while (--d > 0) {
			gain[d - 1] = -Math.max(-gain[d - 1], gain[d]);
		}
		return gain[0]>= 0;
	}

	private void updateXrays(DetailedPieceLocations pieceLocs)
	{
		if (xrays != 0) {
			Flow<Square> xrayLocs = BitboardIterator.from(xrays);
			long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
			while (xrayLocs.hasNext()) {
				Square loc = xrayLocs.next();
				Piece p = pieceLocs.getPieceAt(loc);
				if (bitboardsIntersect(p.getSquaresOfControl(loc, white, black), target)) {
					long locBitboard = loc.bitboard;
					xrays ^= locBitboard;
					attadef ^= locBitboard;
				}
			}
		}
	}

	private void generateAttackDefenseInfo(DetailedPieceLocations locationProvider)
	{
		attadef = 0L;
		xrays = 0L;
		long white = locationProvider.getWhiteLocations();
		long black = locationProvider.getBlackLocations();

		for (Piece p : ChessPieces.ALL) {
			Flow<Square> locations = locationProvider.iterateLocs(p);
			while (locations.hasNext()) {
				Square loc = locations.next();
				long control = p.getSquaresOfControl(loc, white, black);
				if (bitboardsIntersect(control, target)) {
					attadef |= loc.bitboard;
				}
				else if (p.isSlidingPiece() && bitboardsIntersect(emptyBoardAttackset(p, loc), target)) {
					xrays |= loc.bitboard;
				}
			}
		}
	}

	private long getLeastValuablePiece(DetailedPieceLocations locationProvider, Side fromSide)
	{
		for (Piece p : ChessPieces.of(fromSide)) {
			long intersection = attadef & locationProvider.locationsOf(p);
			if (intersection != 0) {
				return (intersection & -intersection);
			}
		}
		return 0L;
	}
}
