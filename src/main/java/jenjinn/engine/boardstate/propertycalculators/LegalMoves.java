/**
 *
 */
package jenjinn.engine.boardstate.propertycalculators;

import static java.lang.Math.abs;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static xawd.jflow.utilities.CollectionUtil.head;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.bitboards.BitboardIterator;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DetailedPieceLocations;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;
import jenjinn.engine.misc.PieceSquarePair;
import jenjinn.engine.misc.PinnedPieceCollection;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnpassantMove;
import jenjinn.engine.moves.PromotionMove;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.EmptyIteration;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 */
public final class LegalMoves {

	private LegalMoves() {}

	public static List<ChessMove> forState(final BoardState state)
	{
		final Side active = state.getActiveSide(), passive = active.otherSide();
		final long passiveControl = SquareControl.calculate(state, passive);
		final long activeKingLoc = state.getPieceLocations().locationOverviewOf(ChessPieces.king(active));
		if (bitboardsIntersect(passiveControl, activeKingLoc)) {
			return getMovesOutOfCheck(state, passiveControl);
		}
		else {
			return getLegalMoves(state, passiveControl);
		}
	}

	private static List<ChessMove> getLegalMoves(final BoardState state, final long passiveControl)
	{
		/*
		 * We must take into account possible pins, Standard sliding moves induce 'cord' bitboards which
		 * can be used to check for pins.
		 */
		throw new RuntimeException();
	}

	/**
	 * @param state - The source state
	 * @param passiveControl - A bitboard representing all squares controlled by the passive
	 * side in the source state.
	 *
	 * @return a list of every legal move available assuming the active king is under direct attack.
	 * I.e. that its location intersects the passive side control.
	 */
	private static List<ChessMove> getMovesOutOfCheck(final BoardState state, final long passiveControl)
	{
		final Side active = state.getActiveSide();
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final ChessPiece activeKing = ChessPieces.king(active);
		final BoardSquare activeKingLoc = pieceLocs.iterateLocs(activeKing).next();
		final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();

		final List<PieceSquarePair> attackers = getPassiveAttackersOfActiveKing(state);

		final long kingMovesBitboard = activeKing.getMoves(activeKingLoc, white, black) & ~passiveControl;
		Flow<ChessMove> allMoves = bitboard2moves(activeKing, activeKingLoc, kingMovesBitboard);

		// King can move or we can move a piece to a blocking square (respecting constraint of pins)
		// if attacker was pawn who just created enpassant square we also check for enpassant moves.

		if (attackers.size() == 1) {
			final PieceSquarePair attacker = head(attackers);
			final long blockingSquares = getBlockingSquares(activeKingLoc, attacker);

			final PinnedPieceCollection pinnedActivePieces = PinnedPieces.in(state);
			final Predicate<BoardSquare> notPinned = square -> !pinnedActivePieces.containsLocation(square);

			final Flow<ChessMove> blocksFromFreePieces = Iterate.over(ChessPieces.ofSide(active))
					.flatten(piece ->
					pieceLocs.iterateLocs(piece).filter(notPinned)
					.flatten(sq -> bitboard2moves(piece, sq, piece.getMoves(sq, white, black) & blockingSquares))
					);

			final Flow<ChessMove> blocksFromPinnedPieces = pinnedActivePieces.iterator().flatten(pinned ->
			{
				final ChessPiece piece = pieceLocs.getPieceAt(pinned.getLocation(), active);
				final long constraint = blockingSquares & pinned.getConstrainedArea();
				final long moves = piece.getMoves(pinned.getLocation(), white, black) & constraint;
				return bitboard2moves(piece, pinned.getLocation(), moves);
			});

			allMoves = allMoves
					.append(blocksFromFreePieces)
					.append(blocksFromPinnedPieces)
					.append(getEnpassantCheckEscape(attacker, state, pinnedActivePieces));
		}

		return allMoves.toList();
	}

	/**
	 * In the unbelievably unlikely situation that a passive pawn which has just moved
	 * forward two is attacking the active king (causing check) we must additionally check
	 * for enpassant escape moves from the active pawns.
	 */
	private static Flow<ChessMove> getEnpassantCheckEscape(final PieceSquarePair attacker, final BoardState state, final PinnedPieceCollection pinnedPieces)
	{
		final BoardSquare attackerLoc = attacker.getSquare(), enpassantSquare = state.getEnPassantSquare();
		// Very, very, very, very extremely rare that we will go into here...
		if (enpassantSquare != null && attacker.getPiece().isPawn() && abs(attackerLoc.ordinal() - enpassantSquare.ordinal()) == 8)
		{
			final Side active = state.getActiveSide();
			final ChessPiece activePawn = ChessPieces.pawn(active);
			final long activePawnLocs = state.getPieceLocations().locationOverviewOf(activePawn);
			final BoardSquare leftSquare = enpassantSquare.getNextSquareInDirection(active.isWhite()? Direction.SW : Direction.NW);
			final BoardSquare rightSquare = enpassantSquare.getNextSquareInDirection(active.isWhite()? Direction.SE : Direction.NE);
			return Iterate.over(leftSquare, rightSquare)
					.filter(x -> x != null)
					.filter(square -> {
						if (bitboardsIntersect(activePawnLocs, square.asBitboard())) {
							if (pinnedPieces.containsLocation(square)) {
								return bitboardsIntersect(pinnedPieces.getConstraintAreaOfPieceAt(square), enpassantSquare.asBitboard());
							}
							else {
								return true;
							}
						}
						else {
							return false;
						}
					})
					.map(sourceSquare -> new EnpassantMove(sourceSquare, enpassantSquare));
		}
		else {
			return EmptyIteration.ofObjects();
		}
	}

	/**
	 * @param activeKingLoc - Assumed location of the active side king
	 * @param attacker - Piece and location assumed to be directly attacking the active king.
	 * @return the squares (represented by a bitboard) which an active piece (not the king)
	 * could be moved to to remove the direct attack.
	 */
	private static long getBlockingSquares(final BoardSquare activeKingLoc, final PieceSquarePair attacker)
	{
		if (attacker.getPiece().isSlidingPiece()) {
			return MoveCache.getMove(attacker.getSquare(), activeKingLoc).getInducedCord() ^ activeKingLoc.asBitboard();
		}
		else {
			return attacker.getSquare().asBitboard();
		}
	}

	/**
	 * Calculates a List of the passive pieces (along with the squares they reside at) which are
	 * directly attacking the king on the active side in the parameter state. I.e. those which
	 * are causing check. There can be at most two such pieces.
	 */
	private static List<PieceSquarePair> getPassiveAttackersOfActiveKing(final BoardState state)
	{
		final Side active = state.getActiveSide();
		final DetailedPieceLocations pieceLocs = state.getPieceLocations();
		final ChessPiece activeKing = ChessPieces.king(active);

		final long white = pieceLocs.getWhiteLocations(), black = pieceLocs.getBlackLocations();
		final long kloc = pieceLocs.locationOverviewOf(activeKing);

		final List<PieceSquarePair> attackers = new ArrayList<>(2);
		PIECE_LOOP:
			for (final ChessPiece potentialAttacker : ChessPieces.ofSide(active.otherSide())) {
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
	 * @param piece - the piece located at the source square
	 * @param source - the source square for any moves generated
	 * @param bitboard - a representation of all the available target squares
	 *
	 * @return an iteration of moves from source to target for each target
	 * described by the parameter bitboard.
	 */
	static Flow<ChessMove> bitboard2moves(final ChessPiece piece, final BoardSquare source, final long bitboard)
	{
		if (piece.isPawn() && source.rank() == piece.getSide().getPenultimatePawnRank()) {
			return BitboardIterator.from(bitboard).map(target -> new PromotionMove(source, target));
		}
		else {
			return BitboardIterator.from(bitboard).map(target -> MoveCache.getMove(source, target));
		}
	}
}
