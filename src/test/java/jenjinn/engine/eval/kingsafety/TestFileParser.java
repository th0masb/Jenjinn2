/**
 *
 */
package jenjinn.engine.eval.kingsafety;

import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;
import static xawd.jflow.utilities.CollectionUtil.take;
import static xawd.jflow.utilities.Strings.getAllMatches;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.eval.KingSafetyTable;
import jenjinn.engine.parseutils.AbstractTestFileParser;
import jenjinn.engine.parseutils.BoardParser;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.iterators.misc.Pair;

/**
 * @author ThomasB
 */
final class TestFileParser extends AbstractTestFileParser
{
	public Arguments parse(String fileName)
	{
		final List<String> lines = loadFile(fileName);
		return Arguments.of(BoardParser.parse(take(9, lines)), parseConstraintEvaluation(drop(9, lines)));
	}

	private Integer parseConstraintEvaluation(List<String> attackerInfo)
	{
		if (attackerInfo.size() != 12) {
			throw new IllegalArgumentException(attackerInfo.toString());
		}

		final List<String> whiteAttackers = take(6, attackerInfo);
		int whiteAttackUnits = 0, whiteAttackCount = 0;
		for (final Pair<ChessPiece, String> x : ChessPieces.iterate().zipWith(whiteAttackers).toList()) {
			final List<Integer> decoded = getAllMatches(x.second(), "[0-9]").map(Integer::parseInt).toList();
			if (decoded.size() != 3) {
				throw new IllegalArgumentException(attackerInfo.toString());
			}
			final int outerAttack = head(decoded), innerAttack = decoded.get(1);
			whiteAttackUnits += outerAttack * KingSafetyTable.INSTANCE.getOuterUnitValue(x.first());
			whiteAttackUnits += innerAttack * KingSafetyTable.INSTANCE.getInnerUnitValue(x.first());
			whiteAttackCount += tail(decoded);
		}

		final List<String> blackAttackers = drop(6, attackerInfo);
		int blackAttackUnits = 0, blackAttackCount = 0;
		for (final Pair<ChessPiece, String> x : ChessPieces.iterate().drop(6).zipWith(blackAttackers).toList()) {
			final List<Integer> decoded = getAllMatches(x.second(), "[0-9]").map(Integer::parseInt).toList();
			if (decoded.size() != 3) {
				throw new IllegalArgumentException(attackerInfo.toString());
			}
			final int outerAttack = head(decoded), innerAttack = decoded.get(1);
			blackAttackUnits += outerAttack * KingSafetyTable.INSTANCE.getOuterUnitValue(x.first());
			blackAttackUnits += innerAttack * KingSafetyTable.INSTANCE.getInnerUnitValue(x.first());
			blackAttackCount += tail(decoded);
		}

		final int whiteDivisor = whiteAttackCount > 1? 1 : 2, blackDivisor = blackAttackCount > 1? 1 : 2;
		return KingSafetyTable.INSTANCE.indexSafetyTable(whiteAttackUnits / whiteDivisor)
				- KingSafetyTable.INSTANCE.indexSafetyTable(blackAttackUnits / blackDivisor);
	}
}
