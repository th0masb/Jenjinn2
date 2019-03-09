/**
 *
 */
package com.github.maumay.jenjinn.eval.kingsafety;

import org.junit.jupiter.params.provider.Arguments;

import com.github.maumay.jenjinn.eval.KingSafetyTable;
import com.github.maumay.jenjinn.parseutils.AbstractTestFileParser;
import com.github.maumay.jenjinn.parseutils.BoardParser;
import com.github.maumay.jenjinn.pieces.ChessPieces;
import com.github.maumay.jenjinn.pieces.Piece;
import com.github.maumay.jflow.utils.Strings;
import com.github.maumay.jflow.utils.Tup;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		Vec<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(lines.take(9)),
				parseConstraintEvaluation(lines.skip(9)));
	}

	private Integer parseConstraintEvaluation(Vec<String> attackerInfo)
	{
		if (attackerInfo.size() != 12) {
			throw new IllegalArgumentException(attackerInfo.toString());
		}

		Vec<String> whiteAttackers = attackerInfo.take(6);
		int whiteAttackUnits = 0, whiteAttackCount = 0;
		for (Tup<Piece, String> x : ChessPieces.ALL.iter().zipWith(whiteAttackers)
				.toList()) {
			Vec<Integer> decoded = Strings.allMatches(x._2, "[0-9]")
					.map(Integer::parseInt).toVec();
			if (decoded.size() != 3) {
				throw new IllegalArgumentException(attackerInfo.toString());
			}
			int outerAttack = decoded.head(), innerAttack = decoded.get(1);
			whiteAttackUnits += outerAttack
					* KingSafetyTable.INSTANCE.getOuterUnitValue(x._1);
			whiteAttackUnits += innerAttack
					* KingSafetyTable.INSTANCE.getInnerUnitValue(x._1);
			whiteAttackCount += decoded.last();
		}

		Vec<String> blackAttackers = attackerInfo.skip(6);
		int blackAttackUnits = 0, blackAttackCount = 0;
		for (Tup<Piece, String> x : ChessPieces.ALL.iter().skip(6).zipWith(blackAttackers)
				.toVec()) {
			Vec<Integer> decoded = Strings.allMatches(x._2, "[0-9]")
					.map(Integer::parseInt).toVec();
			if (decoded.size() != 3) {
				throw new IllegalArgumentException(attackerInfo.toString());
			}
			int outerAttack = decoded.head(), innerAttack = decoded.get(1);
			blackAttackUnits += outerAttack
					* KingSafetyTable.INSTANCE.getOuterUnitValue(x._1);
			blackAttackUnits += innerAttack
					* KingSafetyTable.INSTANCE.getInnerUnitValue(x._1);
			blackAttackCount += decoded.last();
		}

		int whiteDivisor = whiteAttackCount > 1 ? 1 : 2,
				blackDivisor = blackAttackCount > 1 ? 1 : 2;
		return KingSafetyTable.INSTANCE.indexSafetyTable(whiteAttackUnits / whiteDivisor)
				- KingSafetyTable.INSTANCE
						.indexSafetyTable(blackAttackUnits / blackDivisor);
	}
}
