/**
 *
 */
package jenjinn.engine.moves;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.StringUtils.findLastMatch;

import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.MoveReversalData;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import xawd.jflow.utilities.Optionals;
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
	 * {@linkplain MoveReversalData} instance. Upon mutation of the state of the
	 * board in this forward direction a {@linkplain ChessMove} <b>must update
	 * all</b> the parameters in the {@linkplain MoveReversalData} instance.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void makeMove(BoardState state, MoveReversalData unmakeDataStore);

	default void makeMove(final BoardState state)
	{
		makeMove(state, new MoveReversalData());
	}

	/**
	 * Using the supplied {@linkplain MoveReversalData} to mutate the state of the
	 * parameter {@linkplain BoardState} instance to reverse this move.
	 *
	 * @param state
	 * @param unmakeDataStore
	 */
	void reverseMove(BoardState state, MoveReversalData unmakeDataStore);

	/**
	 * @param repr
	 *            A string encoding a chess move, it must be the same as the output
	 *            of {@link #toString()} or {@link #toCompactString()} of one of the
	 *            concrete subclasses of this interface.
	 * @return the decoded move.
	 */
	static ChessMove decode(final String repr)
	{
		final String explicitStandardEnpassantRx = "([SE][a-z]+Move\\[source=[a-h][1-8]\\|target=[a-h][1-8]\\])";
		final String compactStandardEnpassantRx = "([SE]([a-h][1-8]){2})";
		final String standardEnpassantRx = "^" + explicitStandardEnpassantRx + "|" + compactStandardEnpassantRx + "$";

		final String explicitCastleRx = "(CastleMove\\[zone=((wk)|(wq)|(bk)|(bq))\\])";
		final String compactCastleRx = "((wk)|(wq)|(bk)|(bq))";
		final String castleMoveRx = "^" + explicitCastleRx + "|" + compactCastleRx + "$";

		final String explicitPromotionRx = "(PromotionMove\\[source=[a-h][1-8]\\|target=[a-h][1-8]\\|result=[NBRQ]\\])";
		final String compactPromotionRx = "(P([a-h][1-8]){2}[NBRQ])";
		final String promotionRx = "^" + explicitPromotionRx + "|" + compactPromotionRx + "$";

		if (repr.matches(standardEnpassantRx)) {
			final List<String> squares = StringUtils.getAllMatches(repr, "[a-h][1-8]");
			final BoardSquare source = BoardSquare.valueOf(head(squares).toUpperCase());
			final BoardSquare target = BoardSquare.valueOf(tail(squares).toUpperCase());
			final char firstChar = repr.charAt(0);
			switch (firstChar) {
			case 'S':
				return new StandardMove(source, target);
			case 'E':
				return new EnpassantMove(source, target);
			default:
				throw new RuntimeException();
			}
		} else if (repr.matches(promotionRx)) {
			final List<String> squares = StringUtils.getAllMatches(repr, "[a-h][1-8]");
			final BoardSquare source = BoardSquare.valueOf(head(squares).toUpperCase());
			final BoardSquare target = BoardSquare.valueOf(tail(squares).toUpperCase());
			final PromotionResult result = PromotionResult.valueOf(Optionals.getOrError(findLastMatch(repr, "[NBRQ]")));
			return new PromotionMove(source, target, result);
		} else if (repr.matches(castleMoveRx)) {
			final String zoneId = StringUtils.findFirstMatch(repr, "(wk)|(wq)|(bk)|(bq)").get();
			return new CastleMove(CastleZone.fromSimpleIdentifier(zoneId));
		} else {
			throw new IllegalArgumentException(repr);
		}
	}
}
