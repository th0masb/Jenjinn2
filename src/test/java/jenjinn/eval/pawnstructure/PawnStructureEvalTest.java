/**
 *
 */
package jenjinn.eval.pawnstructure;

import static jenjinn.eval.PawnStructureEvaluator.BACKWARD_PENALTY;
import static jenjinn.eval.PawnStructureEvaluator.CHAIN_BONUS;
import static jenjinn.eval.PawnStructureEvaluator.DOUBLED_PENALTY;
import static jenjinn.eval.PawnStructureEvaluator.ISOLATED_PENALTY;
import static jenjinn.eval.PawnStructureEvaluator.PASSED_BONUS;
import static jenjinn.eval.PawnStructureEvaluator.SEMIOPEN_FILE_BONUS;
import static jenjinn.eval.PawnStructureEvaluator.evaluateBackwardPawns;
import static jenjinn.eval.PawnStructureEvaluator.evaluateDoubledPawns;
import static jenjinn.eval.PawnStructureEvaluator.evaluateIsolatedPawns;
import static jenjinn.eval.PawnStructureEvaluator.evaluatePassedPawns;
import static jenjinn.eval.PawnStructureEvaluator.evaluatePawnChains;
import static jenjinn.eval.PawnStructureEvaluator.evaluatePhalanxFormations;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.eval.PawnStructureEvaluator;
import jflow.iterators.misc.IntPair;

/**
 * @author ThomasB
 */
class PawnStructureEvalTest
{
	@ParameterizedTest
	@MethodSource
	void test(Long whitePawnLocs, Long blackPawnLocs, ExpectedValues expectedValues)
	{
		long w = whitePawnLocs, b = blackPawnLocs;
		int doubledDifference = expectedValues.getDoubledPawnCountDifference();
		assertEquals(-doubledDifference*DOUBLED_PENALTY, evaluateDoubledPawns(w, b));

		int passedDifference = expectedValues.getPassedPawnCountDifference();
		assertEquals(passedDifference*PASSED_BONUS, evaluatePassedPawns(w, b));

		int chainLinkDifference = expectedValues.getChainLinkCountDifference();
		assertEquals(chainLinkDifference*CHAIN_BONUS, evaluatePawnChains(w, b));

		int backwardCountDifference = expectedValues.getBackwardCountDifference();
		assertEquals(-backwardCountDifference*BACKWARD_PENALTY, evaluateBackwardPawns(w, b));

		IntPair isolatedDifferences = expectedValues.getIsolatedPawnCountDifferences();
		int expectedEval = -isolatedDifferences._1 * ISOLATED_PENALTY
				- isolatedDifferences._2 * (ISOLATED_PENALTY + SEMIOPEN_FILE_BONUS);
		assertEquals(expectedEval, evaluateIsolatedPawns(w, b));

		int expectedWhitePhalanxScore = expectedValues
				.getWhitePhalanxSizes().flow()
				.mapToInt(i -> PawnStructureEvaluator.PHALANX_BONUSES[i])
				.fold(0, (x, y) -> x + y);
		assertEquals(expectedWhitePhalanxScore, evaluatePhalanxFormations(w));

		int expectedBlackPhalanxScore = expectedValues
				.getBlackPhalanxSizes().flow()
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
