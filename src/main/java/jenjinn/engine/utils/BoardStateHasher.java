/**
 *
 */
package jenjinn.engine.utils;

import java.util.Random;

import xawd.jflow.iterators.construction.IterRange;

/**
 * @author ThomasB
 *
 */
public final class BoardStateHasher {

	private BoardStateHasher() {
	}

	private static final long DEFAULT_SEED = 0x110894L;

	public static ZobristHasher getDefault()
	{
		return getFromSeed(DEFAULT_SEED);
	}

	public static ZobristHasher getFromSeed(final long seed)
	{
		if (!seedIsValid(seed)) {
			throw new IllegalArgumentException();
		}
		return new ZobristHasher(new Random(seed));
	}

	private static boolean seedIsValid(final long seed)
	{
		final Random r = new Random(seed);
		return IterRange.to(800).mapToObject(i -> r.nextLong()).toSet().size() == 800;
	}
}
