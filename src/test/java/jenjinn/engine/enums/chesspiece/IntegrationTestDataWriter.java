/**
 *
 */
package jenjinn.engine.enums.chesspiece;

import static java.util.stream.Collectors.toList;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.utils.BasicPieceLocations;
import xawd.jflow.iterators.factories.IterRange;

/**
 * @author ThomasB
 *
 */
public final class IntegrationTestDataWriter
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
			throw new RuntimeException(e);
		}
	}

	static List<String> createRandomPieceLocations(final int locationCount, final int sidePieceCount)
	{
		final Random random = new Random(0x110894L);

		final Set<BasicPieceLocations> createdLocations = new HashSet<>(locationCount);

		IterRange.to(locationCount).forEach(i ->
		{
			final int oldSize = createdLocations.size();
			while (createdLocations.size() == oldSize) {
				final BasicPieceLocations newLocations = generateRandomBoard(random, sidePieceCount);
				if (!createdLocations.contains(newLocations)) {
					createdLocations.add(newLocations);
				}
			}
		});

		return createdLocations
				.stream()
				.map(BasicPieceLocations::toString)
				.sorted()
				.collect(toList());
	}

	static BasicPieceLocations generateRandomBoard(Random numberGenerator, int sidePieceCount)
	{
		if (sidePieceCount > 32 || sidePieceCount < 0) {
			throw new IllegalArgumentException();
		}
		final List<BoardSquare> squares = new ArrayList<>(BoardSquare.valuesAsList());
		final List<BoardSquare> whiteLocs = new ArrayList<>(), blackLocs = new ArrayList<>();

		IterRange.to(sidePieceCount).forEach(i -> whiteLocs.add(squares.remove(numberGenerator.nextInt(squares.size()))));
		IterRange.to(sidePieceCount).forEach(i -> blackLocs.add(squares.remove(numberGenerator.nextInt(squares.size()))));

		return new BasicPieceLocations(bitwiseOr(whiteLocs), bitwiseOr(blackLocs));
	}

	//	/**
	//	 * @param args
	//	 */
	//	public static void main(final String[] args)
	//	{
	//		writeTestData(Paths.get("/home/t/git/Jenjinn2/src/test/resources/movementIntegrationTestData"), 500);
	//	}
}
