/**
 *
 */
package jenjinn.engine.moves;

import static jenjinn.engine.eval.piecesquaretables.TestingPieceSquareTables.getEndgameTables;
import static jenjinn.engine.eval.piecesquaretables.TestingPieceSquareTables.getMidgameTables;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static xawd.jflow.utilities.MapUtil.objMap;
import static xawd.jflow.utilities.StringUtils.findFirstMatch;
import static xawd.jflow.utilities.StringUtils.getAllMatches;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import jenjinn.engine.bitboards.BitboardUtils;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.CastlingStatus;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.boardstate.HalfMoveCounter;
import jenjinn.engine.boardstate.StateHashCache;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import jenjinn.engine.utils.BoardStateHasher;
import jenjinn.engine.utils.ZobristHasher;
import xawd.jflow.iterators.construction.IterRange;
import xawd.jflow.iterators.construction.Iterate;
import xawd.jflow.utilities.StringUtils;

/**
 * Lets us construct {@linkplain BoardState} instances in an easy way via external files.
 *
 * @author t
 */
public final class BoardParseUtils
{
	private BoardParseUtils() {}

	public static BoardState parseBoard(final List<String> attributes)
	{
		if (attributes.size() != 9) {
			throw new IllegalArgumentException();
		}
		final List<String> atts = Iterate.over(attributes).map(String::trim).map(String::toLowerCase).toList();
		final DetailedPieceLocations pieceLocations = constructPieceLocations(atts.get(0), atts.get(1));
		final HalfMoveCounter moveCount = constructHalfMoveCounter(atts.get(2));
		final CastlingStatus castlingStatus = constructCastlingStatus(atts.get(3), atts.get(4), atts.get(5));
		final Set<DevelopmentPiece> developedPieces = constructDevelopedPieces(atts.get(6));
		final Side activeSide = constructActiveSide(atts.get(7));
		final BoardSquare enpassantSquare = constructEnpassantSquare(atts.get(8));
		final ZobristHasher stateHasher = BoardStateHasher.getDefault();
		final long hashOfConstructedState = stateHasher.hashBoardState(activeSide, enpassantSquare, castlingStatus, pieceLocations);
		final StateHashCache hashCache = constructDummyHashCache(hashOfConstructedState, moveCount.getValue());

		return new BoardState(stateHasher, hashCache, pieceLocations, moveCount, castlingStatus, developedPieces, activeSide, enpassantSquare);
	}

	private static StateHashCache constructDummyHashCache(final long initializedBoardHash, final int halfMoveCount)
	{
		final int cacheSize = StateHashCache.CACHE_SIZE;
		final long[] cache = IterRange.to(cacheSize).mapToLong(i -> i + 1).toArray();
		cache[halfMoveCount % cacheSize] = initializedBoardHash;
		return new StateHashCache(cache, halfMoveCount);
	}

	private static BoardSquare constructEnpassantSquare(final String enpassantSquare)
	{
		assertTrue(enpassantSquare.trim().matches("^enpassant_square: *(none)|([a-h][1-8])$"));
		final Optional<String> squareMatch = StringUtils.findFirstMatch(enpassantSquare, "[a-h][1-8]");
		return squareMatch.isPresent()? BoardSquare.valueOf(squareMatch.get().toUpperCase()) : null;
	}

	private static Side constructActiveSide(final String activeSide)
	{
		assertTrue(activeSide.trim().matches("^active_side: *(white)|(black)$"));
		final Optional<String> whiteMatch = StringUtils.findFirstMatch(activeSide, "white");
		return whiteMatch.isPresent()? Side.WHITE : Side.BLACK;
	}

	private static Set<DevelopmentPiece> constructDevelopedPieces(final String developedPieces)
	{
		assertTrue(developedPieces.trim().matches("^developed_pieces:( *[a-h][1-8])?( +[a-h][1-8]){0 : 5}$"));
		final List<String> squaresMatched = StringUtils.getAllMatches(developedPieces, "[a-h][1-8]");
		final Set<BoardSquare> uniqueSquares = Iterate.over(squaresMatched).map(String::toUpperCase).map(BoardSquare::valueOf).toSet();
		if (uniqueSquares.size() != squaresMatched.size()) {
			throw new IllegalArgumentException(developedPieces);
		}
		// Will error here if invalid square was passed, cannot add null to enumset.
		return Iterate.over(uniqueSquares).map(DevelopmentPiece::fromStartSquare).toCollection(() -> EnumSet.noneOf(DevelopmentPiece.class));
	}

	private static CastlingStatus constructCastlingStatus(final String rights, final String whiteStatus, final String blackStatus)
	{
		assertTrue(rights.trim().matches("^castling_rights:( *wk)?( +wq)?( +bk)?( +bq)?$"));
		assertTrue(whiteStatus.trim().matches("^white_castle_status: *(wk)|(wq)|(bk)|(bq)$"));
		assertTrue(blackStatus.trim().matches("^black_castle_status: *(wk)|(wq)|(bk)|(bq)$"));

		final Map<String, CastleZone> regexMatchers = CastleZone.iterateAll().toMap(CastleZone::getSimpleIdentifier, Function.identity());

		final Set<CastleZone> rightSet = Iterate.over(regexMatchers.keySet())
				.filter(rx -> StringUtils.matchesAnywhere(rights, rx))
				.map(regexMatchers::get)
				.toCollection(() -> EnumSet.noneOf(CastleZone.class));

		final CastleZone whiteCastleStatus = Iterate.over(regexMatchers.keySet())
				.filter(rx -> StringUtils.matchesAnywhere(whiteStatus, rx))
				.map(regexMatchers::get)
				.safeNext().orElseThrow(AssertionError::new);

		final CastleZone blackCastleStatus = Iterate.over(regexMatchers.keySet())
				.filter(rx -> StringUtils.matchesAnywhere(blackStatus, rx))
				.map(regexMatchers::get)
				.safeNext().orElseThrow(AssertionError::new);

		return new CastlingStatus(rightSet, whiteCastleStatus, blackCastleStatus);
	}

	private static HalfMoveCounter constructHalfMoveCounter(final String clockString)
	{
		assertTrue(clockString.trim().matches("^half_move_clock: *[0-9]+$"));
		return new HalfMoveCounter(Integer.parseInt(findFirstMatch(clockString, "[0-9]+").get()));
	}

	private static DetailedPieceLocations constructPieceLocations(final String whiteLocs, final String blackLocs)
	{
		/* Arbitrary number of boardsquare separated by whitespace in parentheses */
		final String groupedSquares = "\\( *([a-h][1-8] *)?( +[a-h][1-8])* *\\)";
		final String sixGroupedSquareSets = IterRange.to(6).mapToObject(i -> groupedSquares).reduce(" *", (a, b) -> a + " +" + b);

		assertTrue(whiteLocs.trim().matches("^white_pieces:" + sixGroupedSquareSets + "$"));
		assertTrue(blackLocs.trim().matches("^black_pieces:" + sixGroupedSquareSets + "$"));

		final PieceSquareTables midTables = getMidgameTables(), endTables = getEndgameTables();
		return Iterate.over(getAllMatches(whiteLocs + blackLocs, groupedSquares))
				.map(x -> getAllMatches(x, "[a-h][1-8]"))
				.map(xs -> objMap(String::toUpperCase, xs))
				.map(xs -> objMap(BoardSquare::valueOf, xs))
				.map(BitboardUtils::bitwiseOr)
				.build(flow -> new DetailedPieceLocations(flow, midTables, endTables));
	}

//	public static void main(final String[] args) {
//		final String groupedSquaresGroup = "\\( *([a-h][1-8] *)?( +[a-h][1-8])* *\\)";
//		final String sixGroupedSquareSets = IterRange.to(6).mapToObject(i -> groupedSquaresGroup).reduce(" *", (a, b) -> a + " +" + b);
//
//		final String whiteLocs = "white_pieces: ( e1 h8  ) (f2) ()  (c7 c8) () ()";
//		System.out.println(whiteLocs.trim().matches("^white_pieces:" + sixGroupedSquareSets + "$"));
//	}
}
