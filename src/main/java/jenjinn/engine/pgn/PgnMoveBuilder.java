/**
 *
 */
package jenjinn.engine.pgn;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.StringUtils.findFirstMatch;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.moves.CastleMove;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.PromotionMove;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.utilities.StringUtils;

/**
 * @author ThomasB
 *
 */
public final class PgnMoveBuilder
{
	/**
	 * Maps PGN identifiers to piece ordinals mod 6.
	 */
	private static final Map<Character, Integer> CHARACTER_PIECE_MAP = Iterate.over('N', 'B', 'R', 'Q', 'K')
			.zipWith(Iterate.over(ChessPieces.white()).drop(1))
			.toMap(p -> Character.valueOf((char) p.getInt()), p -> p.getElement().ordinal());


	private static final String FILE = "([a-h])", RANK = "([1-8])";
	private static final String SQUARE = "(" + FILE + RANK +")";
	private static final String PIECE = "(N|B|R|Q|K)";
	private static final String CHECK = "(\\+|#)";

	public static final String KINGSIDE_CASTLE = "(O-O)", QUEENSIDE_CASTLE = "(O-O-O)";
	public static final String CASTLE_MOVE = "(" + KINGSIDE_CASTLE +"|" + QUEENSIDE_CASTLE + ")";
	public static final String PROMOTION_MOVE = "(([a-h]x)?" + SQUARE + "=Q" + ")";
	public static final String STANDARD_MOVE = "(" + PIECE + "?([a-h]|[1-8]|([a-h][1-8]))?x?" + SQUARE + ")";

	public static final String EXCLUDED_PROMOTION_MOVE = "(([a-h]x)?" + SQUARE + "=(N|B|R)" + CHECK + "?)";

	public static final String MOVE_LOOKBEHIND = "(?<=(\\.| {1,3}))";
	public static final String MOVE_LOOKAHEAD = "(?=(" + CHECK + "| {1,3}))";
	public static final String MOVE_EXTRACTOR = "(" + MOVE_LOOKBEHIND + "(" + STANDARD_MOVE + "|" + PROMOTION_MOVE + "|" + CASTLE_MOVE + ")" + MOVE_LOOKAHEAD + ")";

	public static final String GAME_START = "(^1\\." + STANDARD_MOVE + ")";
	public static final String GAME_TERMINATION = "(((1//-0)|(0\\-1)|(1/2\\-1/2)|(\\*))$)";

	private PgnMoveBuilder()
	{
	}

	public static ChessMove convertPgnCommand(final BoardState currentState, final String moveCommand) throws BadPgnException
	{
		final Supplier<BadPgnException> exSupplier = () -> new BadPgnException(moveCommand);
		final String mc = moveCommand.toUpperCase();
		final Set<ChessMove> legalMoves = LegalMoves.getMoves(currentState).toSet();

		if (mc.matches(CASTLE_MOVE)) {
			final CastleZone kingSide = currentState.getActiveSide().isWhite()? CastleZone.WHITE_KINGSIDE : CastleZone.BLACK_KINGSIDE;
			final CastleZone queenSide = currentState.getActiveSide().isWhite()? CastleZone.WHITE_QUEENSIDE : CastleZone.BLACK_QUEENSIDE;
			final ChessMove mv = mc.matches(KINGSIDE_CASTLE)? new CastleMove(kingSide) : new CastleMove(queenSide);
			if (!legalMoves.contains(mv)) {
				throw exSupplier.get();
			}
			return mv;
		}
		else if (mc.matches(PROMOTION_MOVE)) {
			final String encodedTarget = findFirstMatch(mc, SQUARE).orElseThrow(exSupplier);
			final BoardSquare target = BoardSquare.valueOf(encodedTarget.toUpperCase());
			final List<PromotionMove> candidates = Iterate.over(legalMoves)
					.filterAndCastTo(PromotionMove.class)
					.filter(mv -> mv.getTarget() == target)
					.toList();

			if (candidates.size() == 1) {
				return head(candidates);
			}
			else if (candidates.size() == 2) {
				final char file = mc.charAt(0);
				return Iterate.over(candidates)
						.filter(mv -> mv.getSource().name().charAt(0) == file)
						.safeNext().orElseThrow(exSupplier);
			}
			else {
				throw exSupplier.get();
			}
		}
		else if (mc.matches(STANDARD_MOVE)) {
			final List<BoardSquare> encodedSquares = Iterate.over(getAllMatches(mc, SQUARE))
					.map(String::toUpperCase)
					.map(BoardSquare::valueOf)
					.toList();

			if (encodedSquares.isEmpty()) {
				throw exSupplier.get();
			}
			else if (encodedSquares.size() == 1) {
				final BoardSquare target = tail(encodedSquares);
//				int pieceOrdinalMod6 = StringUtils.findFirstMatch(mc, PIECE).orElse("0);
			}
		}


		throw new RuntimeException();
	}

	public static void main(final String[] args)
	{
		System.out.println(StringUtils.getAllMatches("23.Qd3 Rae8 24.Rf2 Rxe3 25.Qxe3 Bd4 26.Qe6+ Qxe6 27.dxe6 Bxf2+ 28.Kxf2 Bd5+", MOVE_EXTRACTOR));

		System.out.println(StringUtils.getAllMatches("Qd3", PIECE));
	}
}
