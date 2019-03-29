/**
 *
 */
package com.github.maumay.jenjinn.parseutils;

import com.github.maumay.jenjinn.base.CastleZone;
import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.moves.CastleMove;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jenjinn.moves.EnpassantMove;
import com.github.maumay.jenjinn.moves.PromotionMove;
import com.github.maumay.jenjinn.moves.PromotionResult;
import com.github.maumay.jenjinn.moves.StandardMove;
import com.github.maumay.jenjinn.pgn.CommonRegex;
import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.utils.Tup;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 *
 */
public final class ShorthandMoveParser
{
	private ShorthandMoveParser()
	{
	}

	public static Vec<ChessMove> parse(String encoded)
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
			return Vec.of(parseEnpassantMove(ec));
		case 'C':
			return parseCastlingMoves(ec);
		default:
			throw new RuntimeException();
		}
	}

	private static Vec<ChessMove> parseStandardMoves(String ec)
	{
		String mtarg = CommonRegex.MULTI_TARGET, cord = CommonRegex.CORD;
		if (ec.matches("S\\[(" + mtarg + "|" + cord + ")\\]")) {
			Tup<Square, Vec<Square>> moves = parseMultiMove(
					ec.substring(2, ec.length() - 1));
			return moves._2.iter().map(target -> new StandardMove(moves._1, target))
					.cast(ChessMove.class).toVec();
		} else if (ec.matches("S\\[(" + CommonRegex.DOUBLE_SQUARE + ")\\]")) {
			Vec<Square> squares = Strings.allMatches(ec, CommonRegex.SINGLE_SQUARE)
					.map(s -> Square.valueOf(s.toUpperCase())).toVec();

			return Vec.of(new StandardMove(squares.head(), squares.last()));
		} else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static Vec<ChessMove> parsePromotionMoves(String ec)
	{
		String mtarg = CommonRegex.MULTI_TARGET, cord = CommonRegex.CORD;
		String result = Strings.lastMatch(ec, "[NBRQ]")
				.orElseThrow(() -> new IllegalArgumentException(ec));
		if (ec.matches("P\\[(" + mtarg + "|" + cord + ") " + result + "\\]")) {
			Tup<Square, Vec<Square>> moves = parseMultiMove(
					ec.substring(2, ec.length() - 3));
			return moves._2.iter()
					.map(target -> new PromotionMove(moves._1, target,
							PromotionResult.valueOf(result)))
					.cast(ChessMove.class).toVec();
		} else if (ec
				.matches("P\\[(" + CommonRegex.DOUBLE_SQUARE + ") " + result + "\\]")) {
			Vec<Square> squares = Strings.allMatches(ec, CommonRegex.SINGLE_SQUARE)
					.map(s -> Square.valueOf(s.toUpperCase())).toVec();

			return Vec.of(new PromotionMove(squares.head(), squares.last(),
					PromotionResult.valueOf(result)));
		} else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static Tup<Square, Vec<Square>> parseMultiMove(String ec)
	{
		if (ec.matches(CommonRegex.CORD)) {
			Vec<Square> squares = CordParser.parse(ec);
			return Tup.of(squares.head(), squares.drop(1));
		} else if (ec.matches(CommonRegex.MULTI_TARGET)) {
			Vec<Square> squares = Strings.allMatches(ec, CommonRegex.SINGLE_SQUARE)
					.map(String::toUpperCase).map(Square::valueOf).toVec();
			return Tup.of(squares.head(), squares.drop(1));
		} else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static Vec<ChessMove> parseCastlingMoves(String ec)
	{
		String cz = CommonRegex.CASTLE_ZONE;
		if (ec.matches("[cC]\\[( *" + cz + " *)+\\]")) {
			return Strings.allMatches(ec, cz).map(CastleZone::fromSimpleIdentifier)
					.map(CastleMove::new).cast(ChessMove.class).toVec();
		} else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static ChessMove parseEnpassantMove(String ec)
	{
		String sq = CommonRegex.SINGLE_SQUARE;
		if (ec.matches("[eE]\\[ *" + sq + " +" + sq + " *\\]")) {
			Vec<Square> sqMatches = Strings.allMatches(ec, sq).map(String::toUpperCase)
					.map(Square::valueOf).toVec();
			return new EnpassantMove(sqMatches.head(), sqMatches.last());
		} else {
			throw new IllegalArgumentException(ec);
		}
	}
}
