/**
 *
 */
package jenjinn.engine.pgn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * @author t
 */
public class PgnFileConversion
{
	private PgnFileConversion()
	{
	}

	static void executeConversion(Path srcFolder, Path outFolder, Consumer<PgnConverter> conversionInstructions)
			throws IOException
	{
		if (!outFolder.toFile().exists()) {
			Files.createDirectory(outFolder);
			Files.newDirectoryStream(srcFolder).forEach(src -> {
				final String outFileName = src.getFileName().toString().replaceFirst(PgnConverter.PGN_EXT, "");
				final Path out = Paths.get(outFolder.toString(), outFileName);
				try (final PgnConverter writer = new PgnConverter(src, out)) {
					conversionInstructions.accept(writer);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			});
		} else {
			throw new AssertionError();
		}
	}

	public static void main(String[] args) throws IOException
	{
		final Path source = Paths.get("/home", "t", "chesspgns", "test");
		final Path outFolder = Paths.get("/home", "t", "chesspgns", "convertedtest");

		// write game lines
		executeConversion(source, outFolder, t -> {
			try {
				t.writeLines();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		});

		//		// Write opening db
		//		executeConversion(source, outFolder, t -> {
		//			try {
		//				t.writeUniquePositions();
		//			} catch (final IOException e) {
		//				throw new RuntimeException(e);
		//			}
		//		});
	}
}
