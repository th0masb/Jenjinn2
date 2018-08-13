/**
 *
 */
package jenjinn.engine.pgn;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.last;
import static xawd.jflow.utilities.Strings.allMatches;
import static xawd.jflow.utilities.Strings.firstMatch;

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

	public static ChessMove convertPgnCommand(BoardState currentState, String moveCommand) throws BadPgnException
	{
		Set<ChessMove> legalMoves = LegalMoves.getAllMoves(currentState).toSet();

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
		String mc = moveCommand;
		Supplier<BadPgnException> exSupplier = () -> new BadPgnException(moveCommand);

		List<BoardSquare> encodedSquares = allMatches(mc, SQUARE)
				.map(String::toUpperCase)
				.map(BoardSquare::valueOf)
				.toList();

		if (encodedSquares.size() == 1) {
			BoardSquare target = last(encodedSquares);
			List<String> files = allMatches(mc, FILE).toList(), ranks = allMatches(mc, RANK).toList();
			char pieceIdentifier = firstMatch(mc, PIECE).orElse("P").charAt(0);
			int pieceOrdinalMod6 = CHARACTER_PIECE_MAP.getOrDefault(pieceIdentifier, 0);
			DetailedPieceLocations plocs = state.getPieceLocations();
			List<ChessMove> candidates = Iterate.over(legalMoves)
					.filter(mv -> mv.getTarget() == target && (plocs.getPieceAt(mv.getSource()).ordinal() % 6) == pieceOrdinalMod6)
					.toList();

			if (candidates.size() == 1) {
				return head(candidates);
			}
			else if (files.size() == 2) {
				char sourceFile = head(files).toUpperCase().charAt(0);
				return Iterate.over(candidates)
						.filter(mv -> mv.getSource().name().charAt(0) == sourceFile)
						.safeNext().orElseThrow(() -> new BadPgnException(mc + ", " + sourceFile + ", " + candidates));
			}
			else if (ranks.size() == 2) {
				char sourceRank = head(ranks).charAt(0);
				return Iterate.over(candidates)
						.filter(mv -> mv.getSource().name().charAt(1) == sourceRank)
						.safeNext().orElseThrow(() -> new BadPgnException(mc + ", " + sourceRank + ", " + candidates));
			}
			else {
				throw exSupplier.get();
			}
		}
		else if (encodedSquares.size() == 2) {
			BoardSquare source = head(encodedSquares), target = last(encodedSquares);
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
		String mc = moveCommand;
		Supplier<BadPgnException> exSupplier = () -> new BadPgnException(moveCommand);

		PromotionResult piece = PromotionResult.valueOf(Strings.lastMatch(mc, "[NBRQ]").get());
		String encodedTarget = firstMatch(mc, SQUARE).orElseThrow(exSupplier);
		BoardSquare target = BoardSquare.valueOf(encodedTarget.toUpperCase());
		List<PromotionMove> candidates = Iterate.over(legalMoves)
				.filterAndCastTo(PromotionMove.class)
				.filter(mv -> mv.getTarget().equals(target) && mv.getPromotionResult().equals(piece))
				.toList();

		if (candidates.size() == 1) {
			return head(candidates);
		}
		else if (candidates.size() == 2) {
			char file = mc.toUpperCase().charAt(0);
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
		String mc = moveCommand;
		CastleZone kingSide = state.getActiveSide().isWhite()? CastleZone.WHITE_KINGSIDE : CastleZone.BLACK_KINGSIDE;
		CastleZone queenSide = state.getActiveSide().isWhite()? CastleZone.WHITE_QUEENSIDE : CastleZone.BLACK_QUEENSIDE;
		ChessMove mv = mc.matches("O-O")? new CastleMove(kingSide) : new CastleMove(queenSide);

		if (legalMoves.contains(mv)) {
			return mv;
		}
		else {
			throw new BadPgnException(moveCommand); //
		}
	}
}
