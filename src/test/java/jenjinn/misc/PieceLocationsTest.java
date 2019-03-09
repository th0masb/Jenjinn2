/**
 *
 */
package jenjinn.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import com.github.maumay.jflow.iterators.factories.Iter;

import jenjinn.utils.BasicPieceLocations;

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

		Iter.until(1000).forEach(i -> {
			final long whiteLocs = random.nextLong(),
					blackLocs = random.nextLong() & ~whiteLocs;
			final BasicPieceLocations locations = new BasicPieceLocations(whiteLocs,
					blackLocs);
			final BasicPieceLocations reconstructed = BasicPieceLocations
					.reconstructFrom(locations.toString());
			assertEquals(locations, reconstructed);
		});
	}
}
