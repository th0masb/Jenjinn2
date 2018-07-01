/**
 *
 */
package jenjinn.engine.parseutils;

import static java.util.Arrays.asList;
import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.moves.CastleMove;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnpassantMove;
import jenjinn.engine.moves.PromotionMove;
import jenjinn.engine.moves.PromotionResult;
import jenjinn.engine.moves.StandardMove;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.iterators.misc.Pair;
import xawd.jflow.utilities.StringUtils;

/**
 * @author ThomasB
 *
 */
public final class ShorthandMoveParser
{
	private ShorthandMoveParser()
	{
	}

	public static List<ChessMove> parse(final String encoded)
	{
		final String ec = encoded.trim().toUpperCase();
		if (!ec.matches(CommonRegex.SHORTHAND_MOVE)) {
			throw new IllegalArgumentException(encoded);
		}
		switch (ec.charAt(0)) {
		case 'S':
			return parseStandardMoves(ec);
		case 'P':
			return parsePromotionMoves(ec);
		case 'E':
			return asList(parseEnpassantMove(ec));
		case 'C':
			return parseCastlingMoves(ec);
		default:
			throw new RuntimeException();
		}
	}

	private static List<ChessMove> parseStandardMoves(final String ec)
	{
		final String mtarg = CommonRegex.MULTI_TARGET, cord = CommonRegex.CORD;
		if (ec.matches("S\\[(" + mtarg + "|" + cord + ")\\]")) {
			final Pair<BoardSquare, Iterable<BoardSquare>> moves = parseMultiMove(ec.substring(2, ec.length() - 1));
			return Iterate.over(moves.second())
					.map(target -> new StandardMove(moves.first(), target))
					.filterAndCastTo(ChessMove.class)
					.toList();
		}
		else if (ec.matches("S\\[(" + CommonRegex.DOUBLE_SQUARE + ")\\]")) {
			final List<BoardSquare> squares = Iterate.over(StringUtils.getAllMatches(ec, CommonRegex.SINGLE_SQUARE))
					.map(s -> BoardSquare.valueOf(s.toUpperCase()))
					.toList();

			return asList(new StandardMove(head(squares), tail(squares)));
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static Pair<BoardSquare, Iterable<BoardSquare>> parseMultiMove(final String ec)
	{
		if (ec.matches(CommonRegex.CORD)) {
			final List<BoardSquare> squares = CordParser.parse(ec);
			return Pair.of(head(squares), drop(1, squares));
		}
		else if (ec.matches(CommonRegex.MULTI_TARGET)) {
			final List<BoardSquare> squares = Iterate.over(getAllMatches(ec, CommonRegex.SINGLE_SQUARE))
					.map(String::toUpperCase)
					.map(BoardSquare::valueOf)
					.toList();
			return Pair.of(head(squares), drop(1, squares));
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static List<ChessMove> parsePromotionMoves(final String ec)
	{
		final String mtarg = CommonRegex.MULTI_TARGET, cord = CommonRegex.CORD;
		final String result = StringUtils.findLastMatch(ec, "[NBRQ]")
				.orElseThrow(() -> new IllegalArgumentException(ec));
		if (ec.matches("P\\[(" + mtarg + "|" + cord + ") " + result + "\\]")) {
			final Pair<BoardSquare, Iterable<BoardSquare>> moves = parseMultiMove(ec.substring(2, ec.length() - 3));
			return Iterate.over(moves.second())
					.map(target -> new PromotionMove(moves.first(), target, PromotionResult.valueOf(result)))
					.filterAndCastTo(ChessMove.class)
					.toList();
		}
		else if (ec.matches("P\\[(" + CommonRegex.DOUBLE_SQUARE + ") " + result + "\\]")) {
			final List<BoardSquare> squares = Iterate.over(StringUtils.getAllMatches(ec, CommonRegex.SINGLE_SQUARE))
					.map(s -> BoardSquare.valueOf(s.toUpperCase()))
					.toList();

			return asList(new PromotionMove(head(squares), tail(squares), PromotionResult.valueOf(result)));
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static List<ChessMove> parseCastlingMoves(final String ec)
	{
		final String cz = CommonRegex.CASTLE_ZONE;
		if (ec.matches("[cC]\\[( *" + cz + " *)+\\]")) {
			return Iterate.over(getAllMatches(ec, cz))
					.map(CastleZone::fromSimpleIdentifier)
					.map(CastleMove::new)
					.filterAndCastTo(ChessMove.class)
					.toList();
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}

	private static ChessMove parseEnpassantMove(final String ec)
	{
		final String sq = CommonRegex.SINGLE_SQUARE;
		if (ec.matches("[eE]\\[ *" + sq + " +" + sq + " *\\]")) {
			final List<BoardSquare> sqMatches = Iterate.over(getAllMatches(ec, sq))
					.map(String::toUpperCase)
					.map(BoardSquare::valueOf)
					.toList();
			return new EnpassantMove(head(sqMatches), tail(sqMatches));
		}
		else {
			throw new IllegalArgumentException(ec);
		}
	}
}
