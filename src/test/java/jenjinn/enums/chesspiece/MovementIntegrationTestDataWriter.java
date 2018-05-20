/**
 *
 */
package jenjinn.enums.chesspiece;

import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.misc.PieceLocations;
import xawd.jflow.iterators.construction.IterRange;

/**
 * @author ThomasB
 *
 */
public final class MovementIntegrationTestDataWriter
{
	private static final int SIDE_PIECE_COUNT = 10;

	static void writeTestData(Path filePath, int dataCount)
	{
		try {
			Files.write(
					filePath,
					createRandomPieceLocations(dataCount, SIDE_PIECE_COUNT),
					Charset.defaultCharset(),
					StandardOpenOption.CREATE_NEW);

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	static List<String> createRandomPieceLocations(final int locationCount, final int sidePieceCount)
	{
		final Random random = new Random(0x110894L);

		final Set<PieceLocations> createdLocations = new HashSet<>(locationCount);

		IterRange.to(locationCount).forEach(i ->
		{
			final int oldSize = createdLocations.size();
			while (createdLocations.size() == oldSize) {
				final PieceLocations newLocations = generateRandomBoard(random, sidePieceCount);
				if (!createdLocations.contains(newLocations)) {
					createdLocations.add(newLocations);
				}
			}
		});

		return createdLocations
				.stream()
				.map(PieceLocations::toString)
				.sorted()
				.collect(Collectors.toList());
	}

	static PieceLocations generateRandomBoard(Random numberGenerator, int sidePieceCount)
	{
		if (sidePieceCount > 32 || sidePieceCount < 0) {
			throw new IllegalArgumentException();
		}
		final List<BoardSquare> squares = new ArrayList<>(BoardSquare.valuesAsList());
		final List<BoardSquare> whiteLocs = new ArrayList<>(), blackLocs = new ArrayList<>();

		IterRange.to(sidePieceCount).forEach(i -> whiteLocs.add(squares.remove(numberGenerator.nextInt(squares.size()))));
		IterRange.to(sidePieceCount).forEach(i -> blackLocs.add(squares.remove(numberGenerator.nextInt(squares.size()))));

		return new PieceLocations(bitwiseOr(whiteLocs), bitwiseOr(blackLocs));
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		writeTestData(Paths.get("/home/t/git/Jenjinn2/src/test/resources/movementIntegrationTestData"), 500);
	}
}
