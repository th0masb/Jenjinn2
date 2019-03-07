/**
 *
 */
package jenjinn.moves;

import static java.lang.Math.abs;
import static java.util.Collections.unmodifiableSet;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import jenjinn.base.CastleZone;
import jenjinn.base.DevelopmentPiece;
import jenjinn.base.Dir;
import jenjinn.base.Side;
import jenjinn.base.Square;
import jenjinn.boardstate.BoardState;
import jenjinn.boardstate.MoveReversalData;
import jenjinn.pieces.Piece;
import jflow.seq.Seq;

/**
 * @author ThomasB
 *
 */
public final class StandardMove extends AbstractChessMove
{
	private final Set<CastleZone> rightsRemovedByThisMove;
	private final long inducedCord;

	public StandardMove(Square start, Square target)
	{
		super(start, target);
		rightsRemovedByThisMove = initRightsRemoved();
		inducedCord = initInducedCord();
	}

	private long initInducedCord()
	{
		Optional<Dir> dir = Dir.ofLineBetween(getSource(), getTarget());
		if (dir.isPresent()) {
			Seq<Square> squares = getSource().getAllSquares(dir.get(), 10);
			return squares.flow()
					.takeWhile(sq -> sq != getTarget())
					.insert(getSource())
					.append(getTarget())
					.mapToLong(sq -> sq.bitboard)
					.fold(0L, (a, b) -> a ^ b);
		}
		else {
			return Long.MIN_VALUE;
		}
	}

	private Set<CastleZone> initRightsRemoved()
	{
		Map<Square, Set<CastleZone>> rightsSets = MoveConstants.STANDARDMOVE_RIGHTS_SETS;

		Predicate<Object> p = rightsSets::containsKey;
		if (p.test(getSource()) || p.test(getTarget())) {
			Set<CastleZone> x = p.test(getSource())? rightsSets.get(getSource()) : EnumSet.noneOf(CastleZone.class);
			Set<CastleZone> y = p.test(getTarget())? rightsSets.get(getTarget()) : EnumSet.noneOf(CastleZone.class);
			Set<CastleZone> mutableResult = EnumSet.noneOf(CastleZone.class);
			mutableResult.addAll(x);
			mutableResult.addAll(y);
			return unmodifiableSet(mutableResult);
		}
		else {
			return MoveConstants.EMPTY_RIGHTS_SET;
		}
	}

	public long getInducedCord()
	{
		return inducedCord;
	}

	@Override
	void updatePieceLocations(BoardState state, MoveReversalData unmakeDataStore)
	{
		Square source = getSource(), target = getTarget();
		Side activeSide = state.getActiveSide(), passiveSide = activeSide.otherSide();
		Piece movingPiece = state.getPieceLocations().getPieceAt(source, activeSide);
		Piece removedPiece = state.getPieceLocations().getPieceAt(target, passiveSide);
		boolean pieceWasRemoved = removedPiece != null;

		// Update locations
		unmakeDataStore.setPieceTaken(removedPiece);
		state.getPieceLocations().removePieceAt(source, movingPiece);
		state.getPieceLocations().addPieceAt(target, movingPiece);
		if (pieceWasRemoved) {
			state.getPieceLocations().removePieceAt(target, removedPiece);
		}

		//---------------------------------------------
		// Update enpassant stuff
		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		boolean pawnWasMoved = movingPiece.isPawn();
		if (pawnWasMoved) {
			int squareOrdinalDifference = target.ordinal() - source.ordinal();
			if (abs(squareOrdinalDifference) == 16) {
				Square newEnpassantSquare = Square.of(source.ordinal() + squareOrdinalDifference/2);
				state.setEnPassantSquare(newEnpassantSquare);
			}
		}
		//---------------------------------------------
		// Update half move clock
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		if (pawnWasMoved || pieceWasRemoved) {
			state.getHalfMoveClock().resetValue();
		}
		else {
			state.getHalfMoveClock().incrementValue();
		}
	}

	@Override
	void resetPieceLocations(BoardState state, MoveReversalData unmakeDataStore)
	{
		// Reset locations
		Piece previouslyMovedPiece = state.getPieceLocations().getPieceAt(getTarget(), state.getActiveSide());
		state.getPieceLocations().removePieceAt(getTarget(), previouslyMovedPiece);
		state.getPieceLocations().addPieceAt(getSource(), previouslyMovedPiece);

		Piece previouslyRemovedPiece = unmakeDataStore.getPieceTaken();
		if (previouslyRemovedPiece != null) {
			state.getPieceLocations().addPieceAt(getTarget(), previouslyRemovedPiece);
		}
	}

	@Override
	Set<CastleZone> getAllRightsToBeRemoved()
	{
		return rightsRemovedByThisMove;
	}

	@Override
	DevelopmentPiece getPieceDeveloped()
	{
		return DevelopmentPiece.fromStartSquare(getSource());
	}

	@Override
	public String toCompactString()
	{
		return new StringBuilder("S")
				.append(getSource().name())
				.append(getTarget().name())
				.toString();
	}
}
