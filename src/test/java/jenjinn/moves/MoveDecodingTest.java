/**
 *
 */
package jenjinn.moves;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.base.CastleZone;
import jenjinn.base.Square;
import jenjinn.moves.CastleMove;
import jenjinn.moves.ChessMove;
import jenjinn.moves.EnpassantMove;
import jenjinn.moves.PromotionMove;
import jenjinn.moves.PromotionResult;
import jenjinn.moves.StandardMove;
import jflow.iterators.Flow;
import jflow.iterators.factories.Iter;

/**
 * @author ThomasB
 *
 */
class MoveDecodingTest
{
	@ParameterizedTest
	@MethodSource
	void test(String explicitEncoding, String compactEncoding, ChessMove expectedMove)
	{
		assertEquals(expectedMove, ChessMove.decode(explicitEncoding));
		assertEquals(expectedMove, ChessMove.decode(compactEncoding));
	}

	static Flow<Arguments> test()
	{
		return Iter.over(
				Arguments.of("StandardMove[source=a3|target=a4]", "Sa3a4", new StandardMove(Square.A3, Square.A4)),
				Arguments.of("EnpassantMove[source=a3|target=a4]", "Ea3a4", new EnpassantMove(Square.A3, Square.A4)),
				Arguments.of("PromotionMove[source=a3|target=a4|result=R]", "Pa3a4R", new PromotionMove(Square.A3, Square.A4, PromotionResult.R)),
				Arguments.of("CastleMove[zone=wq]", "wq", new CastleMove(CastleZone.WHITE_QUEENSIDE))
				);
	}
}
