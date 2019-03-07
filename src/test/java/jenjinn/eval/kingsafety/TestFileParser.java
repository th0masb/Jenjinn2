/**
 *
 */
package jenjinn.eval.kingsafety;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.eval.KingSafetyTable;
import jenjinn.parseutils.AbstractTestFileParser;
import jenjinn.parseutils.BoardParser;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;
import jflow.iterators.misc.Pair;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	@Override
	public Arguments parse(String fileName)
	{
		Seq<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(lines.take(9)), parseConstraintEvaluation(lines.drop(9)));
	}

	private Integer parseConstraintEvaluation(Seq<String> attackerInfo)
	{
		if (attackerInfo.size() != 12) {
			throw new IllegalArgumentException(attackerInfo.toString());
		}

		Seq<String> whiteAttackers = attackerInfo.take(6);
		int whiteAttackUnits = 0, whiteAttackCount = 0;
		for (Pair<Piece, String> x : ChessPieces.ALL.flow().zipWith(whiteAttackers).toList()) {
			Seq<Integer> decoded = Strings.allMatches(x._2, "[0-9]").map(Integer::parseInt).toSeq();
			if (decoded.size() != 3) {
				throw new IllegalArgumentException(attackerInfo.toString());
			}
			int outerAttack = decoded.head(), innerAttack = decoded.get(1);
			whiteAttackUnits += outerAttack * KingSafetyTable.INSTANCE.getOuterUnitValue(x._1);
			whiteAttackUnits += innerAttack * KingSafetyTable.INSTANCE.getInnerUnitValue(x._1);
			whiteAttackCount += decoded.last();
		}

		Seq<String> blackAttackers = attackerInfo.drop(6);
		int blackAttackUnits = 0, blackAttackCount = 0;
		for (Pair<Piece, String> x : ChessPieces.ALL.flow().drop(6).zipWith(blackAttackers).toSeq()) {
			Seq<Integer> decoded = Strings.allMatches(x._2, "[0-9]").map(Integer::parseInt).toSeq();
			if (decoded.size() != 3) {
				throw new IllegalArgumentException(attackerInfo.toString());
			}
			int outerAttack = decoded.head(), innerAttack = decoded.get(1);
			blackAttackUnits += outerAttack * KingSafetyTable.INSTANCE.getOuterUnitValue(x._1);
			blackAttackUnits += innerAttack * KingSafetyTable.INSTANCE.getInnerUnitValue(x._1);
			blackAttackCount += decoded.last();
		}

		int whiteDivisor = whiteAttackCount > 1? 1 : 2, blackDivisor = blackAttackCount > 1? 1 : 2;
		return KingSafetyTable.INSTANCE.indexSafetyTable(whiteAttackUnits / whiteDivisor)
				- KingSafetyTable.INSTANCE.indexSafetyTable(blackAttackUnits / blackDivisor);
	}
}
