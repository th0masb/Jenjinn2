/**
 *
 */
package jenjinn.pgn;

/**
 * @author ThomasB
 *
 */
public final class CommonRegex
{
	public static final String SINGLE_SQUARE = "([a-hA-H][1-8])";
	public static final String DOUBLE_SQUARE = "(" + SINGLE_SQUARE + " +" + SINGLE_SQUARE + ")";
	public static final String CORD = "(" + SINGLE_SQUARE + "\\-\\>" + SINGLE_SQUARE + ")";
	public static final String MULTI_TARGET = "(" + SINGLE_SQUARE + "\\-\\>\\{( *" + SINGLE_SQUARE + " *)+\\})";
	public static final String SHORTHAND_MOVE = "([sScCeEpP]\\[[a-hA-H1-8kKqQwW \\-\\>\\{\\}]+( [NBRQ])?\\])";
	public static final String CASTLE_ZONE = "(([wW][kK])|([wW][qQ])|([bB][kK])|([bB][qQ]))";

	private CommonRegex()
	{
	}
}
