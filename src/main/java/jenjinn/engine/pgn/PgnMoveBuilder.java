/**
 *
 */
package jenjinn.engine.pgn;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.Strings.findFirstMatch;
import static xawd.jflow.utilities.Strings.getAllMatches;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.CastleZone;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.moves.CastleMove;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.PromotionMove;
import jenjinn.engine.moves.PromotionResult;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.utilities.Optionals;
import xawd.jflow.utilities.Strings;

/**
 * @author ThomasB
 *
 */
public final class PgnMoveBuilder
{
	/**
	 * Maps PGN identifiers to piece ordinals mod 6.
	 */
	private static final Map<Character, Integer> CHARACTER_PIECE_MAP = Iterate.overInts('N', 'B', 'R', 'Q', 'K')
			.zipWith(Iterate.over(ChessPieces.white()).drop(1))
			.toMap(p -> Character.valueOf((char) p.getInt()), p -> p.getElement().ordinal());


	private static final String FILE = "([a-h])", RANK = "([1-8])";
	private static final String SQUARE = "(" + FILE + RANK +")";
	private static final String PIECE = "(N|B|R|Q|K)";

	public static final String CASTLE_MOVE = "(O-O(-O)?)";
	public static final String PROMOTION_MOVE = "(([a-h]x)?" + SQUARE + "=[NBRQ]" + ")";
	public static final String STANDARD_MOVE = "(" + PIECE + "?([a-h]|[1-8]|([a-h][1-8]))?x?" + SQUARE + "(?!=))";
	public static final String MOVE = "(" + STANDARD_MOVE + "|" + PROMOTION_MOVE + "|" + CASTLE_MOVE + ")";

	private PgnMoveBuilder()
	{
	}

	public static void main(String[] args)
	{
		final String pgn = "1.e4 e5 2.Bc4 Bc5 3.Qe2 Nf6 4.d3 Nc6 5.c3 Ne7 6.f4 exf4 7.d4 Bb6 8.Bxf4 d6 9.Bd3 Ng6 10.Be3 O-O 11.h3 Re8 12.Nd2 Qe7 13.O-O-O c5 14.Kb1 cxd4 15.cxd4 a5 16.Ngf3 Bd7 17.g4 h6 18.Rdg1 a4 19.g5 hxg5 20.Bxg5 a3 21.b3 Bc6 22.Rg4 Ba5 23.h4 Bxd2 24.Nxd2 Ra5 25.h5 Rxg5 26.Rxg5 Nf4 27.Qf3 Nxd3 28.d5 Nxd5 29.Rhg1 Nc3+ 30.Ka1 Bxe4 31.Rxg7+ Kh8 32.Qg3 Bg6 33.hxg6 Qe1+ 34.Rxe1 Rxe1+ 35.Qxe1 Nxe1 36.Rh7+ Kg8 37.gxf7+ Kxh7 38.f8=N+ Kh6 39.Nb1 Nc2+  0-1";

		System.out.println(Strings.getAllMatches(pgn, MOVE));
	}

	public static ChessMove convertPgnCommand(final BoardState currentState, final String moveCommand) throws BadPgnException
	{
		final Set<ChessMove> legalMoves = LegalMoves.getAllMoves(currentState).toSet();

		if (moveCommand.matches(CASTLE_MOVE)) {
			return decodeCastleMove(currentState, moveCommand, legalMoves);
		}
		else if (moveCommand.matches(PROMOTION_MOVE)) {
			return decodePromotionMove(currentState, moveCommand, legalMoves);
		}
		else if (moveCommand.matches(STANDARD_MOVE)) {
			return decodeStandardMove(currentState, moveCommand, legalMoves);
		}
		else {
			throw new BadPgnException(moveCommand);
		}
	}

	private static ChessMove decodeStandardMove(BoardState state, String moveCommand, Set<ChessMove> legalMoves) throws BadPgnException
	{
		final String mc = moveCommand;
		final Supplier<BadPgnException> exSupplier = () -> new BadPgnException(moveCommand);

		final List<BoardSquare> encodedSquares = Iterate.over(getAllMatches(mc, SQUARE))
				.map(String::toUpperCase)
				.map(BoardSquare::valueOf)
				.toList();

		if (encodedSquares.size() == 1) {
			final BoardSquare target = tail(encodedSquares);
			final List<String> files = getAllMatches(mc, FILE), ranks = getAllMatches(mc, RANK);
			final char pieceIdentifier = findFirstMatch(mc, PIECE).orElse("P").charAt(0);
			final int pieceOrdinalMod6 = CHARACTER_PIECE_MAP.getOrDefault(pieceIdentifier, 0);
			final DetailedPieceLocations plocs = state.getPieceLocations();
			final List<ChessMove> candidates = Iterate.over(legalMoves)
					.filter(mv -> mv.getTarget() == target && (plocs.getPieceAt(mv.getSource()).ordinal() % 6) == pieceOrdinalMod6)
					.toList();

			if (candidates.size() == 1) {
				return head(candidates);
			}
			else if (files.size() == 2) {
				final char sourceFile = head(files).toUpperCase().charAt(0);
				return Iterate.over(candidates)
						.filter(mv -> mv.getSource().name().charAt(0) == sourceFile)
						.safeNext().orElseThrow(() -> new BadPgnException(mc + ", " + sourceFile + ", " + candidates));
			}
			else if (ranks.size() == 2) {
				final char sourceRank = head(ranks).charAt(0);
				return Iterate.over(candidates)
						.filter(mv -> mv.getSource().name().charAt(1) == sourceRank)
						.safeNext().orElseThrow(() -> new BadPgnException(mc + ", " + sourceRank + ", " + candidates));
			}
			else {
				throw exSupplier.get();
			}
		}
		else if (encodedSquares.size() == 2) {
			final BoardSquare source = head(encodedSquares), target = tail(encodedSquares);
			return Iterate.over(legalMoves)
					.filter(mv -> mv.getSource() == source && mv.getTarget() == target)
					.safeNext().orElseThrow(exSupplier);
		}
		else {
			throw exSupplier.get();
		}
	}

	private static ChessMove decodePromotionMove(BoardState state, String moveCommand, Set<ChessMove> legalMoves) throws BadPgnException
	{
		final String mc = moveCommand;
		final Supplier<BadPgnException> exSupplier = () -> new BadPgnException(moveCommand);

		final PromotionResult piece = PromotionResult.valueOf(Optionals.getOrError(Strings.findLastMatch(mc, "[NBRQ]")));
		final String encodedTarget = findFirstMatch(mc, SQUARE).orElseThrow(exSupplier);
		final BoardSquare target = BoardSquare.valueOf(encodedTarget.toUpperCase());
		final List<PromotionMove> candidates = Iterate.over(legalMoves)
				.filterAndCastTo(PromotionMove.class)
				.filter(mv -> mv.getTarget().equals(target) && mv.getPromotionResult().equals(piece))
				.toList();

		if (candidates.size() == 1) {
			return head(candidates);
		}
		else if (candidates.size() == 2) {
			final char file = mc.toUpperCase().charAt(0);
			return Iterate.over(candidates)
					.filter(mv -> mv.getSource().name().charAt(0) == file)
					.safeNext().orElseThrow(exSupplier);
		}
		else {
			throw exSupplier.get();
		}
	}

	private static ChessMove decodeCastleMove(BoardState state, String moveCommand, Set<ChessMove> legalMoves) throws BadPgnException
	{
		final String mc = moveCommand;
		final CastleZone kingSide = state.getActiveSide().isWhite()? CastleZone.WHITE_KINGSIDE : CastleZone.BLACK_KINGSIDE;
		final CastleZone queenSide = state.getActiveSide().isWhite()? CastleZone.WHITE_QUEENSIDE : CastleZone.BLACK_QUEENSIDE;
		final ChessMove mv = mc.matches("O-O")? new CastleMove(kingSide) : new CastleMove(queenSide);

		if (legalMoves.contains(mv)) {
			return mv;
		}
		else {
			throw new BadPgnException(moveCommand); //
		}
	}
}
