/**
 *
 */
package jenjinn.moves;


import jenjinn.base.CastleZone;
import jenjinn.base.Square;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.MoveReversalData;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
public interface ChessMove
{
	Square getSource();

	Square getTarget();

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

	/**
	 * Mutate the state of the parameter board by performing this move without
	 * saving the reversal data. That is this method is irreversible.
	 * 
	 * @param state
	 */
	default void makeMove(BoardState state)
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
	 *            of {@link Object#toString()} or {@link #toCompactString()} of one of the
	 *            concrete subclasses of this interface.
	 * @return the decoded move.
	 */
	static ChessMove decode(String repr)
	{
		String explicitStandardEnpassantRx = "([SE][a-z]+Move\\[source=[a-h][1-8]\\|target=[a-h][1-8]\\])";
		String compactStandardEnpassantRx = "([SE]([a-h][1-8]){2})";
		String standardEnpassantRx = "^" + explicitStandardEnpassantRx + "|" + compactStandardEnpassantRx + "$";

		String explicitCastleRx = "(CastleMove\\[zone=((wk)|(wq)|(bk)|(bq))\\])";
		String compactCastleRx = "((wk)|(wq)|(bk)|(bq))";
		String castleMoveRx = "^" + explicitCastleRx + "|" + compactCastleRx + "$";

		String explicitPromotionRx = "(PromotionMove\\[source=[a-h][1-8]\\|target=[a-h][1-8]\\|result=[NBRQ]\\])";
		String compactPromotionRx = "(P([a-h][1-8]){2}[NBRQ])";
		String promotionRx = "^" + explicitPromotionRx + "|" + compactPromotionRx + "$";

		if (repr.matches(standardEnpassantRx)) {
			Seq<String> squares = Strings.allMatches(repr, "[a-h][1-8]").toSeq();
			Square source = Square.valueOf(squares.head().toUpperCase());
			Square target = Square.valueOf(squares.last().toUpperCase());
			char firstChar = repr.charAt(0);
			switch (firstChar) {
			case 'S':
				return new StandardMove(source, target);
			case 'E':
				return new EnpassantMove(source, target);
			default:
				throw new RuntimeException();
			}
		} else if (repr.matches(promotionRx)) {
			Seq<String> squares = Strings.allMatches(repr, "[a-h][1-8]").toSeq();
			Square source = Square.valueOf(squares.head().toUpperCase());
			Square target = Square.valueOf(squares.last().toUpperCase());
			PromotionResult result = PromotionResult.valueOf(Strings.lastMatch(repr, "[NBRQ]").get());
			return new PromotionMove(source, target, result);
		} else if (repr.matches(castleMoveRx)) {
			String zoneId = Strings.firstMatch(repr, "(wk)|(wq)|(bk)|(bq)").get();
			return new CastleMove(CastleZone.fromSimpleIdentifier(zoneId));
		} else {
			throw new IllegalArgumentException(repr);
		}
	}
}
