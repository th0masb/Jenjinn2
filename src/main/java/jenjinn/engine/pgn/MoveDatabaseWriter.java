/**
 *
 */
package jenjinn.engine.pgn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ThomasB
 *
 */
public final class MoveDatabaseWriter
{
	private static final String PGN_EXT = ".pgn", OUT_EXT = ".odb";

	private final BufferedReader src;
	private final BufferedWriter out;

	private final Set<Long> usedPositions = new HashSet<>();
	/**
	 * @throws IOException
	 *
	 */
	public MoveDatabaseWriter(final Path sourceFilePath, final Path outFilePath) throws IOException
	{
		if (!Files.exists(sourceFilePath) || Files.exists(outFilePath) || sourceFilePath.toString().endsWith(PGN_EXT)) {
			throw new IllegalArgumentException();
		}
		src = Files.newBufferedReader(sourceFilePath);
		out = Files.newBufferedWriter(outFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
	}


}
