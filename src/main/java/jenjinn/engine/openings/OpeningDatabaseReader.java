/**
 *
 */
package jenjinn.engine.openings;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.StringUtils.findFirstMatch;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnpassantMove;
import jenjinn.engine.moves.MoveCache;
import jenjinn.engine.utils.FileUtils;
import xawd.jflow.collections.FlowList;

/**
 * @author ThomasB
 */
public class OpeningDatabaseReader implements Closeable
{
	private final BufferedReader src;
	private boolean consumed = false;

	public OpeningDatabaseReader(String databaseFilename)
	{
		final String absname = FileUtils.absoluteName(getClass(), databaseFilename);
		src = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(absname)));
	}

	public Optional<ChessMove> searchForMove(long positionHash) throws IOException
	{
		if (!consumed) {
			consumed = true;
			String line = src.readLine();
			while (line != null) {
				final Optional<ChessMove> extract = extractMove(line, positionHash);
				if (extract.isPresent()) {
					return extract;
				}
				else {
					line = src.readLine();
				}
			}
			return Optional.empty();
		}
		else {
			throw new IllegalStateException();
		}
	}

	private Optional<ChessMove> extractMove(String line, long positionHash)
	{
		for (final String positionWithMove : getAllMatches(line, ReaderRegex.POS_AND_MOVE)) {
			final String pos = findFirstMatch(positionWithMove, ReaderRegex.POSITION).orElseThrow(AssertionError::new);
			if (Long.parseUnsignedLong(pos, 16) == positionHash) {
				final String encodedMove = findFirstMatch(positionWithMove, ReaderRegex.MOVE).orElseThrow(AssertionError::new);
				return Optional.of(decodeMove(encodedMove));
			}
		}
		return Optional.empty();
	}

	private ChessMove decodeMove(String encodedMove)
	{
		final FlowList<BoardSquare> squares = getAllMatches(encodedMove, ReaderRegex.SQUARE)
				.map(BoardSquare::valueOf).toList();

		if (encodedMove.matches(ReaderRegex.SMOVE)) {
			return MoveCache.getMove(head(squares), tail(squares));
		}
		else if (encodedMove.matches(ReaderRegex.EPMOVE)) {
			return new EnpassantMove(head(squares), tail(squares));
		}
		else if (encodedMove.matches(ReaderRegex.CASTLEMOVE)) {
			return MoveCache.getMove(CastleZone.fromSimpleIdentifier(encodedMove));
		}
		else {
			throw new AssertionError(encodedMove);
		}
	}

	@Override
	public void close() throws IOException
	{
		src.close();
	}

//	public static void main(String[] args) throws IOException
//	{
//		//127a9aec8d69d3b9 7fd6fc9a72b32560
//
////		System.out.println(Long.parseUnsignedLong("127a9aec8d69d3b9", 16));
////		System.out.println(Long.parseUnsignedLong("7fd6fc9a72b32560", 16));
//
//		final BoardState start = StartStateGenerator.getStartBoard();
////		System.out.println(start.calculateHash());
//		MoveCache.getMove(BoardSquare.D2, BoardSquare.D4).makeMove(start);
////		System.out.println(start.calculateHash());
//
//		try (OpeningDatabaseReader reader = new OpeningDatabaseReader("classicalkid.odb")) {
////			System.out.println(start.calculateHash());
//			System.out.println(reader.searchForMove(start.calculateHash()));
////			final String s = reader.src.readLine();
////			System.out.println(getAllMatches(s, ReaderRegex.POS_AND_MOVE));
//		}
//	}
}
