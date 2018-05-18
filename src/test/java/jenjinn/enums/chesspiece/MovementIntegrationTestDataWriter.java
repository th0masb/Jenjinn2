/**
 *
 */
package jenjinn.enums.chesspiece;

import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jenjinn.engine.bitboards.BitboardUtils;
import jenjinn.engine.enums.BoardSquare;

/**
 * @author ThomasB
 *
 */
public final class MovementIntegrationTestDataWriter
{
	static List<PieceLocations> createRandomPieceLocations(final int locationCount, final int sidePieceCount)
	{
		final Random random = new Random(0x110894L);

		final List<PieceLocations> createdLocations = new ArrayList<>(locationCount);
		final Set<Integer> createdLocationsHashcodeSet = new HashSet<>(locationCount);

		final List<BoardSquare> squares = BoardSquare.iterateAll().toCollection(ArrayList::new);
		final List<BoardSquare> whiteLocs = new ArrayList<>(sidePieceCount), blackLocs = new ArrayList<>(sidePieceCount);

		for (int i = 0; i < 2*sidePieceCount; i++)
		{
			final BoardSquare nextSquare = squares.remove(random.nextInt(squares.size()));
			if ((i % 2) == 0) {
				whiteLocs.add(nextSquare);
			}
			else {
				blackLocs.add(nextSquare);
			}
		}

		final PieceLocations newCreatedLocations = new PieceLocations(bitwiseOr(whiteLocs), BitboardUtils.bitwiseOr(blackLocs));

		throw new RuntimeException();
	}



	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		// TODO Auto-generated method stub
	}
}
