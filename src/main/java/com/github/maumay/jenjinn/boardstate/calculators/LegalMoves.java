/**
 *
 */
package com.github.maumay.jenjinn.boardstate.calculators;

import static com.github.maumay.jenjinn.bitboards.Bitboard.intersects;
import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.github.maumay.jenjinn.base.CastleZone;
import com.github.maumay.jenjinn.base.Dir;
import com.github.maumay.jenjinn.base.Side;
import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.bitboards.BitboardIterator;
import com.github.maumay.jenjinn.bitboards.Bitboards;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.DetailedPieceLocations;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jenjinn.moves.EnpassantMove;
import com.github.maumay.jenjinn.moves.MoveCache;
import com.github.maumay.jenjinn.moves.PromotionMove;
import com.github.maumay.jenjinn.pieces.ChessPieces;
import com.github.maumay.jenjinn.pieces.Piece;
import com.github.maumay.jenjinn.utils.PieceSquarePair;
import com.github.maumay.jflow.iterators.Iter;
import com.github.maumay.jflow.iterators.RichIterator;
import com.github.maumay.jflow.utils.Tup;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 */
public final class LegalMoves
{
	private static final Vec<Dir> WHITE_EP_SEARCH_DIRS = Vec.of(Dir.SW, Dir.SE);
	private static final Vec<Dir> BLACK_EP_SEARCH_DIRS = Vec.of(Dir.NW, Dir.NE);

	private LegalMoves()
	{
	}

	public static RichIterator<ChessMove> getAttacks(BoardState state)
	{
		return getLegalMoves(state, true);
	}

	public static RichIterator<ChessMove> getAllMoves(BoardState state)
	{
		return getLegalMoves(state, false);
	}

	static RichIterator<ChessMove> getLegalMoves(BoardState state, boolean forceAttacks)
	{
		Side active = state.getActiveSide(), passive = active.otherSide();
		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		long passivePieceLocs = pieceLocs.getSideLocations(passive);
		Vec<Piece> activePieces = ChessPieces.of(active);
		Piece activeKing = activePieces.last();
		Square kingLoc = pieceLocs.iterateLocs(activeKing).next();
		long passiveControl = SquareControl.calculate(state, passive);
		PinnedPieceCollection pinnedPieces = PinnedPieces.in(state);

		boolean inCheck = intersects(passiveControl, kingLoc.bitboard);
		boolean castlingAllowed = !inCheck && !forceAttacks
				&& state.getCastlingStatus().getStatusFor(active) == null;
		RichIterator<ChessMove> moves = castlingAllowed
				? getCastlingMoves(state, passiveControl)
				: Iter.empty();
		long allowedMoveArea = forceAttacks ? passivePieceLocs : Bitboards.universal();

		if (inCheck) {
			List<PieceSquarePair> attackers = getPassiveAttackersOfActiveKing(state);
			for (PieceSquarePair attacker : attackers) {
				Piece piece = attacker.getPiece();
				if (piece.isSlidingPiece()) {
					boolean whiteAttack = piece.isWhite();
					long kloc = kingLoc.bitboard;
					long white = whiteAttack ? pieceLocs.getWhiteLocations()
							: pieceLocs.getWhiteLocations() ^ kloc;
					long black = whiteAttack ? pieceLocs.getBlackLocations() ^ kloc
							: pieceLocs.getBlackLocations();
					passiveControl |= piece.getSquaresOfControl(attacker.getSquare(),
							white, black);
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
					moves = moves.append(
							getEnpassantCheckEscape(attacker, state, pinnedPieces));
				}
			}
		}

		// Add moves from non king pieces
		long faa = allowedMoveArea;
		moves = moves.append(activePieces.iter().take(5)
				.flatMap(p -> getNonKingMoves(state, p, pinnedPieces, faa)));

		// Add king moves
		long kingConstraint = forceAttacks ? ~passiveControl & passivePieceLocs
				: ~passiveControl;
		moves = moves.append(getMovesForKing(state, kingLoc, kingConstraint));

		return moves;
	}

	private static RichIterator<ChessMove> getCastlingMoves(BoardState state,
			long passiveControl)
	{
		Side activeSide = state.getActiveSide();
		Predicate<CastleZone> sideFilter = activeSide.isWhite() ? z -> z.isWhiteZone()
				: z -> !z.isWhiteZone();
		Set<CastleZone> allRights = state.getCastlingStatus().getCastlingRights();
		RichIterator<CastleZone> availableRights = Iter.over(allRights)
				.filter(sideFilter);
		long allPieces = state.getPieceLocations().getAllLocations();
		RichIterator<CastleZone> legalAvailableRights = availableRights.filter(zone -> {
			long reqClearArea = zone.getRequiredFreeSquares();
			long reqUncontrolledArea = zone.getRequiredUncontrolledSquares();
			return !intersects(reqClearArea, allPieces)
					&& !intersects(passiveControl, reqUncontrolledArea);
		});
		return legalAvailableRights.map(MoveCache::getMove);
	}

	private static RichIterator<ChessMove> getMovesForKing(BoardState state,
			Square kingLoc, long areaConstraint)
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
	static RichIterator<ChessMove> getNonKingMoves(BoardState state, Piece piece,
			PinnedPieceCollection pinnedPieces, long overallAreaConstraint)
	{
		if (Long.bitCount(overallAreaConstraint) == 0) {
			return Iter.empty();
		}

		DetailedPieceLocations pieceLocs = state.getPieceLocations();
		long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		Tup<Vec<Square>, Vec<Square>> pinnedPartition = pieceLocs.iterateLocs(piece)
				.toVec().partition(pinnedPieces::containsLocation);

		RichIterator<ChessMove> pinnedContribution = pinnedPartition._1.iter()
				.flatMap(square -> {
					long areaCons = pinnedPieces.getConstraintAreaOfPieceAt(square)
							& overallAreaConstraint;
					return bitboard2moves(piece, square,
							piece.getMoves(square, white, black) & areaCons);
				});

		RichIterator<ChessMove> notPinnedContributions = pinnedPartition._2.iter()
				.flatMap(square -> {
					long areaCons = overallAreaConstraint;
					return bitboard2moves(piece, square,
							piece.getMoves(square, white, black) & areaCons);
				});

		RichIterator<ChessMove> allContributions = notPinnedContributions
				.append(pinnedContribution);

		if (piece.isPawn() && state.hasEnpassantAvailable()) {
			Square ep = state.getEnPassantSquare();
			long plocs = pieceLocs.locationsOf(piece);
			Vec<Dir> searchDirs = piece.isWhite() ? WHITE_EP_SEARCH_DIRS
					: BLACK_EP_SEARCH_DIRS;
			RichIterator<ChessMove> epContribution = searchDirs.iter().map(ep::next)
					.filter(Optional::isPresent).map(Optional::get).filter(sq -> {
						if (intersects(plocs, sq.bitboard)) {
							if (pinnedPieces.containsLocation(sq)) {
								long areaCons = pinnedPieces
										.getConstraintAreaOfPieceAt(sq);
								return intersects(areaCons, ep.bitboard);
							} else {
								return true;
							}
						} else {
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
	static RichIterator<ChessMove> getEnpassantCheckEscape(PieceSquarePair attacker,
			BoardState state, PinnedPieceCollection pinnedPieces)
	{
		Square attackerLoc = attacker.getSquare(),
				enpassantSquare = state.getEnPassantSquare();
		assert enpassantSquare != null;
		assert attacker.getPiece().isPawn();
		assert Math.abs(attackerLoc.ordinal() - enpassantSquare.ordinal()) == 8;

		Side active = state.getActiveSide();
		Piece activePawn = ChessPieces.of(active).head();
		long activePawnLocs = state.getPieceLocations().locationsOf(activePawn);
		Vec<Dir> searchDirs = active.isWhite() ? WHITE_EP_SEARCH_DIRS
				: BLACK_EP_SEARCH_DIRS;

		return searchDirs.iter().map(enpassantSquare::next).filter(Optional::isPresent)
				.map(Optional::get).filter(square -> {
					if (intersects(activePawnLocs, square.bitboard)) {
						if (pinnedPieces.containsLocation(square)) {
							return intersects(
									pinnedPieces.getConstraintAreaOfPieceAt(square),
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
	 * @param activeKingLoc - Assumed location of the active side king
	 * @param attacker      - Piece and location assumed to be directly attacking
	 *                      the active king.
	 * @return the squares (represented by a bitboard) which an active piece (not
	 *         the king) could be moved to to remove the direct attack.
	 */
	private static long getBlockingSquares(Square activeKingLoc, PieceSquarePair attacker)
	{
		if (attacker.getPiece().isSlidingPiece()) {
			return MoveCache.getMove(attacker.getSquare(), activeKingLoc).getInducedCord()
					^ activeKingLoc.bitboard;
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
			RichIterator<Square> locs = pieceLocs.iterateLocs(potentialAttacker);
			while (locs.hasNext()) {
				Square loc = locs.next();
				long attacks = potentialAttacker.getSquaresOfControl(loc, white, black);
				if (intersects(attacks, kloc)) {
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
	 * @param piece    The piece located at the source square
	 * @param source   The source square for any moves generated
	 * @param bitboard A representation of all the available target squares
	 *
	 * @return An iteration of moves from source to target for each target described
	 *         by the parameter bitboard.
	 */
	static RichIterator<ChessMove> bitboard2moves(Piece piece, Square source,
			long bitboard)
	{
		if (piece.isPawn() && source.rank == piece.getSide().penultimatePawnRank) {
			return BitboardIterator.from(bitboard).flatMap(
					target -> PromotionMove.generateAllPossibilities(source, target));
		} else {
			return BitboardIterator.from(bitboard)
					.map(target -> MoveCache.getMove(source, target));
		}
	}
}
