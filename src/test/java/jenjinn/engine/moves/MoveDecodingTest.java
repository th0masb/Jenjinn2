/**
 *
 */
package jenjinn.engine.moves;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
class MoveDecodingTest
{
	@ParameterizedTest
	@MethodSource
	void test(final String explicitEncoding, final String compactEncoding, final ChessMove expectedMove)
	{
		assertEquals(expectedMove, ChessMove.decode(explicitEncoding));
		assertEquals(expectedMove, ChessMove.decode(compactEncoding));
	}

	static Flow<Arguments> test()
	{
		return Iterate.over(
				Arguments.of("StandardMove[source=a3|target=a4]", "Sa3a4", new StandardMove(BoardSquare.A3, BoardSquare.A4)),
				Arguments.of("EnpassantMove[source=a3|target=a4]", "Ea3a4", new EnpassantMove(BoardSquare.A3, BoardSquare.A4)),
				Arguments.of("PromotionMove[source=a3|target=a4]", "Pa3a4", new PromotionMove(BoardSquare.A3, BoardSquare.A4)),
				Arguments.of("CastleMove[zone=wq]", "wq", new CastleMove(CastleZone.WHITE_QUEENSIDE))
				);
	}
}
