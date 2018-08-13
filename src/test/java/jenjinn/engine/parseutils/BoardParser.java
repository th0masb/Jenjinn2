/**
 *
 */
package jenjinn.engine.parseutils;

import static jenjinn.engine.eval.piecesquaretables.TestingPieceSquareTables.getEndgameTables;
import static jenjinn.engine.eval.piecesquaretables.TestingPieceSquareTables.getMidgameTables;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static xawd.jflow.utilities.Strings.allMatches;
import static xawd.jflow.utilities.Strings.firstMatch;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.CastleZone;
import jenjinn.engine.base.DevelopmentPiece;
import jenjinn.engine.base.Side;
import jenjinn.engine.bitboards.BitboardUtils;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.CastlingStatus;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.boardstate.HalfMoveCounter;
import jenjinn.engine.boardstate.HashCache;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import jenjinn.engine.utils.BoardHasher;
import xawd.jflow.iterators.factories.IterRange;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.utilities.Strings;

/**
 * Lets us construct {@linkplain BoardState} instances in an easy way via external files.
 *
 * @author t
 */
public final class BoardParser
{
	private static final int DEFAULT_MOVE_COUNT = 20;

	private BoardParser() {}

	public static BoardState parse(List<String> attributes)
	{
		return parse(attributes, DEFAULT_MOVE_COUNT);
	}

	public static BoardState parse(List<String> attributes, int totalMoveCount)
	{
		if (attributes.size() != 9) {
			throw new IllegalArgumentException();
		}
		List<String> atts = Iterate.over(attributes).map(String::trim).map(String::toLowerCase).toList();
		DetailedPieceLocations pieceLocations = constructPieceLocations(atts.get(0), atts.get(1));
		HalfMoveCounter halfMoveCount = constructHalfMoveCounter(atts.get(2));
		CastlingStatus castlingStatus = constructCastlingStatus(atts.get(3), atts.get(4), atts.get(5));
		Set<DevelopmentPiece> developedPieces = constructDevelopedPieces(atts.get(6));
		Side activeSide = constructActiveSide(atts.get(7));
		BoardSquare enpassantSquare = constructEnpassantSquare(atts.get(8));
		long hashOfConstructedState = pieceLocations.getSquarePieceFeatureHash()
				^ BoardHasher.INSTANCE.hashNonPieceFeatures(activeSide, enpassantSquare, castlingStatus);
		HashCache hashCache = constructDummyHashCache(hashOfConstructedState, totalMoveCount);

		return new BoardState(hashCache, pieceLocations, halfMoveCount, castlingStatus, developedPieces, activeSide, enpassantSquare);
	}

	private static HashCache constructDummyHashCache(long initializedBoardHash, int totalMoveCount)
	{
		int cacheSize = HashCache.CACHE_SIZE;
		long[] cache = IterRange.to(cacheSize).mapToLong(i -> i + 1).toArray();
		cache[totalMoveCount % cacheSize] = initializedBoardHash;
		return new HashCache(cache, totalMoveCount);
	}

	private static BoardSquare constructEnpassantSquare(String enpassantSquare)
	{
		assertTrue(enpassantSquare.trim().matches("^enpassant_square: *((none)|([a-h][1-8]))$"));
		Optional<String> squareMatch = Strings.firstMatch(enpassantSquare, "[a-h][1-8]");
		return squareMatch.isPresent()? BoardSquare.valueOf(squareMatch.get().toUpperCase()) : null;
	}

	private static Side constructActiveSide(String activeSide)
	{
		assertTrue(activeSide.trim().matches("^active_side: *(white|black)$"));
		Optional<String> whiteMatch = Strings.firstMatch(activeSide, "white");
		return whiteMatch.isPresent()? Side.WHITE : Side.BLACK;
	}

	private static Set<DevelopmentPiece> constructDevelopedPieces(String developedPieces)
	{
		assertTrue(developedPieces.trim().matches("^developed_pieces:(( *none)|(( *[a-h][1-8])( +[a-h][1-8]){0,11}))$"), developedPieces);
		List<String> squaresMatched = Strings.allMatches(developedPieces, "[a-h][1-8]").toList();
		Set<BoardSquare> uniqueSquares = Iterate.over(squaresMatched).map(String::toUpperCase).map(BoardSquare::valueOf).toSet();
		if (uniqueSquares.size() != squaresMatched.size()) {
			throw new IllegalArgumentException(developedPieces);
		}
		// Will error here if invalid square was passed, cannot add null to enumset.
		return Iterate.over(uniqueSquares).map(DevelopmentPiece::fromStartSquare).toCollection(() -> EnumSet.noneOf(DevelopmentPiece.class));
	}

	private static CastlingStatus constructCastlingStatus(String rights, String whiteStatus, String blackStatus)
	{
		assertTrue(rights.trim().matches("^castling_rights:( *wk)?( +wq)?( +bk)?( +bq)?$"));
		assertTrue(whiteStatus.trim().matches("^white_castle_status: *(none|wk|wq|bk|bq)$"));
		assertTrue(blackStatus.trim().matches("^black_castle_status: *(none|wk|wq|bk|bq)$"));

		Map<String, CastleZone> regexMatchers = CastleZone.iterateAll().toMap(CastleZone::getSimpleIdentifier, Function.identity());

		Set<CastleZone> rightSet = Iterate.over(regexMatchers.keySet())
				.filter(rx -> Strings.matchesAnywhere(rights, rx))
				.map(regexMatchers::get)
				.toCollection(() -> EnumSet.noneOf(CastleZone.class));

		CastleZone whiteCastleStatus = Iterate.over(regexMatchers.keySet())
				.filter(rx -> Strings.matchesAnywhere(whiteStatus, rx))
				.map(regexMatchers::get)
				.safeNext().orElse(null);

		CastleZone blackCastleStatus = Iterate.over(regexMatchers.keySet())
				.filter(rx -> Strings.matchesAnywhere(blackStatus, rx))
				.map(regexMatchers::get)
				.safeNext().orElse(null);

		return new CastlingStatus(rightSet, whiteCastleStatus, blackCastleStatus);
	}

	private static HalfMoveCounter constructHalfMoveCounter(String clockString)
	{
		assertTrue(clockString.trim().matches("^half_move_clock: *[0-9]+$"));
		return new HalfMoveCounter(Integer.parseInt(firstMatch(clockString, "[0-9]+").get()));
	}

	private static DetailedPieceLocations constructPieceLocations(String whiteLocs, String blackLocs)
	{
		/* Arbitrary number of boardsquare separated by whitespace in parentheses */
		String groupedSquares = "\\( *([a-h][1-8] *)?( +[a-h][1-8])* *\\)";
		String sixGroupedSquareSets = IterRange.to(6).mapToObject(i -> groupedSquares).fold(" *", (a, b) -> a + " +" + b);

		assertTrue(whiteLocs.trim().matches("^white_pieces:" + sixGroupedSquareSets + "$"));
		assertTrue(blackLocs.trim().matches("^black_pieces:" + sixGroupedSquareSets + "$"));

		PieceSquareTables midTables = getMidgameTables(), endTables = getEndgameTables();
		return allMatches(whiteLocs + blackLocs, groupedSquares)
				.map(x -> allMatches(x, "[a-h][1-8]"))
				.map(xs -> xs.map(String::toUpperCase))
				.map(xs -> xs.map(BoardSquare::valueOf).toList())
				.mapToLong(BitboardUtils::bitwiseOr)
				.build(flow -> new DetailedPieceLocations(flow.toArray(), midTables, endTables));
	}
}
