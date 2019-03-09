/**
 *
 */
package com.github.maumay.jenjinn.eval.pawnstructure;

import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.BACKWARD_PENALTY;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.CHAIN_BONUS;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.DOUBLED_PENALTY;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.ISOLATED_PENALTY;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.PASSED_BONUS;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.SEMIOPEN_FILE_BONUS;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.evaluateBackwardPawns;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.evaluateDoubledPawns;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.evaluateIsolatedPawns;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.evaluatePassedPawns;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.evaluatePawnChains;
import static com.github.maumay.jenjinn.eval.PawnStructureEvaluator.evaluatePhalanxFormations;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.maumay.jenjinn.eval.PawnStructureEvaluator;
import com.github.maumay.jflow.utils.IntTup;

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
		assertEquals(-doubledDifference * DOUBLED_PENALTY, evaluateDoubledPawns(w, b));

		int passedDifference = expectedValues.getPassedPawnCountDifference();
		assertEquals(passedDifference * PASSED_BONUS, evaluatePassedPawns(w, b));

		int chainLinkDifference = expectedValues.getChainLinkCountDifference();
		assertEquals(chainLinkDifference * CHAIN_BONUS, evaluatePawnChains(w, b));

		int backwardCountDifference = expectedValues.getBackwardCountDifference();
		assertEquals(-backwardCountDifference * BACKWARD_PENALTY,
				evaluateBackwardPawns(w, b));

		IntTup isolatedDifferences = expectedValues.getIsolatedPawnCountDifferences();
		int expectedEval = -isolatedDifferences._1 * ISOLATED_PENALTY
				- isolatedDifferences._2 * (ISOLATED_PENALTY + SEMIOPEN_FILE_BONUS);
		assertEquals(expectedEval, evaluateIsolatedPawns(w, b));

		int expectedWhitePhalanxScore = expectedValues.getWhitePhalanxSizes().iter()
				.mapToInt(i -> PawnStructureEvaluator.PHALANX_BONUSES[i])
				.fold(0, (x, y) -> x + y);
		assertEquals(expectedWhitePhalanxScore, evaluatePhalanxFormations(w));

		int expectedBlackPhalanxScore = expectedValues.getBlackPhalanxSizes().iter()
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
