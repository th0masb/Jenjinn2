/**
 *
 */
package jenjinn.engine.boardstate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static xawd.jflow.utilities.MapUtil.objMap;
import static xawd.jflow.utilities.StringUtils.findFirstMatch;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import jenjinn.engine.bitboards.BitboardUtils;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author t
 *
 */
public final class BoardParseUtils {

	private BoardParseUtils() {}

	public static BoardState parseBoard(final List<String> attributes)
	{
		if (attributes.size() != 9) {
			throw new IllegalArgumentException();
		}
		final List<String> atts = Iterate.over(attributes).map(String::trim).toList();
		final DetailedPieceLocations pieceLocations = constructPieceLocations(atts.get(0), atts.get(1));
		final HalfMoveClock clock = constructHalfMoveClock(atts.get(2));
		final CastlingStatus castlingStatus = constructCastlingStatus(atts.get(3), atts.get(4), atts.get(5));
		final Set<DevelopmentPiece> developedPieces = constructDevelopedPieces(atts.get(6));
		final Side activeSide = constructActiveSide(atts.get(7));
		final BoardSquare enpassantSquare = constructEnpassantSquare(atts.get(8));

		throw new RuntimeException();
		//		return new BoardState(stateHasher, hashCache, pieceLocations, gameClock, castlingStatus, developedPieces, activeSide, enPassantSquare)
	}

	private static BoardSquare constructEnpassantSquare(String string)
	{
		throw new RuntimeException();
	}

	private static Side constructActiveSide(String string)
	{
		throw new RuntimeException();
	}

	private static Set<DevelopmentPiece> constructDevelopedPieces(String developedPieces)
	{
		throw new RuntimeException();
	}

	private static CastlingStatus constructCastlingStatus(String rights, String whiteStatus, String blackStatus)
	{
		throw new RuntimeException();
	}

	public static HalfMoveClock constructHalfMoveClock(String clockString)
	{
		assertTrue(clockString.trim().matches("^half_move_clock: *[0-9]+$"));
		return new HalfMoveClock(Integer.parseInt(findFirstMatch(clockString, "[0-9]+").get()));
	}

	public static DetailedPieceLocations constructPieceLocations(String whiteLocs, String blackLocs)
	{
		assertTrue(whiteLocs.trim().matches("^white_locs:.*$"));
		assertTrue(blackLocs.trim().matches("^black_locs:.*$"));

		final Pattern squareSequencePattern = Pattern.compile("\\([a-hA-H1-8, ]*\\)");
		final Pattern squarePattern = Pattern.compile("[a-hA-H1-8]{2}");
		final PieceSquareTables testingTables = TestingPieceSquareTables.get();
		return Iterate.over(getAllMatches(whiteLocs + blackLocs, squareSequencePattern))
				.map(x -> getAllMatches(x, squarePattern))
				.map(xs -> objMap(String::toUpperCase, xs))
				.map(xs -> objMap(BoardSquare::valueOf, xs))
				.map(BitboardUtils::bitwiseOr)
				.build(flow -> new DetailedPieceLocations(flow, testingTables, testingTables));
	}
}
