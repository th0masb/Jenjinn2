/**
 *
 */
package jenjinn.parseutils;

import jenjinn.base.CastleZone;
import jenjinn.base.Square;
import jenjinn.moves.CastleMove;
import jenjinn.moves.ChessMove;
import jenjinn.moves.EnpassantMove;
import jenjinn.moves.PromotionMove;
import jenjinn.moves.PromotionResult;
import jenjinn.moves.StandardMove;
import jenjinn.pgn.CommonRegex;
import jflow.iterators.misc.Pair;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

/**
 * @author ThomasB
 *
 */
public final class ShorthandMoveParser
{
	private ShorthandMoveParser()
	{
	}

	public static Seq<ChessMove> parse(String encoded)
	{
		String ec = encoded.trim().toUpperCase();
		if (!ec.matches(CommonRegex.SHORTHAND_MOVE)) {
			throw new IllegalArgumentException(encoded);
		}
		switch (ec.charAt(0)) {
		case 'S':
			return parseStandardMoves(ec);
		case 'P':
			return parsePromotionMoves(ec);
		case 'E':
			return Seq.of(parseEnpassantMove(ec));
		case 'C':
			return parseCastlingMoves(ec);
		default:
			throw new RuntimeException();
		}
	}

	private static Seq<ChessMove> parseStandardMoves(String ec)
	{
		String mtarg = CommonRegex.MULTI_TARGET, cord = CommonRegex.CORD;
		if (ec.matches("S\\[(" + mtarg + "|" + cord + ")\\]")) {
			Pair<Square, Seq<Square>> moves = parseMultiMove(ec.substring(2, ec.length() - 1));
			return moves._2.flow()
					.map(target -> new StandardMove(moves._1, target))
					.castTo(ChessMove.class)
					.toSeq();
		}
		else if (ec.matches("S\\[(" + CommonRegex.DOUBLE_SQUARE + ")\\]")) {
			Seq<Square> squares = Strings.allMatches(ec, CommonRegex.SINGLE_SQUARE)
					.map(s -> Square.valueOf(s.toUpperCase()))
					.toSeq();

			return Seq.of(new StandardMove(squares.head(), squares.last()));
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static Seq<ChessMove> parsePromotionMoves(String ec)
	{
		String mtarg = CommonRegex.MULTI_TARGET, cord = CommonRegex.CORD;
		String result = Strings.lastMatch(ec, "[NBRQ]")
				.orElseThrow(() -> new IllegalArgumentException(ec));
		if (ec.matches("P\\[(" + mtarg + "|" + cord + ") " + result + "\\]")) {
			Pair<Square, Seq<Square>> moves = parseMultiMove(ec.substring(2, ec.length() - 3));
			return moves._2.flow()
					.map(target -> new PromotionMove(moves._1, target, PromotionResult.valueOf(result)))
					.castTo(ChessMove.class)
					.toSeq();
		}
		else if (ec.matches("P\\[(" + CommonRegex.DOUBLE_SQUARE + ") " + result + "\\]")) {
			Seq<Square> squares = Strings.allMatches(ec, CommonRegex.SINGLE_SQUARE)
					.map(s -> Square.valueOf(s.toUpperCase()))
					.toSeq();

			return Seq.of(new PromotionMove(squares.head(), squares.last(), PromotionResult.valueOf(result)));
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}
	
	private static Pair<Square, Seq<Square>> parseMultiMove(String ec)
	{
		if (ec.matches(CommonRegex.CORD)) {
			Seq<Square> squares = CordParser.parse(ec);
			return Pair.of(squares.head(), squares.drop(1));
		}
		else if (ec.matches(CommonRegex.MULTI_TARGET)) {
			Seq<Square> squares = Strings.allMatches(ec, CommonRegex.SINGLE_SQUARE)
					.map(String::toUpperCase)
					.map(Square::valueOf)
					.toSeq();
			return Pair.of(squares.head(), squares.drop(1));
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static Seq<ChessMove> parseCastlingMoves(String ec)
	{
		String cz = CommonRegex.CASTLE_ZONE;
		if (ec.matches("[cC]\\[( *" + cz + " *)+\\]")) {
			return Strings.allMatches(ec, cz)
					.map(CastleZone::fromSimpleIdentifier)
					.map(CastleMove::new)
					.castTo(ChessMove.class)
					.toSeq();
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static ChessMove parseEnpassantMove(String ec)
	{
		String sq = CommonRegex.SINGLE_SQUARE;
		if (ec.matches("[eE]\\[ *" + sq + " +" + sq + " *\\]")) {
			Seq<Square> sqMatches = Strings.allMatches(ec, sq)
					.map(String::toUpperCase)
					.map(Square::valueOf)
					.toSeq();
			return new EnpassantMove(sqMatches.head(), sqMatches.last());
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}
}
