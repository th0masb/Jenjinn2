/**
 *
 */
package jenjinn.engine.moves;

import static jenjinn.engine.stringutils.StringifyBoard.formatGrids;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.stringutils.VisualGridGenerator;

/**
 * @author t
 *
 */
public abstract class AbstractBoardStateTest
{
	public BoardState constructBoardState(List<String> lines)
	{
		throw new RuntimeException();
	}

	public void assertBoardStatesAreEqual(BoardState constraint, BoardState toTest)
	{
		assertEquals(constraint.getActiveSide(), toTest.getActiveSide());
		assertEquals(constraint.getEnPassantSquare(), toTest.getEnPassantSquare());
		assertEquals(constraint.getDevelopedPieces(), toTest.getDevelopedPieces());
		assertEquals(constraint.getHalfMoveClock(), toTest.getHalfMoveClock());
		assertEquals(constraint.getCastlingStatus(), toTest.getCastlingStatus());
		assertEquals(constraint.getHashCache(), toTest.getHashCache());
		assertEquals(constraint.getPieceLocations(), toTest.getPieceLocations(), formatPieceLocationsErrorOutput(constraint, toTest));
	}

	private String formatPieceLocationsErrorOutput(BoardState constraint, BoardState toTest)
	{
		return new StringBuilder()
				.append(formatGrids(VisualGridGenerator.from(constraint.getPieceLocations())))
				.append(System.lineSeparator())
				.append(System.lineSeparator())
				.append(formatGrids(VisualGridGenerator.from(constraint.getPieceLocations())))
				.toString();
	}
}
