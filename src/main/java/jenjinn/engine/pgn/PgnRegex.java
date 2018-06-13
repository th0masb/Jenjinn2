/**
 *
 */
package jenjinn.engine.pgn;

import xawd.jflow.utilities.StringUtils;

/**
 * @author ThomasB
 */
public final class PgnRegex
{
	private static final String FILE = "([a-h])", RANK = "([1-8])";
	private static final String SQUARE = "(" + FILE + RANK +")";
	private static final String PIECE = "(|N|B|R|Q|K)";
	private static final String CHECK = "(\\+|#)";

	public static final String KINGSIDE_CASTLE = "(O-O)", QUEENSIDE_CASTLE = "(O-O-O)";
	public static final String CASTLE_MOVE = "(" + KINGSIDE_CASTLE +"|" + QUEENSIDE_CASTLE + ")";
	public static final String PROMOTION_MOVE = "(([a-h]x)?" + SQUARE + "=Q" + CHECK + "?)";
	public static final String STANDARD_MOVE = "(" + PIECE + "([a-h]|[1-8]|([a-h][1-8]))?x?" + SQUARE + CHECK + "?)";

	public static final String MOVE_LOOKBEHIND = "(?<=(\\.| {1,3}))";
	public static final String MOVE_EXTRACTOR = "(" + MOVE_LOOKBEHIND + "(" + STANDARD_MOVE + "|" + PROMOTION_MOVE + "|" + CASTLE_MOVE + "))";

	public static final String GAME_START = "(^1\\." + STANDARD_MOVE + ")";
	public static final String GAME_TERMINATION = "(((1//-0)|(0\\-1)|(1/2\\-1/2)|(\\*))$)";

	private PgnRegex()
	{
	}

	public static void main(final String[] args)
	{
		System.out.println(StringUtils.getAllMatches("23.Qd3 Rae8 24.Rf2 Rxe3 25.Qxe3 Bd4 26.Qe6+ Qxe6 27.dxe6 Bxf2+ 28.Kxf2 Bd5+", MOVE_EXTRACTOR));
	}
}
