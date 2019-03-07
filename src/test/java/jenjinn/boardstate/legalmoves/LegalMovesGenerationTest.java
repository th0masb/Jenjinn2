/**
 *
 */
package jenjinn.boardstate.legalmoves;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.calculators.LegalMoves;
import jenjinn.moves.ChessMove;
import jflow.iterators.factories.Iter;
import jflow.iterators.factories.IterRange;
import jflow.iterators.factories.Repeatedly;
import jflow.iterators.misc.Strings;

/**
 * @author ThomasB
 */
class LegalMovesGenerationTest
{
	@ParameterizedTest
	@MethodSource
	void test(BoardState state, Set<ChessMove> expectedMoves, Set<ChessMove> expectedAttacks)
	{
		Set<ChessMove> actualMoves = LegalMoves.getAllMoves(state).toSet();
		assertEquals(expectedMoves, actualMoves, formatDifferences(expectedMoves, actualMoves));

		Set<ChessMove> actualAttacks = LegalMoves.getAttacks(state).toSet();
		assertEquals(expectedAttacks, actualAttacks, formatDifferences(expectedAttacks, actualAttacks));
	}

	private String formatDifferences(Set<ChessMove> expectedMoves, Set<ChessMove> actualMoves)
	{
		Set<ChessMove> expectedcpy = new HashSet<>(expectedMoves);
		expectedcpy.removeAll(actualMoves);
		List<String> missingMoves = Iter.over(expectedcpy).map(ChessMove::toString).toList();
		missingMoves.sort(Comparator.naturalOrder());

		StringBuilder sb = new StringBuilder("Moves which should have been calculated:\n")
				.append(missingMoves)
				.append(System.lineSeparator());

		Set<ChessMove> actualcpy = new HashSet<>(actualMoves);
		actualcpy.removeAll(expectedMoves);
		List<String> addedMoves = Iter.over(actualcpy).map(ChessMove::toString).toList();
		addedMoves.sort(Comparator.naturalOrder());

		return sb.append("Moves which should not have been calculated:\n")
				.append(addedMoves)
				.append(System.lineSeparator())
				.toString();
	}

	static Iterator<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return IterRange.between(1, 11).mapToObject(i -> "case" + pad(i)).map(parser::parse);
	}

	static String pad(int caseNumber) {
		String caseString = Strings.$(caseNumber);
		return Repeatedly.cycle("0")
				.take(3 - caseString.length())
				.append(caseString)
				.fold("", (a, b) -> a + b);
	}
}
