/**
 * 
 */
package jenjinn.parseutils;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.params.provider.Arguments;

import jenjinn.base.FileUtils;
import jenjinn.moves.ChessMove;
import jenjinn.pgn.CommonRegex;
import jflow.iterators.misc.Strings;
import jflow.seq.Seq;

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
	public Seq<String> loadFile(String fileName)
	{
		return FileUtils.cacheResource(getClass(), fileName, s -> !s.startsWith("//"));
	}
	
	public Set<ChessMove> parseMoves(Seq<String> lines)
	{
		if (lines.size() == 0) 
			throw new IllegalArgumentException();
		else if (lines.get(0).trim().toLowerCase().matches("none")) 
			return Collections.emptySet();
		else {
			String mv = CommonRegex.SHORTHAND_MOVE;
			return lines.flow()
					.flatMap(line -> Strings.allMatches(line, mv))
					.flatMap(shortmv -> ShorthandMoveParser.parse(shortmv).flow())
					.toSet();
		}
	}
}
