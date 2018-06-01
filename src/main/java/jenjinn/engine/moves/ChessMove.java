/**
 *
 */
package jenjinn.engine.moves;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;

import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import xawd.jflow.utilities.StringUtils;

/**
 * @author ThomasB
 */
public interface ChessMove
{
	BoardSquare getSource();

	BoardSquare getTarget();

	/**
	 * Mutate the state of the parameter {@linkplain BoardState} according
	 * to this move. Store required data for reversing this move in the parameter
	 * {@linkplain DataForReversingMove} instance. Upon mutation of the state of
	 * the board in this forward direction a {@linkplain ChessMove} <b>must
	 * update all</b> the parameters in the {@linkplain DataForReversingMove} instance.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void makeMove(BoardState state, DataForReversingMove unmakeDataStore);

	/**
	 * Using the supplied {@linkplain DataForReversingMove} to mutate the state of the
	 * parameter {@linkplain BoardState} instance to reverse this move.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void reverseMove(BoardState state, DataForReversingMove unmakeDataStore);

	/**
	 * @param repr - A string encoding a chess move, it must be the same as the output of .toString() of one of the
	 * concrete subclasses of this interface.
	 * @return the decoded move.
	 */
	static ChessMove decode(final String repr)
	{
		final String nonCastleMoveRegex = "^[SECP][a-z]+Move\\[source=[A-H][1-8]\\|target=[A-H][1-8]\\]$";
		final String castleMoveRegex = "^CastleMove\\[zone=(wk)|(wq)|(bk)|(bq)\\]$";

		if (repr.matches(nonCastleMoveRegex)) {
			final List<String> squares = StringUtils.getAllMatches(repr, "[A-H][1-8]");
			final BoardSquare source = BoardSquare.valueOf(head(squares)), target = BoardSquare.valueOf(tail(squares));
			final char firstChar = repr.charAt(0);
			switch (firstChar)  {
				case 'S':
					return new StandardMove(source, target);
				case 'E':
					return new EnpassantMove(source, target);
				case 'P':
					throw new RuntimeException();
				default:
					throw new RuntimeException();
			}
		}
		else if (repr.matches(castleMoveRegex)) {
			final String zoneId = StringUtils.findFirstMatch(repr, "(wk)|(wq)|(bk)|(bq)").get();
			final CastleZone matching = CastleZone.iterateAll().filter(zone -> zone.getSimpleIdentifier().equals(zoneId)).next();
			return new CastleMove(matching);
		}
		else {
			throw new IllegalArgumentException();
		}
	}
}
