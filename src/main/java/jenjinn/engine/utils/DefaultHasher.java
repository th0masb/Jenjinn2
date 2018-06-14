/**
 *
 */
package jenjinn.engine.utils;

/**
 * @author ThomasB
 *
 */
public final class DefaultHasher {

	private DefaultHasher() {
	}

	private static final long DEFAULT_SEED = 0x110894L;

	public static ZobristHasher get()
	{
		return ZobristHasher.getFromSeed(DEFAULT_SEED);
	}
}
