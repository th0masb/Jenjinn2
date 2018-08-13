/**
 *
 */
package jenjinn.engine.eval;

import static java.lang.Math.max;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.Bitboards.emptyBoardAttackset;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Side;
import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.iterators.Flow;

/**
 * @author ThomasB
 *
 */
public class StaticExchangeEvaluator
{
	private long target, source, attadef, xrays;

	public boolean isGoodExchange(BoardSquare sourceSquare, BoardSquare targetSquare, BoardState state)
	{
		// Make sure all instance variables set correctly first
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		source = sourceSquare.asBitboard();
		target = targetSquare.asBitboard();
		generateAttackDefenseInfo(pieceLocs);
		long knightLocs = pieceLocs.locationsOf(ChessPiece.WHITE_KNIGHT)
				| pieceLocs.locationsOf(ChessPiece.BLACK_KNIGHT);

		int d = 0;
		int[] gain = new int[32];
		gain[d] = PieceValues.MIDGAME.valueOf(pieceLocs.getPieceAt(targetSquare));
		ChessPiece attPiece = pieceLocs.getPieceAt(source);

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
			Flow<BoardSquare> xrayLocs = BitboardIterator.from(xrays);
			long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
			while (xrayLocs.hasNext()) {
				BoardSquare loc = xrayLocs.next();
				ChessPiece p = pieceLocs.getPieceAt(loc);
				if (bitboardsIntersect(p.getSquaresOfControl(loc, white, black), target)) {
					long locBitboard = loc.asBitboard();
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

		for (ChessPiece p : ChessPieces.all()) {
			Flow<BoardSquare> locations = locationProvider.iterateLocs(p);
			while (locations.hasNext()) {
				BoardSquare loc = locations.next();
				long control = p.getSquaresOfControl(loc, white, black);
				if (bitboardsIntersect(control, target)) {
					attadef |= loc.asBitboard();
				}
				else if (p.isSlidingPiece() && bitboardsIntersect(emptyBoardAttackset(p, loc), target)) {
					xrays |= loc.asBitboard();
				}
			}
		}
	}

	private long getLeastValuablePiece(DetailedPieceLocations locationProvider, Side fromSide)
	{
		for (ChessPiece p : ChessPieces.ofSide(fromSide)) {
			long intersection = attadef & locationProvider.locationsOf(p);
			if (intersection != 0) {
				return (intersection & -intersection);
			}
		}
		return 0L;
	}
}
