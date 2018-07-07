package jenjinn.engine.movesearch;

/**
 * Constant values which can be negated without numerical overflow
 *
 * @author ThomasB
 * @since 13 Jul 2017
 */
public final class IntConstants
{
	public static final int MAX_NEGATABLE_VALUE = Integer.MAX_VALUE - 1;
	public static final int INITIAL_ALPHA = -2 * (MAX_NEGATABLE_VALUE / 3);
	public static final int INITIAL_BETA = 2 * (MAX_NEGATABLE_VALUE / 3);
	public static final int WIN_VALUE = MAX_NEGATABLE_VALUE / 2;

	private IntConstants()
	{
	}
}