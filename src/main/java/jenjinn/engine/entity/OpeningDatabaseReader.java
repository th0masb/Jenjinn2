/**
 *
 */
package jenjinn.engine.entity;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.last;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.CastleZone;
import jenjinn.engine.base.FileUtils;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnpassantMove;
import jenjinn.engine.moves.MoveCache;
import xawd.jflow.collections.FList;
import xawd.jflow.utilities.Strings;

/**
 * @author ThomasB
 */
public class OpeningDatabaseReader implements Closeable
{
	private final BufferedReader src;
	private boolean consumed = false;

	public OpeningDatabaseReader(String databaseFilename)
	{
		String absname = FileUtils.absoluteName(getClass(), databaseFilename);
		src = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(absname)));
	}

	public Optional<ChessMove> searchForMove(long positionHash) throws IOException
	{
		if (!consumed) {
			consumed = true;
			String line = src.readLine();
			while (line != null) {
				Optional<ChessMove> extract = extractMove(line, positionHash);
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
		for ( String positionWithMove : Strings.allMatches(line, ReaderRegex.POS_AND_MOVE).toList()) {
			String pos = Strings.firstMatch(positionWithMove, ReaderRegex.POSITION).get();
			if (Long.parseUnsignedLong(pos, 16) == positionHash) {
				String encodedMove = Strings.firstMatch(positionWithMove, ReaderRegex.MOVE).get();
				return Optional.of(decodeMove(encodedMove));
			}
		}
		return Optional.empty();
	}

	private ChessMove decodeMove(String encodedMove)
	{
		FList<BoardSquare> squares = Strings.allMatches(encodedMove, ReaderRegex.SQUARE)
				.map(BoardSquare::valueOf).toList();

		if (encodedMove.matches(ReaderRegex.SMOVE)) {
			return MoveCache.getMove(head(squares), last(squares));
		}
		else if (encodedMove.matches(ReaderRegex.EPMOVE)) {
			return new EnpassantMove(head(squares), last(squares));
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
}
