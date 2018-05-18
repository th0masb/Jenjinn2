/**
 *
 */
package jenjinn.enums.chesspiece;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

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
		final Random random = new Random(110894L);

		IterRange.to(1000).forEach(i ->
		{
			final PieceLocations locations = new PieceLocations(random.nextLong(), random.nextLong());
			final PieceLocations reconstructed = PieceLocations.reconstructFrom(locations.toString());
			assertEquals(locations, reconstructed);
		});
	}
}
