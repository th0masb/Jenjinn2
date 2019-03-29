package com.github.maumay.jenjinn.parseutils;

import static com.github.maumay.jenjinn.base.Square.A1;
import static com.github.maumay.jenjinn.base.Square.A2;
import static com.github.maumay.jenjinn.base.Square.A3;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jenjinn.base.CastleZone;
import com.github.maumay.jenjinn.moves.CastleMove;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jenjinn.moves.EnpassantMove;
import com.github.maumay.jenjinn.moves.PromotionMove;
import com.github.maumay.jenjinn.moves.PromotionResult;
import com.github.maumay.jenjinn.moves.StandardMove;
import com.github.maumay.jflow.iterators.Iter;
import com.github.maumay.jflow.iterators.RichIterator;

/**
 * @author ThomasB
 */
class ShorthandMoveParserTest
{
	@ParameterizedTest
	@MethodSource
	void testMoveConstruction(String encodedMove, List<StandardMove> expectedMoves)
	{
		Set<ChessMove> upcast = Iter.over(expectedMoves).cast(ChessMove.class).toSet();
		assertEquals(upcast, ShorthandMoveParser.parse(encodedMove).toSet());
	}

	static RichIterator<Arguments> testMoveConstruction()
	{
		return Iter.over(
				Arguments.of("s[a1->a3]",
						asList(new StandardMove(A1, A2), new StandardMove(A1, A3))),
				Arguments.of("S[a1->{ a2 a3  }]",
						asList(new StandardMove(A1, A2), new StandardMove(A1, A3))),

				Arguments.of("p[a1->a3 R]",
						asList(new PromotionMove(A1, A2, PromotionResult.R),
								new PromotionMove(A1, A3, PromotionResult.R))),
				Arguments.of("P[a1->{ a2 a3  } Q]",
						asList(new PromotionMove(A1, A2, PromotionResult.Q),
								new PromotionMove(A1, A3, PromotionResult.Q))),

				Arguments.of("c[ wq bk]",
						asList(new CastleMove(CastleZone.WHITE_QUEENSIDE),
								new CastleMove(CastleZone.BLACK_KINGSIDE))),
				Arguments.of("c[wk]", asList(new CastleMove(CastleZone.WHITE_KINGSIDE))),

				Arguments.of("e[a1 a2]", asList(new EnpassantMove(A1, A2))),
				Arguments.of("E[a1 a2]", asList(new EnpassantMove(A1, A2))));
	}

	@ParameterizedTest
	@MethodSource
	void testMoveConstructionFailure(String encoded)
	{
		assertThrows(IllegalArgumentException.class,
				() -> ShorthandMoveParser.parse(encoded));
	}

	static RichIterator<Arguments> testMoveConstructionFailure()
	{
		return Iter.over(Arguments.of("S [a1->a3]"), Arguments.of("S[a1 ->a3]"),
				Arguments.of("S[a1->k3]"), Arguments.of("S[a1->{}]"),

				Arguments.of("P [a1->]"), Arguments.of("p[]"), Arguments.of("P[a1->k3]"),
				Arguments.of("Pa1->{e4}]"),

				Arguments.of("C [wq]"), Arguments.of("c[we]"), Arguments.of("C[a1->a2]"),
				Arguments.of("c[]"),

				Arguments.of("E [a4 a5]"), Arguments.of("e[a4]"),
				Arguments.of("e[a1->a2]"), Arguments.of("E[]"));
	}
}
