/**
 *
 */
package jenjinn.engine.boardstate.legalmoves;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.moves.CastleMove;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnpassantMove;
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
		assertEquals(expectedMoves, LegalMoves.getMoves(state).toSet());
		assertEquals(filterAttacks(state, expectedMoves), LegalMoves.getAttacks(state).toSet());
	}

	private Set<ChessMove> filterAttacks(final BoardState state, final Set<ChessMove> moves)
	{
		final long passiveLocs = state.getPieceLocations().getSideLocations(state.getActiveSide().otherSide());
		return Iterate.over(moves)
				.filter(mv -> !(mv instanceof CastleMove))
				.filter(mv -> (mv instanceof EnpassantMove) || bitboardsIntersect(mv.getTarget().asBitboard(), passiveLocs))
				.toSet();
	}

	static Flow<Arguments> test()
	{
		throw new RuntimeException();
	}
}
