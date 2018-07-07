/**
 * 
 */
package jenjinn.engine.parseutils;

import static java.util.stream.Collectors.toList;
import static jenjinn.engine.base.FileUtils.loadResourceFromPackageOf;
import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.Strings.getAllMatches;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.pgn.CommonRegex;
import xawd.jflow.collections.FlowList;
import xawd.jflow.collections.Lists;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
public abstract class AbstractTestFileParser
{
	/**
	 * Parses a test resource file into a test case for some parameterized junit5
	 * test.
	 * 
	 * @param filename
	 *            The name of the file which is assumed to be a test resource in the
	 *            same package as the class subclassing this abstract class.
	 * @return A collection of arguments ready to be passed some parameterized test.
	 */
	public abstract Arguments parse(String filename);

	/**
	 * Load a test resource.
	 * 
	 * @param fileName
	 *            The name of the file which is assumed to be a test resource in the
	 *            same package as the class subclassing this abstract class.
	 * @return A list of the lines of the required file with comments and white
	 *         space ignored.
	 */
	public FlowList<String> loadFile(String fileName)
	{
		final List<String> lines = loadResourceFromPackageOf(getClass(), fileName).map(String::trim)
				.filter(s -> !s.isEmpty() && !s.startsWith("//")).collect(toList());
		return Lists.copy(lines);
	}
	
	public Set<ChessMove> parseMoves(final List<String> lines)
	{
		if (lines.isEmpty()) {
			throw new IllegalArgumentException();
		}
		else if (head(lines).trim().toLowerCase().matches("none")) {
			return Collections.emptySet();
		}
		else {
			final String mv = CommonRegex.SHORTHAND_MOVE;
			return Iterate.over(lines)
					.flatten(line -> Iterate.over(getAllMatches(line, mv)))
					.flatten(shortmv -> Iterate.over(ShorthandMoveParser.parse(shortmv)))
					.toSet();
		}
	}
}
