/**
 *
 */
package jenjinn.engine.eval;

import jenjinn.engine.base.CastleZone;
import jenjinn.engine.base.DevelopmentPiece;
import jenjinn.engine.boardstate.BoardState;

/**
 * @author ThomasB
 */
public final class DevelopmentEvaluator implements EvaluationComponent
{
	private static final int[] DEVELOPMENT_VALUES = {110, 100, 200, 200, 100, 100};
	private static final int[] CASTLING_VALUES = {200, 180};

	public DevelopmentEvaluator()
	{
	}

	@Override
	public int evaluate(BoardState state)
	{
		int eval = 0;
		for (final DevelopmentPiece devPiece : state.getDevelopedPieces()) {
			eval += getValueOf(devPiece);
		}
		eval += getValueOf(state.getCastlingStatus().getWhiteCastlingStatus());
		eval += getValueOf(state.getCastlingStatus().getBlackCastlingStatus());
		return eval;
	}

	static int getValueOf(DevelopmentPiece dpiece)
	{
		final int ord = dpiece.ordinal();
		return (1 - 2*(ord / 6)) * DEVELOPMENT_VALUES[ord % 6];
	}

	static int getValueOf(CastleZone dpiece)
	{
		if (dpiece == null) {
			return 0;
		}
		else {
			final int ord = dpiece.ordinal();
			return (1 - 2*(ord / 2)) * CASTLING_VALUES[ord % 2];
		}
	}
}
