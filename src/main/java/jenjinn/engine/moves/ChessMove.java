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

	String toCompactString();

	/**
	 * Mutate the state of the parameter {@linkplain BoardState} according to this
	 * move. Store required data for reversing this move in the parameter
	 * {@linkplain DataForReversingMove} instance. Upon mutation of the state of the
	 * board in this forward direction a {@linkplain ChessMove} <b>must update
	 * all</b> the parameters in the {@linkplain DataForReversingMove} instance.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void makeMove(BoardState state, DataForReversingMove unmakeDataStore);

	default void makeMove(final BoardState state)
	{
		makeMove(state, new DataForReversingMove());
	}

	/**
	 * Using the supplied {@linkplain DataForReversingMove} to mutate the state of
	 * the parameter {@linkplain BoardState} instance to reverse this move.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void reverseMove(BoardState state, DataForReversingMove unmakeDataStore);

	/**
	 * @param repr
	 *            A string encoding a chess move, it must be the same as the output
	 *            of .toString() of one of the concrete subclasses of this
	 *            interface.
	 * @return the decoded move.
	 */
	static ChessMove decode(final String repr)
	{
		final String explicitNonCastleMoveRegex = "([SECP][a-z]+Move\\[source=[a-h][1-8]\\|target=[a-h][1-8]\\])";
		final String compactNonCastleMoveRegex = "([SECP][a-h][1-8][a-h][1-8])";
		final String nonCastleMoveRx = "^" + explicitNonCastleMoveRegex + "|" + compactNonCastleMoveRegex + "$";

		final String explicitCastleMoveRegex = "(CastleMove\\[zone=((wk)|(wq)|(bk)|(bq))\\])";
		final String compactCastleMoveRx = "((wk)|(wq)|(bk)|(bq))";
		final String castleMoveRx = "^" + explicitCastleMoveRegex + "|" + compactCastleMoveRx + "$";

		if (repr.matches(nonCastleMoveRx)) {
			final List<String> squares = StringUtils.getAllMatches(repr, "[a-h][1-8]");
			final BoardSquare source = BoardSquare.valueOf(head(squares).toUpperCase());
			final BoardSquare target = BoardSquare.valueOf(tail(squares).toUpperCase());
			final char firstChar = repr.charAt(0);
			switch (firstChar) {
			case 'S':
				return new StandardMove(source, target);
			case 'E':
				return new EnpassantMove(source, target);
			case 'P':
				return new PromotionMove(source, target);
			default:
				throw new RuntimeException();
			}
		} else if (repr.matches(castleMoveRx)) {
			final String zoneId = StringUtils.findFirstMatch(repr, "(wk)|(wq)|(bk)|(bq)").get();
			return new CastleMove(CastleZone.fromSimpleIdentifier(zoneId));
		} else {
			throw new IllegalArgumentException(repr);
		}
	}
}
