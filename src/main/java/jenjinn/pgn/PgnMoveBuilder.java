/**
 *
 */
package jenjinn.pgn;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import jenjinn.base.CastleZone;
import jenjinn.base.Square;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.DetailedPieceLocations;
import jenjinn.boardstate.calculators.LegalMoves;
import jenjinn.moves.CastleMove;
import jenjinn.moves.ChessMove;
import jenjinn.moves.PromotionMove;
import jenjinn.moves.PromotionResult;
import jenjinn.pieces.ChessPieces;
import jflow.iterators.factories.Iter;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

/**
 * @author ThomasB
 *
 */
public final class PgnMoveBuilder
{
	/**
	 * Maps PGN identifiers to piece ordinals mod 6.
	 */
	private static final Map<Character, Integer> CHARACTER_PIECE_MAP = Iter.overInts('N', 'B', 'R', 'Q', 'K')
			.zipWith(ChessPieces.WHITE.flow().drop(1))
			.toMap(p -> Character.valueOf((char) p._i), p -> p._o.ordinal());


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

		Seq<Square> encodedSquares = Strings.allMatches(mc, SQUARE)
				.map(String::toUpperCase)
				.map(Square::valueOf)
				.toSeq();

		if (encodedSquares.size() == 1) {
			Square target = encodedSquares.last();
			Seq<String> files = Strings.allMatches(mc, FILE).toSeq(), ranks = Strings.allMatches(mc, RANK).toSeq();
			char pieceIdentifier = Strings.firstMatch(mc, PIECE).orElse("P").charAt(0);
			int pieceOrdinalMod6 = CHARACTER_PIECE_MAP.getOrDefault(pieceIdentifier, 0);
			DetailedPieceLocations plocs = state.getPieceLocations();
			Seq<ChessMove> candidates = Iter.over(legalMoves)
					.filter(mv -> mv.getTarget() == target && (plocs.getPieceAt(mv.getSource()).ordinal() % 6) == pieceOrdinalMod6)
					.toSeq();

			if (candidates.size() == 1) {
				return candidates.head();
			}
			else if (files.size() == 2) {
				char sourceFile = files.head().toUpperCase().charAt(0);
				return candidates.findFirst(mv -> mv.getSource().name().charAt(0) == sourceFile)
						.orElseThrow(() -> new BadPgnException(mc + ", " + sourceFile + ", " + candidates));
			}
			else if (ranks.size() == 2) {
				char sourceRank = ranks.head().charAt(0);
				return candidates.findFirst(mv -> mv.getSource().name().charAt(1) == sourceRank)
						.orElseThrow(() -> new BadPgnException(mc + ", " + sourceRank + ", " + candidates));
			}
			else {
				throw exSupplier.get();
			}
		}
		else if (encodedSquares.size() == 2) {
			Square source = encodedSquares.head(), target = encodedSquares.last();
			return Iter.over(legalMoves)
					.filter(mv -> mv.getSource() == source && mv.getTarget() == target)
					.nextOption().orElseThrow(exSupplier);
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
		String encodedTarget = Strings.firstMatch(mc, SQUARE).orElseThrow(exSupplier);
		Square target = Square.valueOf(encodedTarget.toUpperCase());
		Seq<PromotionMove> candidates = Iter.over(legalMoves)
				.castTo(PromotionMove.class)
				.filter(mv -> mv.getTarget().equals(target) && mv.getPromotionResult().equals(piece))
				.toSeq();

		if (candidates.size() == 1) {
			return candidates.head();
		}
		else if (candidates.size() == 2) {
			char file = mc.toUpperCase().charAt(0);
			return candidates.findFirst(mv -> mv.getSource().name().charAt(0) == file)
					.orElseThrow(exSupplier);
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
