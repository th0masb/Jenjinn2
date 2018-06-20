/**
 *
 */
package jenjinn.engine.pgn;

import static java.lang.Long.toHexString;
import static java.lang.Math.min;
import static xawd.jflow.utilities.StringUtils.matchesAnywhere;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public final class MoveDatabaseWriter implements Closeable
{
//	public static final String HASH_MOVE_SEPARATOR = ":", POSITION_SEPARATOR = "|";
	private static final String PGN_EXT = ".pgn";
	private static final int POSITIONS_PER_LINE = 15, GAME_DEPTH_CAP = 20;

	private final BufferedReader src;
	private final BufferedWriter out;
	private final Set<Long> usedPositions = new HashSet<>();

	private int totalGamesSearched = 0, totalErrorsInGames = 0;

	public MoveDatabaseWriter(final Path sourceFilePath, final Path outFilePath) throws IOException
	{
		if (!Files.exists(sourceFilePath) || Files.exists(outFilePath) || !sourceFilePath.toString().endsWith(PGN_EXT)) {
			throw new IllegalArgumentException();
		}
		src = Files.newBufferedReader(sourceFilePath);
		out = Files.newBufferedWriter(outFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
	}

	public void writeUniquePositions() throws IOException
	{
		final List<PositionalInstruction> writeBuffer = new ArrayList<>(POSITIONS_PER_LINE);
		Optional<String> game = readGame();
		while (game.isPresent()) {
			totalGamesSearched++;
			writeUniquePositions(game.get(), writeBuffer);
			game = readGame();
		}
		flushBuffer(writeBuffer);

		final String outputLog = new StringBuilder("We searched " )
				.append(totalGamesSearched)
				.append(" games and extracted ")
				.append(usedPositions.size())
				.append(" moves. There were ")
				.append(totalErrorsInGames)
				.append(" pgns which caused an error.")
				.toString();

		System.out.println(outputLog);
	}

	private void writeUniquePositions(final String gameString, final List<PositionalInstruction> writeBuffer) throws IOException
	{
		try {
			final List<ChessMove> moves = PgnGameConverter.parse(gameString);
			final BoardState state = StartStateGenerator.getStartBoard();
			for (int i = 0; i < min(GAME_DEPTH_CAP, moves.size()); i++) {
				final ChessMove ithMove = moves.get(i);
				final long stateHash = state.calculateHash();
				if (!usedPositions.contains(stateHash)) {
					usedPositions.add(stateHash);
					final PositionalInstruction newInstruction = new PositionalInstruction(stateHash, ithMove.toCompactString());
					addPositionToBuffer(newInstruction, writeBuffer);
				}
				ithMove.makeMove(state);
			}
		} catch (final BadPgnException e) {
			System.err.println("Error in game: " + gameString);
			totalErrorsInGames++;
			return;
		}
	}

	private void addPositionToBuffer(final PositionalInstruction instructionToAdd, final List<PositionalInstruction> buffer) throws IOException
	{
		if (buffer.size() == POSITIONS_PER_LINE) {
			flushBuffer(buffer);
		}
		buffer.add(instructionToAdd);
	}

	private void flushBuffer(final List<PositionalInstruction> buffer) throws IOException
	{
		if (!buffer.isEmpty()) {
			final int bufsze = buffer.size();
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
		}
		else {
			final String gameStart = PgnGameConverter.GAME_START, gameEnd = PgnGameConverter.GAME_TERMINATION;
			while (!matchesAnywhere(nextLine, gameStart)) {
				nextLine = src.readLine();
				if (nextLine == null) {
					return Optional.empty();
				}
			}
			final StringBuilder game = new StringBuilder(nextLine).append(" ");
			while (!matchesAnywhere(nextLine, gameEnd)) {
				nextLine = src.readLine();
				if (nextLine == null) {
					return Optional.empty();
				}
				else {
					game.append(nextLine).append(" ");
				}
			}
			return Optional.of(game.toString().trim());
		}
	}

	public static void main(final String[] args) throws IOException
	{
		final Path source = Paths.get("C:", "bin", "messabout", "KIDClassical.pgn");
		final Path out = Paths.get("C:", "bin", "messabout", "classicalkid.odb");

//		final Consumer<Object> print = System.out::println;
//
//		print.accept(Files.exists(source));
//		print.accept(Files.exists(out));

		try (final MoveDatabaseWriter writer = new MoveDatabaseWriter(source, out)) {
			writer.writeUniquePositions();
		}
	}

	@Override
	public void close() throws IOException
	{
		src.close();
		out.close();
	}

	private class PositionalInstruction {
		private final long positionHash;
		private final String compactMoveString;

		public PositionalInstruction(final long positionHash, final String compactMoveString)
		{
			this.positionHash = positionHash;
			this.compactMoveString = compactMoveString;
		}

		@Override
		public String toString() {
			return toHexString(positionHash) + compactMoveString.toUpperCase();
		}
	}
}
