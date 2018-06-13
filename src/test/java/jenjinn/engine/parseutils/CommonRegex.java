/**
 *
 */
package jenjinn.engine.parseutils;

/**
 * @author ThomasB
 *
 */
public final class CommonRegex
{
	public static final String SINGLE_SQUARE = "[a-hA-H][1-8]";
	public static final String CORD = SINGLE_SQUARE + "\\-\\>" + SINGLE_SQUARE;

	private CommonRegex()
	{
	}

}
