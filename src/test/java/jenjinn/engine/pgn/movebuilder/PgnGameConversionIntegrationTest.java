/**
 *
 */
package jenjinn.engine.pgn.movebuilder;

import static jenjinn.engine.utils.FileUtils.loadResourceFromPackageOf;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.MoveReversalData;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.moves.AbstractBoardStateTest;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.pgn.BadPgnException;
import jenjinn.engine.pgn.PgnGameConverter;
import jenjinn.engine.stringutils.VisualGridGenerator;
import xawd.jflow.collections.FlowList;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
class PgnGameConversionIntegrationTest extends AbstractBoardStateTest
{
	@Test
	void test()
	{
		final List<String> files = loadResourceFromPackageOf(getClass(), "integrationtestpgns")
				.sorted()
				.collect(Collectors.toList());

		for (final String filename : files) {
			final Stream<String> pgns = loadResourceFromPackageOf(getClass(), filename);
			pgns.limit(5000).forEach(pgn -> {
				try {
					final List<ChessMove> mvs = PgnGameConverter.parse(pgn.trim());
					final BoardState state = StartStateGenerator.createStartBoard();
					final FlowList<MoveReversalData> reversalData = Iterate.over(mvs).map(x -> new MoveReversalData()).toList();
					reversalData.flow().zipWith(mvs).forEach(p -> p.second().makeMove(state, p.first()));
					reversalData.rflow().zipWith(Iterate.reverseOver(mvs)).forEach(p -> p.second().reverseMove(state, p.first()));
					assertBoardStatesAreEqual(StartStateGenerator.createStartBoard(), state);
				} catch (final BadPgnException e) {
					e.printStackTrace();
					fail("Pgn: " + pgn + "\n" + filename);
				}
			});
		}
	}

	public static void main(String[] args) throws BadPgnException
	{
		final String pgn = "1.Nc3 c5 2.e4 e6 3.f4 Nc6 4.Bb5 Qc7 5.d3 a6 6.Bxc6 Qxc6 7.Nf3 Nf6 8.O-O Be7 9.Qe1 O-O 10.Bd2 b5 11.Ne5 Qc7 12.Qg3 d6 13.Ng4 b4 14.Nd1 Nxg4 15.Qxg4 f5 16.exf5 exf5 17.Qh3 Bf6 18.c3 a5 19.Ne3 Be6 20.Qf3 Qf7 21.a3 Rab8 22.axb4 axb4 23.Ra6 d5 24.Raa1 Rfd8 25.Nd1 bxc3 26.bxc3 d4 27.c4 Bc8 28.Ra7 Qe6 29.Ba5 Re8 30.Bc7 Qe1 1-0";//31.Bxg2
		final List<ChessMove> mvs = PgnGameConverter.parse(pgn);
		final BoardState state = StartStateGenerator.createStartBoard();
		mvs.stream().forEach(mv -> mv.makeMove(state));
		System.out.println(VisualGridGenerator.from(state.getPieceLocations()));
	}
}
