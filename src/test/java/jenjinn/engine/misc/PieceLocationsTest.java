/**
 *
 */
package jenjinn.engine.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import xawd.jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 *
 */
class PieceLocationsTest
{
	@Test
	void testPieceLocationsReconstruction()
	{
		final Random random = new Random(0x110894L);

		IterRange.to(1000).forEach(i ->
		{
			final long whiteLocs = random.nextLong(), blackLocs = random.nextLong() & ~whiteLocs;
			final PieceLocations locations = new PieceLocations(whiteLocs, blackLocs);
			final PieceLocations reconstructed = PieceLocations.reconstructFrom(locations.toString());
			assertEquals(locations, reconstructed);
		});
	}
}
