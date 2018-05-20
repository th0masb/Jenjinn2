/**
 *
 */
package jenjinn.engine.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import jenjinn.engine.misc.PieceLocations;
import xawd.jflow.iterators.construction.IterRange;

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
			final PieceLocations locations = new PieceLocations(random.nextLong(), random.nextLong());
			final PieceLocations reconstructed = PieceLocations.reconstructFrom(locations.toString());
			assertEquals(locations, reconstructed);
		});
	}
}
