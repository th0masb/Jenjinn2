/**
 *
 */
package jenjinn.engine.boardstate;

import static jenjinn.engine.stringutils.StringifyBoard.formatGrids;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jenjinn.engine.stringutils.VisualGridGenerator;

/**
 * @author ThomasB
 *
 */
public interface BoardStateComparisonTest
{
	default void assertBoardStatesAreEqual(final BoardState constraint, final BoardState toTest)
	{
		assertEquals(constraint.getStateHasher(), toTest.getStateHasher());

		assertEquals(constraint.getHalfMoveClock(), toTest.getHalfMoveClock());

		assertEquals(
				constraint.getPieceLocations(),
				toTest.getPieceLocations(),
				formatGrids(VisualGridGenerator.from(constraint.getPieceLocations()))
				+ System.lineSeparator() + System.lineSeparator()
				+ formatGrids(VisualGridGenerator.from(constraint.getPieceLocations()))
				);

		assertEquals(constraint.getCastlingStatus(), toTest.getCastlingStatus());

	}


}
