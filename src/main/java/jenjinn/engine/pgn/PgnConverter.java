/**
 *
 */
package jenjinn.engine.pgn;

import static java.lang.Long.toHexString;
import static java.lang.Math.min;
import static xawd.jflow.utilities.Strings.matchesAnywhere;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.moves.ChessMove;

/**
 * @author ThomasB
 *
 */
public final class PgnConverter implements Closeable
{
	public static final String PGN_EXT = ".pgn";
	private static final int POSITIONS_PER_LINE = 15;

	private final String fileName;
	private final BufferedReader src;
	private final BufferedWriter out;
	private final Set<Long> usedPositions = new HashSet<>();

	private int totalGamesSearched = 0, totalErrorsInGames = 0;

	public PgnConverter(Path sourceFilePath, Path outFilePath) throws IOException
	{
		if (!Files.exists(sourceFilePath) || Files.exists(outFilePath)
				|| !sourceFilePath.toString().endsWith(PGN_EXT)) {
			throw new IllegalArgumentException();
		}
		fileName = sourceFilePath.getFileName().toString();
		src = Files.newBufferedReader(sourceFilePath, Charset.forName("ISO-8859-1"));
		out = Files.newBufferedWriter(outFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
	}

	public void writeLines() throws IOException
	{
		Optional<String> game = readGame();
		while (game.isPresent()) {
			out.write(game.get() + System.lineSeparator());
			game = readGame();
		}
	}

	public void writeUniquePositions(int gameDepthCap) throws IOException
	{
		List<PositionalInstruction> writeBuffer = new ArrayList<>(POSITIONS_PER_LINE);
		Optional<String> game = readGame();
		while (game.isPresent()) {
			totalGamesSearched++;
			writeUniquePositions(game.get(), writeBuffer, gameDepthCap);
			game = readGame();
		}
		flushBuffer(writeBuffer);

		String outputLog = new StringBuilder("We searched ").append(totalGamesSearched)
				.append(" games and extracted ").append(usedPositions.size()).append(" moves. There were ")
				.append(totalErrorsInGames).append(" pgns which caused an error in the file ").append(fileName)
				.toString();

		System.out.println(outputLog);
	}

	private void writeUniquePositions(String gameString, List<PositionalInstruction> writeBuffer,
			int gameDepthCap) throws IOException
	{
		try {
			List<ChessMove> moves = PgnGameConverter.parse(gameString);
			BoardState state = StartStateGenerator.createStartBoard();
			for (int i = 0; i < min(gameDepthCap, moves.size()); i++) {
				ChessMove ithMove = moves.get(i);
				long stateHash = state.calculateHash();
				if (!usedPositions.contains(stateHash)) {
					usedPositions.add(stateHash);
					PositionalInstruction newInstruction = new PositionalInstruction(stateHash,
							ithMove.toCompactString());
					addPositionToBuffer(newInstruction, writeBuffer);
				}
				ithMove.makeMove(state);
			}
		} catch (BadPgnException e) {
			System.err.println("Error in game: " + gameString);
			totalErrorsInGames++;
			return;
		}
	}

	private void addPositionToBuffer(PositionalInstruction instructionToAdd, List<PositionalInstruction> buffer)
			throws IOException
	{
		if (buffer.size() == POSITIONS_PER_LINE) {
			flushBuffer(buffer);
		}
		buffer.add(instructionToAdd);
	}

	private void flushBuffer(List<PositionalInstruction> buffer) throws IOException
	{
		if (!buffer.isEmpty()) {
			int bufsze = buffer.size();
			for (int i = 0; i < bufsze; i++) {
				out.write(buffer.get(i).toString());
			}
			out.newLine();
			buffer.clear();
		}
	}

	private Optional<String> readGame() throws IOException
	{
		String nextLine = src.readLine();
		if (nextLine == null) {
			return Optional.empty();
		} else {
			String gameStart = PgnGameConverter.GAME_START, gameEnd = PgnGameConverter.GAME_TERMINATION;
			while (!matchesAnywhere(nextLine, gameStart)) {
				nextLine = src.readLine();
				if (nextLine == null) {
					return Optional.empty();
				}
			}
			StringBuilder game = new StringBuilder(nextLine).append(" ");
			while (!matchesAnywhere(nextLine, gameEnd)) {
				nextLine = src.readLine();
				if (nextLine == null) {
					return Optional.empty();
				} else {
					game.append(nextLine).append(" ");
				}
			}
			return Optional.of(game.toString().trim());
		}
	}

	@Override
	public void close() throws IOException
	{
		src.close();
		out.close();
	}

	private class PositionalInstruction
	{
		private final long positionHash;
		private final String compactMoveString;

		public PositionalInstruction(long positionHash, String compactMoveString)
		{
			this.positionHash = positionHash;
			this.compactMoveString = compactMoveString;
		}

		@Override
		public String toString()
		{
			return toHexString(positionHash) + compactMoveString.toUpperCase();
		}
	}
}
