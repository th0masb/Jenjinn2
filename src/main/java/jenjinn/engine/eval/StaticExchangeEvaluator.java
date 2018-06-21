/**
 *
 */
package jenjinn.engine.eval;

import static java.lang.Math.max;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.Bitboards.emptyBoardAttackset;

import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.iterators.Flow;

/**
 * @author ThomasB
 *
 */
public enum StaticExchangeEvaluator
{
	INSTANCE;

	private long target, source, attadef, xrays;

	public boolean isGoodExchange(final BoardSquare targ, final BoardSquare from, final BoardState state)
	{
		// Make sure all instance variables set correctly first
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		source = from.asBitboard();
		target = targ.asBitboard();
		generateAttackDefenseInfo(pieceLocs);
		final long knightLocs = pieceLocs.locationOverviewOf(ChessPiece.WHITE_KNIGHT)
				| pieceLocs.locationOverviewOf(ChessPiece.BLACK_KNIGHT);

		int d = 0;
		final int[] gain = new int[32];
		gain[d] = PieceValues.MIDGAME.valueOf(pieceLocs.getPieceAt(targ));
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

	private void updateXrays(final DetailedPieceLocations pieceLocs)
	{
		if (xrays != 0) {
			final Flow<BoardSquare> xrayLocs = BitboardIterator.from(xrays);
			final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
			while (xrayLocs.hasNext()) {
				final BoardSquare loc = xrayLocs.next();
				final ChessPiece p = pieceLocs.getPieceAt(loc);
				if (bitboardsIntersect(p.getSquaresOfControl(loc, white, black), target)) {
					final long locBitboard = loc.asBitboard();
					xrays ^= locBitboard;
					attadef ^= locBitboard;
				}
			}
		}
	}

	private void generateAttackDefenseInfo(final DetailedPieceLocations locationProvider)
	{
		attadef = 0L;
		xrays = 0L;
		final long white = locationProvider.getWhiteLocations();
		final long black = locationProvider.getBlackLocations();

		for (final ChessPiece p : ChessPieces.all()) {
			final Flow<BoardSquare> locations = locationProvider.iterateLocs(p);
			while (locations.hasNext()) {
				final BoardSquare loc = locations.next();
				final long control = p.getSquaresOfControl(loc, white, black);
				if (bitboardsIntersect(control, target)) {
					attadef |= loc.asBitboard();
				}
				else if (p.isSlidingPiece() && bitboardsIntersect(emptyBoardAttackset(p, loc), target)) {
					xrays |= loc.asBitboard();
				}
			}
		}
	}

	private long getLeastValuablePiece(final DetailedPieceLocations locationProvider, final Side fromSide)
	{
		for (final ChessPiece p : ChessPieces.ofSide(fromSide)) {
			final long intersection = attadef & locationProvider.locationOverviewOf(p);
			if (intersection != 0) {
				return (intersection & -intersection);
			}
		}
		return 0L;
	}
}
