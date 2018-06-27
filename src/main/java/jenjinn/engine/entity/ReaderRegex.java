/**
 *
 */
package jenjinn.engine.entity;

/**
 * @author ThomasB
 *
 */
public final class ReaderRegex
{
	public static final String SQUARE = "([A-H][1-8])";
	public static final String POSITION = "([a-f0-9]+)";
	public static final String CASTLEMOVE = "(WK|WQ|BK|BQ)";
	public static final String EPMOVE = "(E([A-H][1-8]){2})";
	public static final String SMOVE = "(S([A-H][1-8]){2})";
	public static final String MOVE = "(" + CASTLEMOVE + "|" + EPMOVE + "|" + SMOVE + ")";
	public static final String POS_AND_MOVE = "(" + POSITION + MOVE + ")";

	private ReaderRegex()
	{
	}
}
