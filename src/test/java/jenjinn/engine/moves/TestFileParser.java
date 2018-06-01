/**
 *
 */
package jenjinn.engine.moves;

import static java.util.stream.Collectors.toList;
import static jenjinn.engine.utils.FileUtils.loadResourceFromPackageOf;
import static xawd.jflow.utilities.CollectionUtil.head;

import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.boardstate.BoardState;

/**
 * @author ThomasB
 *
 */
public final class TestFileParser {

	private TestFileParser() {}

	/**
	 * The file is expected to contain one line encoding the move, then nine lines encoding the
	 * start board, then nine lines encoded the expected resulting board. Blank lines are permitted
	 * between. See {@link BoardParseUtils} for more detail on how the board is encoded.
	 *
	 *
	 * @param fileName - name of test case file which must be contained in same package as this parser.
	 * @return arguments consisting of (move to test, start board, expected resulting board)
	 */
	public static Arguments parse(final String fileName)
	{
		final List<String> lines = loadResourceFromPackageOf(TestFileParser.class, fileName)
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(toList());

		if (lines.size() != 19) {
			throw new IllegalArgumentException();
		}

		final ChessMove reconstructedMove = ChessMove.decode(head(lines));
		final BoardState startState = BoardParseUtils.parseBoard(lines.subList(1, 10));
		final BoardState expectedEvolutionResult = BoardParseUtils.parseBoard(lines.subList(10, 19));
		return Arguments.of(reconstructedMove, startState, expectedEvolutionResult);
	}
}
