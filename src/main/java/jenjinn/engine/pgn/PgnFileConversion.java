/**
 *
 */
package jenjinn.engine.pgn;

import static jenjinn.engine.pgn.PgnConverter.PGN_EXT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import xawd.jflow.collections.FlowList;
import xawd.jflow.collections.Lists;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

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
			final Flow<Path> files = Iterate.wrap(Files.newDirectoryStream(srcFolder).iterator());
			files.filter(pth -> pth.toString().endsWith(PGN_EXT)).forEach(src -> {
				final String outFileName = src.getFileName().toString().replaceFirst(PGN_EXT, "");
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
		final FlowList<String> folderNames = Lists.build("modernkings", "classicalqueens", "modernqueens", "flankandunorthodox");
		folderNames.filter(name -> !Files.isDirectory(Paths.get("/home", "t", "chesspgns", name))).safeNext().ifPresent(x -> {throw new RuntimeException();});

		for (final String folderName : folderNames) {
			System.out.println("---------------------------------------------------------------------");
			System.out.println("Converting files in " + folderName);

			final Path source = Paths.get("/home", "t", "chesspgns", folderName);
			final Path outFolder = Paths.get("/home", "t", "chesspgns", "converted" + folderName);

			executeConversion(source, outFolder, t -> {
				try {
					t.writeUniquePositions(12);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			});

			System.out.println("Finished.");
			System.out.println("---------------------------------------------------------------------");
		}



		// write game lines


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
