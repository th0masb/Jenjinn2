package jenjinn.engine.misc;

/**
 * Constant values which can be negated without numerical overflow
 *
 * @author ThomasB
 * @since 13 Jul 2017
 */
public final class Infinity
{
	public static final int INT_INFINITY = Integer.MAX_VALUE - 1;
	public static final long LONG_INFINITY = Long.MAX_VALUE - 1;
	public static final short SHORT_INFINITY = Short.MAX_VALUE - 1;

	public static final int INITIAL_ALPHA = -INT_INFINITY, INITIAL_BETA = INT_INFINITY;

	private Infinity()
	{
	}

	public static void main(final String[] args)
	{
		System.out.println(INT_INFINITY + " " + -INT_INFINITY);
		System.out.println(LONG_INFINITY + " " + -LONG_INFINITY);
		System.out.println(SHORT_INFINITY + " " + -SHORT_INFINITY);
	}
}