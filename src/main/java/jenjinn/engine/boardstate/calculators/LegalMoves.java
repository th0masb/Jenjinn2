/**
 *
 */
package jenjinn.engine.boardstate.calculators;

import static java.lang.Math.abs;
import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.sizeOf;
import static xawd.jflow.utilities.CollectionUtil.tail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.bitboards.Bitboards;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;
import jenjinn.engine.misc.PieceSquarePair;
import jenjinn.engine.misc.PinnedPieceCollection;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnpassantMove;
import jenjinn.engine.moves.MoveCache;
import jenjinn.engine.moves.PromotionMove;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.EmptyIteration;
import xawd.jflow.iterators.factories.Iterate;
import xawd.jflow.iterators.misc.PredicatePartition;

/**
 * @author ThomasB
 */
public final class LegalMoves
{
	private static final List<Direction> WHITE_EP_SEARCH_DIRS = asList(Direction.SW, Direction.SE);
	private static final List<Direction> BLACK_EP_SEARCH_DIRS = asList(Direction.NW, Direction.NE);

	private LegalMoves()
	{
	}

	public static Flow<ChessMove> getAttacks(final BoardState state)
	{
		return getLegalMoves(state, true);
	}

	public static Flow<ChessMove> getMoves(final BoardState state)
	{
		return getLegalMoves(state, false);
	}

	static Flow<ChessMove> getLegalMoves(final BoardState state, final boolean forceAttacks)
	{
		final Side active = state.getActiveSide(), passive = active.otherSide();
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long passivePieceLocs = pieceLocs.getSideLocations(passive);
		final List<ChessPiece> activePieces = ChessPieces.ofSide(active);
		final ChessPiece activeKing = tail(activePieces);
		final BoardSquare kingLoc = pieceLocs.iterateLocs(activeKing).next();
		long passiveControl = SquareControl.calculate(state, passive);
		final PinnedPieceCollection pinnedPieces = PinnedPieces.in(state);

		final boolean inCheck = bitboardsIntersect(passiveControl, kingLoc.asBitboard());
		final boolean castlingAllowed = !inCheck && !forceAttacks
				&& state.getCastlingStatus().getStatusFor(active) == null;
		Flow<ChessMove> moves = castlingAllowed ? getCastlingMoves(state, passiveControl) : EmptyIteration.ofObjects();
		long allowedMoveArea = forceAttacks ? passivePieceLocs : Bitboards.universal();

		if (inCheck) {
			final List<PieceSquarePair> attackers = getPassiveAttackersOfActiveKing(state);
			for (final PieceSquarePair attacker : attackers) {
				final ChessPiece piece = attacker.getPiece();
				if (piece.isSlidingPiece()) {
					final boolean whiteAttack = piece.isWhite();
					final long kloc = kingLoc.asBitboard();
					final long white = whiteAttack ? pieceLocs.getWhiteLocations() : pieceLocs.getWhiteLocations() ^ kloc;
					final long black = whiteAttack ? pieceLocs.getBlackLocations() ^ kloc : pieceLocs.getBlackLocations();
					passiveControl |= piece.getSquaresOfControl(attacker.getSquare(), white, black);
				}
			}
			if (sizeOf(attackers) > 1) {
				allowedMoveArea = 0L;
			} else {
				final PieceSquarePair attacker = head(attackers);
				allowedMoveArea &= getBlockingSquares(kingLoc, attacker);
				final BoardSquare ep = state.getEnPassantSquare(), attsq = attacker.getSquare();
				if (ep != null && abs(ep.ordinal() - attsq.ordinal()) == 8) {
					assert attacker.getPiece().isPawn();
					moves = moves.append(getEnpassantCheckEscape(attacker, state, pinnedPieces));
				}
			}
		}

		// Add moves from non king pieces
		final long faa = allowedMoveArea;
		moves = moves.append(
				Iterate.reverseOver(activePieces).drop(1).flatten(p -> getNonKingMoves(state, p, pinnedPieces, faa)));

		// Add king moves
		final long kingConstraint = forceAttacks ? ~passiveControl & passivePieceLocs : ~passiveControl;
		moves = moves.append(getMovesForKing(state, kingLoc, kingConstraint));

		return moves;
	}

	private static Flow<ChessMove> getCastlingMoves(final BoardState state, final long passiveControl)
	{
		final Side activeSide = state.getActiveSide();
		final Predicate<CastleZone> sideFilter = activeSide.isWhite() ? z -> z.isWhiteZone() : z -> !z.isWhiteZone();
		final Set<CastleZone> allRights = state.getCastlingStatus().getCastlingRights();
		final Flow<CastleZone> availableRights = Iterate.over(allRights).filter(sideFilter);
		final long allPieces = state.getPieceLocations().getAllLocations();
		final Flow<CastleZone> legalAvailableRights = availableRights.filter(zone -> {
			final long reqClearArea = zone.getRequiredFreeSquares();
			final long reqUncontrolledArea = zone.getRequiredUncontrolledSquares();
			return !bitboardsIntersect(reqClearArea, allPieces) && !bitboardsIntersect(passiveControl, reqUncontrolledArea);
		});
		return legalAvailableRights.map(MoveCache::getMove);
	}

	private static Flow<ChessMove> getMovesForKing(final BoardState state, final BoardSquare kingLoc,
			final long areaConstraint)
	{
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		final ChessPiece activeKing = ChessPieces.king(state.getActiveSide());
		final long kingMoves = activeKing.getMoves(kingLoc, white, black) & areaConstraint;
		return bitboard2moves(activeKing, kingLoc, kingMoves);
	}

	/*
	 * Think this is all we need for both blocking and moving non-king pieces. If
	 * the king is not in check then the overallAreaConstraint is the universal
	 * bitboard.
	 */
	static Flow<ChessMove> getNonKingMoves(final BoardState state, final ChessPiece piece,
			final PinnedPieceCollection pinnedPieces, final long overallAreaConstraint)
	{
		if (Long.bitCount(overallAreaConstraint) == 0) {
			return EmptyIteration.ofObjects();
		}

		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		final PredicatePartition<BoardSquare> pinnedPartition = pieceLocs.iterateLocs(piece)
				.partition(pinnedPieces::containsLocation);

		final Flow<ChessMove> notPinnedContributions = pinnedPartition.getRejected().flatten(square -> {
			final long areaCons = overallAreaConstraint;
			return bitboard2moves(piece, square, piece.getMoves(square, white, black) & areaCons);
		});

		final Flow<ChessMove> pinnedContribution = pinnedPartition.getAccepted().flatten(square -> {
			final long areaCons = pinnedPieces.getConstraintAreaOfPieceAt(square) & overallAreaConstraint;
			return bitboard2moves(piece, square, piece.getMoves(square, white, black) & areaCons);
		});

		final Flow<ChessMove> allContributions = notPinnedContributions.append(pinnedContribution);

		if (piece.isPawn() && state.hasEnpassantAvailable()) {
			final BoardSquare ep = state.getEnPassantSquare();
			final long plocs = pieceLocs.locationsOf(piece);
			final List<Direction> searchDirs = piece.isWhite() ? WHITE_EP_SEARCH_DIRS : BLACK_EP_SEARCH_DIRS;
			final Flow<ChessMove> epContribution = Iterate.over(searchDirs).map(ep::getNextSquareInDirection)
					.filter(sq -> {
						if (sq != null && bitboardsIntersect(plocs, sq.asBitboard())) {
							if (pinnedPieces.containsLocation(sq)) {
								final long areaCons = pinnedPieces.getConstraintAreaOfPieceAt(sq);
								return bitboardsIntersect(areaCons, ep.asBitboard());
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
	static Flow<ChessMove> getEnpassantCheckEscape(final PieceSquarePair attacker, final BoardState state,
			final PinnedPieceCollection pinnedPieces)
	{
		final BoardSquare attackerLoc = attacker.getSquare(), enpassantSquare = state.getEnPassantSquare();
		assert enpassantSquare != null;
		assert attacker.getPiece().isPawn();
		assert Math.abs(attackerLoc.ordinal() - enpassantSquare.ordinal()) == 8;

		final Side active = state.getActiveSide();
		final ChessPiece activePawn = ChessPieces.pawn(active);
		final long activePawnLocs = state.getPieceLocations().locationsOf(activePawn);
		final List<Direction> searchDirs = active.isWhite() ? WHITE_EP_SEARCH_DIRS : BLACK_EP_SEARCH_DIRS;

		return Iterate.over(searchDirs).map(enpassantSquare::getNextSquareInDirection).filter(x -> x != null)
				.filter(square -> {
					if (bitboardsIntersect(activePawnLocs, square.asBitboard())) {
						if (pinnedPieces.containsLocation(square)) {
							return bitboardsIntersect(pinnedPieces.getConstraintAreaOfPieceAt(square),
									enpassantSquare.asBitboard());
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
	private static long getBlockingSquares(final BoardSquare activeKingLoc, final PieceSquarePair attacker)
	{
		if (attacker.getPiece().isSlidingPiece()) {
			return MoveCache.getMove(attacker.getSquare(), activeKingLoc).getInducedCord() ^ activeKingLoc.asBitboard();
		} else {
			return attacker.getSquare().asBitboard();
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
	private static List<PieceSquarePair> getPassiveAttackersOfActiveKing(final BoardState state)
	{
		final Side active = state.getActiveSide();
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final ChessPiece activeKing = ChessPieces.king(active);

		final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		final long kloc = pieceLocs.locationsOf(activeKing);

		final List<PieceSquarePair> attackers = new ArrayList<>(2);
		PIECE_LOOP: for (final ChessPiece potentialAttacker : ChessPieces.ofSide(active.otherSide())) {
			final Flow<BoardSquare> locs = pieceLocs.iterateLocs(potentialAttacker);
			while (locs.hasNext()) {
				final BoardSquare loc = locs.next();
				final long attacks = potentialAttacker.getSquaresOfControl(loc, white, black);
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
	static Flow<ChessMove> bitboard2moves(final ChessPiece piece, final BoardSquare source, final long bitboard)
	{
		if (piece.isPawn() && source.rank() == piece.getSide().getPenultimatePawnRank()) {
			return BitboardIterator.from(bitboard)
					.flatten(target -> PromotionMove.generateAllPossibilities(source, target));
		} else {
			return BitboardIterator.from(bitboard).map(target -> MoveCache.getMove(source, target));
		}
	}
}
