/**
 * 
 */
package jenjinn.engine.parseutils;

import static xawd.jflow.utilities.CollectionUtil.head;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.engine.base.FileUtils;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.pgn.CommonRegex;
import xawd.jflow.collections.FList;
import xawd.jflow.collections.Lists;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.utilities.Strings;

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
	public FList<String> loadFile(String fileName)
	{
		List<String> lines = FileUtils.cacheResource(getClass(), fileName, s -> !s.startsWith("//"));
		return Lists.copy(lines);
	}
	
	public Set<ChessMove> parseMoves(List<String> lines)
	{
		if (lines.isEmpty()) {
			throw new IllegalArgumentException();
		}
		else if (head(lines).trim().toLowerCase().matches("none")) {
			return Collections.emptySet();
		}
		else {
			String mv = CommonRegex.SHORTHAND_MOVE;
			return Iterate.over(lines)
					.flatten(line -> Strings.allMatches(line, mv))
					.flatten(shortmv -> Iterate.over(ShorthandMoveParser.parse(shortmv)))
					.toSet();
		}
	}
}
