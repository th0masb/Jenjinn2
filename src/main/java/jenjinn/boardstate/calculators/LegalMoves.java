/**
 *
 */
package jenjinn.boardstate.calculators;

import static java.lang.Math.abs;
import static jenjinn.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import jenjinn.base.CastleZone;
import jenjinn.base.Dir;
import jenjinn.base.Side;
import jenjinn.base.Square;
import jenjinn.bitboards.BitboardIterator;
import jenjinn.bitboards.Bitboards;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.DetailedPieceLocations;
import jenjinn.moves.ChessMove;
import jenjinn.moves.EnpassantMove;
import jenjinn.moves.MoveCache;
import jenjinn.moves.PromotionMove;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;
import jenjinn.utils.PieceSquarePair;
import jflow.iterators.Flow;
import jflow.iterators.factories.Iter;
import jflow.iterators.misc.Pair;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
public final class LegalMoves
{
	private static final Seq<Dir> WHITE_EP_SEARCH_DIRS = Seq.of(Dir.SW, Dir.SE);
	private static final Seq<Dir> BLACK_EP_SEARCH_DIRS = Seq.of(Dir.NW, Dir.NE);

	private LegalMoves()
	{
	}

	public static Flow<ChessMove> getAttacks(BoardState state)
	{
		return getLegalMoves(state, true);
	}

	public static Flow<ChessMove> getAllMoves(BoardState state)
	{
		return getLegalMoves(state, false);
	}

	static Flow<ChessMove> getLegalMoves(BoardState state, boolean forceAttacks)
	{
		Side active = state.getActiveSide(), passive = active.otherSide();
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		long passivePieceLocs = pieceLocs.getSideLocations(passive);
		Seq<Piece> activePieces = ChessPieces.of(active);
		Piece activeKing = activePieces.last();
		Square kingLoc = pieceLocs.iterateLocs(activeKing).next();
		long passiveControl = SquareControl.calculate(state, passive);
		PinnedPieceCollection pinnedPieces = PinnedPieces.in(state);

		boolean inCheck = bitboardsIntersect(passiveControl, kingLoc.bitboard);
		boolean castlingAllowed = !inCheck && !forceAttacks
				&& state.getCastlingStatus().getStatusFor(active) == null;
		Flow<ChessMove> moves = castlingAllowed ? getCastlingMoves(state, passiveControl) : Iter.empty();
		long allowedMoveArea = forceAttacks ? passivePieceLocs : Bitboards.universal();

		if (inCheck) {
			List<PieceSquarePair> attackers = getPassiveAttackersOfActiveKing(state);
			for (PieceSquarePair attacker : attackers) {
				Piece piece = attacker.getPiece();
				if (piece.isSlidingPiece()) {
					boolean whiteAttack = piece.isWhite();
					long kloc = kingLoc.bitboard;
					long white = whiteAttack ? pieceLocs.getWhiteLocations() : pieceLocs.getWhiteLocations() ^ kloc;
					long black = whiteAttack ? pieceLocs.getBlackLocations() ^ kloc : pieceLocs.getBlackLocations();
					passiveControl |= piece.getSquaresOfControl(attacker.getSquare(), white, black);
				}
			}
			if (attackers.size() > 1) {
				allowedMoveArea = 0L;
			} else {
				PieceSquarePair attacker = attackers.get(0);
				allowedMoveArea &= getBlockingSquares(kingLoc, attacker);
				Square ep = state.getEnPassantSquare(), attsq = attacker.getSquare();
				if (ep != null && abs(ep.ordinal() - attsq.ordinal()) == 8) {
					assert attacker.getPiece().isPawn();
					moves = moves.append(getEnpassantCheckEscape(attacker, state, pinnedPieces));
				}
			}
		}

		// Add moves from non king pieces
		long faa = allowedMoveArea;
		moves = moves.append(
				activePieces.flow().take(5).flatMap(p -> getNonKingMoves(state, p, pinnedPieces, faa)));

		// Add king moves
		long kingConstraint = forceAttacks ? ~passiveControl & passivePieceLocs : ~passiveControl;
		moves = moves.append(getMovesForKing(state, kingLoc, kingConstraint));

		return moves;
	}

	private static Flow<ChessMove> getCastlingMoves(BoardState state, long passiveControl)
	{
		Side activeSide = state.getActiveSide();
		Predicate<CastleZone> sideFilter = activeSide.isWhite() ? z -> z.isWhiteZone() : z -> !z.isWhiteZone();
		Set<CastleZone> allRights = state.getCastlingStatus().getCastlingRights();
		Flow<CastleZone> availableRights = Iter.over(allRights).filter(sideFilter);
		long allPieces = state.getPieceLocations().getAllLocations();
		Flow<CastleZone> legalAvailableRights = availableRights.filter(zone -> {
			long reqClearArea = zone.getRequiredFreeSquares();
			long reqUncontrolledArea = zone.getRequiredUncontrolledSquares();
			return !bitboardsIntersect(reqClearArea, allPieces) && !bitboardsIntersect(passiveControl, reqUncontrolledArea);
		});
		return legalAvailableRights.map(MoveCache::getMove);
	}

	private static Flow<ChessMove> getMovesForKing(BoardState state, Square kingLoc,
			long areaConstraint)
	{
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		Piece activeKing = ChessPieces.of(state.getActiveSide()).last();
		long kingMoves = activeKing.getMoves(kingLoc, white, black) & areaConstraint;
		return bitboard2moves(activeKing, kingLoc, kingMoves);
	}

	/*
	 * Think this is all we need for both blocking and moving non-king pieces. If
	 * the king is not in check then the overallAreaConstraint is the universal
	 * bitboard.
	 */
	static Flow<ChessMove> getNonKingMoves(BoardState state, Piece piece,
			PinnedPieceCollection pinnedPieces, long overallAreaConstraint)
	{
		if (Long.bitCount(overallAreaConstraint) == 0) {
			return Iter.empty();
		}

		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		Pair<Seq<Square>, Seq<Square>> pinnedPartition = pieceLocs.iterateLocs(piece).toSeq()
				.partition(pinnedPieces::containsLocation);
		
		Flow<ChessMove> pinnedContribution = pinnedPartition._1.flow().flatMap(square -> {
			long areaCons = pinnedPieces.getConstraintAreaOfPieceAt(square) & overallAreaConstraint;
			return bitboard2moves(piece, square, piece.getMoves(square, white, black) & areaCons);
		});

		Flow<ChessMove> notPinnedContributions = pinnedPartition._2.flow().flatMap(square -> {
			long areaCons = overallAreaConstraint;
			return bitboard2moves(piece, square, piece.getMoves(square, white, black) & areaCons);
		});

		Flow<ChessMove> allContributions = notPinnedContributions.append(pinnedContribution);

		if (piece.isPawn() && state.hasEnpassantAvailable()) {
			Square ep = state.getEnPassantSquare();
			long plocs = pieceLocs.locationsOf(piece);
			Seq<Dir> searchDirs = piece.isWhite() ? WHITE_EP_SEARCH_DIRS : BLACK_EP_SEARCH_DIRS;
			Flow<ChessMove> epContribution = searchDirs.flow().map(ep::getNextSquare)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.filter(sq -> {
						if (bitboardsIntersect(plocs, sq.bitboard)) {
							if (pinnedPieces.containsLocation(sq)) {
								long areaCons = pinnedPieces.getConstraintAreaOfPieceAt(sq);
								return bitboardsIntersect(areaCons, ep.bitboard);
							}
							else {
								return true;
							}
						}
						else {
							return false;
						}
					}).map(sq -> new EnpassantMove(sq, ep));
			return allContributions.append(epContribution);
		} else {
			return allContributions;
		}
	}

	/**
	 * In the unbelievably unlikely situation that a passive pawn which has just
	 * moved forward two is attacking the active king (causing check) we must
	 * additionally check for enpassant escape moves from the active pawns.
	 */
	static Flow<ChessMove> getEnpassantCheckEscape(PieceSquarePair attacker, BoardState state,
			PinnedPieceCollection pinnedPieces)
	{
		Square attackerLoc = attacker.getSquare(), enpassantSquare = state.getEnPassantSquare();
		assert enpassantSquare != null;
		assert attacker.getPiece().isPawn();
		assert Math.abs(attackerLoc.ordinal() - enpassantSquare.ordinal()) == 8;

		Side active = state.getActiveSide();
		Piece activePawn = ChessPieces.of(active).head();
		long activePawnLocs = state.getPieceLocations().locationsOf(activePawn);
		Seq<Dir> searchDirs = active.isWhite() ? WHITE_EP_SEARCH_DIRS : BLACK_EP_SEARCH_DIRS;

		return searchDirs.flow().map(enpassantSquare::getNextSquare)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(square -> {
					if (bitboardsIntersect(activePawnLocs, square.bitboard)) {
						if (pinnedPieces.containsLocation(square)) {
							return bitboardsIntersect(pinnedPieces.getConstraintAreaOfPieceAt(square),
									enpassantSquare.bitboard);
						} else {
							return true;
						}
					} else {
						return false;
					}
				}).map(sourceSquare -> new EnpassantMove(sourceSquare, enpassantSquare));
	}

	/**
	 * @param activeKingLoc
	 *            - Assumed location of the active side king
	 * @param attacker
	 *            - Piece and location assumed to be directly attacking the active
	 *            king.
	 * @return the squares (represented by a bitboard) which an active piece (not
	 *         the king) could be moved to to remove the direct attack.
	 */
	private static long getBlockingSquares(Square activeKingLoc, PieceSquarePair attacker)
	{
		if (attacker.getPiece().isSlidingPiece()) {
			return MoveCache.getMove(attacker.getSquare(), activeKingLoc).getInducedCord() ^ activeKingLoc.bitboard;
		} else {
			return attacker.getSquare().bitboard;
		}
	}

	/**
	 * Calculates a List of the passive pieces (along with the squares they reside
	 * at) which are directly attacking the king on the active side in the parameter
	 * state. I.e. those which are causing check. There can be at most two such
	 * pieces.
	 *
	 * TODO could optimize the pawn check and we don't need to consider passive
	 * king.
	 */
	private static List<PieceSquarePair> getPassiveAttackersOfActiveKing(BoardState state)
	{
		Side active = state.getActiveSide();
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		Piece activeKing = ChessPieces.of(active).last();

		long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		long kloc = pieceLocs.locationsOf(activeKing);

		List<PieceSquarePair> attackers = new ArrayList<>(2);
		PIECE_LOOP: for (Piece potentialAttacker : ChessPieces.of(active.otherSide())) {
			Flow<Square> locs = pieceLocs.iterateLocs(potentialAttacker);
			while (locs.hasNext()) {
				Square loc = locs.next();
				long attacks = potentialAttacker.getSquaresOfControl(loc, white, black);
				if (bitboardsIntersect(attacks, kloc)) {
					attackers.add(new PieceSquarePair(potentialAttacker, loc));
					if (attackers.size() == 2) {
						break PIECE_LOOP;
					}
				}
			}
		}
		return attackers;
	}

	/**
	 * @param piece
	 *            The piece located at the source square
	 * @param source
	 *            The source square for any moves generated
	 * @param bitboard
	 *            A representation of all the available target squares
	 *
	 * @return An iteration of moves from source to target for each target described
	 *         by the parameter bitboard.
	 */
	static Flow<ChessMove> bitboard2moves(Piece piece, Square source, long bitboard)
	{
		if (piece.isPawn() && source.rank == piece.getSide().penultimatePawnRank) {
			return BitboardIterator.from(bitboard)
					.flatMap(target -> PromotionMove.generateAllPossibilities(source, target));
		} else {
			return BitboardIterator.from(bitboard).map(target -> MoveCache.getMove(source, target));
		}
	}
}
