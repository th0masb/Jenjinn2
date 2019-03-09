/**
 *
 */
package com.github.maumay.jenjinn.entity;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import com.github.maumay.jenjinn.base.CastleZone;
import com.github.maumay.jenjinn.base.FileUtils;
import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jenjinn.moves.EnpassantMove;
import com.github.maumay.jenjinn.moves.MoveCache;
import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.vec.Vec;

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
		src = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream(absname)));
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
				} else {
					line = src.readLine();
				}
			}
			return Optional.empty();
		} else {
			throw new IllegalStateException();
		}
	}

	private Optional<ChessMove> extractMove(String line, long positionHash)
	{
		for (String positionWithMove : Strings.allMatches(line, ReaderRegex.POS_AND_MOVE)
				.toList()) {
			String pos = Strings.firstMatch(positionWithMove, ReaderRegex.POSITION).get();
			if (Long.parseUnsignedLong(pos, 16) == positionHash) {
				String encodedMove = Strings
						.firstMatch(positionWithMove, ReaderRegex.MOVE).get();
				return Optional.of(decodeMove(encodedMove));
			}
		}
		return Optional.empty();
	}

	private ChessMove decodeMove(String encodedMove)
	{
		Vec<Square> squares = Strings.allMatches(encodedMove, ReaderRegex.SQUARE)
				.map(Square::valueOf).toVec();

		if (encodedMove.matches(ReaderRegex.SMOVE)) {
			return MoveCache.getMove(squares.head(), squares.last());
		} else if (encodedMove.matches(ReaderRegex.EPMOVE)) {
			return new EnpassantMove(squares.head(), squares.last());
		} else if (encodedMove.matches(ReaderRegex.CASTLEMOVE)) {
			return MoveCache.getMove(CastleZone.fromSimpleIdentifier(encodedMove));
		} else {
			throw new AssertionError(encodedMove);
		}
	}

	@Override
	public void close() throws IOException
	{
		src.close();
	}
}
