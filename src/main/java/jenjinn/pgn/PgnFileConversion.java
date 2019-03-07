/**
 *
 */
package jenjinn.pgn;

import static jenjinn.pgn.PgnConverter.PGN_EXT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import jflow.iterators.Flow;
import jflow.iterators.factories.Iterators;
import jflow.seq.Seq;

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
			Flow<Path> files = Iterators.wrap(Files.newDirectoryStream(srcFolder).iterator());
			files.filter(pth -> pth.toString().endsWith(PGN_EXT)).forEach(src -> {
				String outFileName = src.getFileName().toString().replaceFirst(PGN_EXT, "");
				Path out = Paths.get(outFolder.toString(), outFileName);
				try (PgnConverter writer = new PgnConverter(src, out)) {
					conversionInstructions.accept(writer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} else {
			throw new AssertionError();
		}
	}

	public static void main(String[] args) throws IOException
	{
		Seq<String> folderNames = Seq.of("modernkings", "classicalqueens", "modernqueens", "flankandunorthodox");
		folderNames.findFirst(name -> !Files.isDirectory(Paths.get("/home", "t", "chesspgns", name))).ifPresent(x -> {throw new RuntimeException();});

		for (String folderName : folderNames) {
			System.out.println("---------------------------------------------------------------------");
			System.out.println("Converting files in " + folderName);

			Path source = Paths.get("/home", "t", "chesspgns", folderName);
			Path outFolder = Paths.get("/home", "t", "chesspgns", "converted" + folderName);

			executeConversion(source, outFolder, t -> {
				try {
					t.writeUniquePositions(12);
				} catch (IOException e) {
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
		//			} catch (IOException e) {
		//				throw new RuntimeException(e);
		//			}
		//		});
	}
}
