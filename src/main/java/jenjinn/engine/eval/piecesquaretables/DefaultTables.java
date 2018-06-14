/**
 *
 */
package jenjinn.engine.eval.piecesquaretables;

import static java.util.Arrays.asList;

import java.util.List;

/**
 * @author ThomasB
 *
 */
public final class DefaultTables
{
	private static final List<String> MIDGAME_FILES = asList(
			"pawn-midgame",
			"knight-midgame",
			"bishop-midgame",
			"rook-midgame",
			"queen-midgame");

	private DefaultTables()
	{
	}

	public static PieceSquareTables getMidgame()
	{
		throw new RuntimeException();
	}

	public static PieceSquareTables getEndgame()
	{
		throw new RuntimeException();
	}
}
