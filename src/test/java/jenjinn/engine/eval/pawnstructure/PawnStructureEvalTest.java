/**
 *
 */
package jenjinn.engine.eval.pawnstructure;

import static jenjinn.engine.eval.PawnStructureEvaluator.BACKWARD_PENALTY;
import static jenjinn.engine.eval.PawnStructureEvaluator.CHAIN_BONUS;
import static jenjinn.engine.eval.PawnStructureEvaluator.DOUBLED_PENALTY;
import static jenjinn.engine.eval.PawnStructureEvaluator.ISOLATED_PENALTY;
import static jenjinn.engine.eval.PawnStructureEvaluator.PASSED_BONUS;
import static jenjinn.engine.eval.PawnStructureEvaluator.SEMIOPEN_FILE_BONUS;
import static jenjinn.engine.eval.PawnStructureEvaluator.evaluateBackwardPawns;
import static jenjinn.engine.eval.PawnStructureEvaluator.evaluateDoubledPawns;
import static jenjinn.engine.eval.PawnStructureEvaluator.evaluateIsolatedPawns;
import static jenjinn.engine.eval.PawnStructureEvaluator.evaluatePassedPawns;
import static jenjinn.engine.eval.PawnStructureEvaluator.evaluatePawnChains;
import static jenjinn.engine.eval.PawnStructureEvaluator.evaluatePhalanxFormations;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.eval.PawnStructureEvaluator;
import xawd.jflow.iterators.misc.IntPair;

/**
 * @author ThomasB
 */
class PawnStructureEvalTest
{
	@ParameterizedTest
	@MethodSource
	void test(Long whitePawnLocs, Long blackPawnLocs, ExpectedValues expectedValues)
	{
		final long w = whitePawnLocs, b = blackPawnLocs;
		final int doubledDifference = expectedValues.getDoubledPawnCountDifference();
		assertEquals(-doubledDifference*DOUBLED_PENALTY, evaluateDoubledPawns(w, b));

		final int passedDifference = expectedValues.getPassedPawnCountDifference();
		assertEquals(passedDifference*PASSED_BONUS, evaluatePassedPawns(w, b));

		final int chainLinkDifference = expectedValues.getChainLinkCountDifference();
		assertEquals(chainLinkDifference*CHAIN_BONUS, evaluatePawnChains(w, b));

		final int backwardCountDifference = expectedValues.getBackwardCountDifference();
		assertEquals(-backwardCountDifference*BACKWARD_PENALTY, evaluateBackwardPawns(w, b));

		final IntPair isolatedDifferences = expectedValues.getIsolatedPawnCountDifferences();
		final int expectedEval = -isolatedDifferences.first() * ISOLATED_PENALTY
				- isolatedDifferences.second() * (ISOLATED_PENALTY + SEMIOPEN_FILE_BONUS);
		assertEquals(expectedEval, evaluateIsolatedPawns(w, b));

		final int expectedWhitePhalanxScore = expectedValues
				.getWhitePhalanxSizes()
				.mapToInt(i -> PawnStructureEvaluator.PHALANX_BONUSES[i])
				.fold(0, (x, y) -> x + y);
		assertEquals(expectedWhitePhalanxScore, evaluatePhalanxFormations(w));

		final int expectedBlackPhalanxScore = expectedValues
				.getBlackPhalanxSizes()
				.mapToInt(i -> PawnStructureEvaluator.PHALANX_BONUSES[i])
				.fold(0, (x, y) -> x + y);
		assertEquals(expectedBlackPhalanxScore, evaluatePhalanxFormations(b));
	}

	static Stream<Arguments> test()
	{
		TestFileParser parser = new TestFileParser();
		return Stream.of("case001", "case002").map(parser::parse);
	}
}
