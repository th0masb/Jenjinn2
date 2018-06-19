/**
 *
 */
package jenjinn.engine.boardstate.legalmoves;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.moves.ChessMove;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class LegalMovesGenerationTest
{
	@ParameterizedTest
	@MethodSource
	void test(final BoardState state, final Set<ChessMove> expectedMoves, final Set<ChessMove> expectedAttacks)
	{
		final Set<ChessMove> actualMoves = LegalMoves.getMoves(state).toSet();
		assertEquals(expectedMoves, actualMoves, formatDifferences(expectedMoves, actualMoves));

		final Set<ChessMove> actualAttacks = LegalMoves.getAttacks(state).toSet();
		assertEquals(expectedAttacks, actualAttacks, formatDifferences(expectedAttacks, actualAttacks));
	}

	private String formatDifferences(Set<ChessMove> expectedMoves, Set<ChessMove> actualMoves)
	{
		final Set<ChessMove> expectedcpy = new HashSet<>(expectedMoves);
		expectedcpy.removeAll(actualMoves);
		final List<String> missingMoves = Iterate.over(expectedcpy).map(ChessMove::toString).toList();
		missingMoves.sort(Comparator.naturalOrder());

		final StringBuilder sb = new StringBuilder("Moves which should have been calculated:\n")
				.append(missingMoves)
				.append(System.lineSeparator());

		final Set<ChessMove> actualcpy = new HashSet<>(actualMoves);
		actualcpy.removeAll(expectedMoves);
		final List<String> addedMoves = Iterate.over(actualcpy).map(ChessMove::toString).toList();
		addedMoves.sort(Comparator.naturalOrder());

		return sb.append("Moves which should not have been calculated:\n")
				.append(addedMoves)
				.append(System.lineSeparator())
				.toString();
	}

	static Flow<Arguments> test()
	{
		return Iterate.over("case001").map(TestFileParser::parse);
	}
}
