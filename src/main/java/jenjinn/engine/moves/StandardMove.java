/**
 *
 */
package jenjinn.engine.moves;

import static java.lang.Math.abs;
import static java.util.Collections.unmodifiableSet;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.CastleZone;
import jenjinn.engine.base.DevelopmentPiece;
import jenjinn.engine.base.Direction;
import jenjinn.engine.base.Side;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.MoveReversalData;
import jenjinn.engine.pieces.ChessPiece;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public final class StandardMove extends AbstractChessMove
{
	private final Set<CastleZone> rightsRemovedByThisMove;
	private final long inducedCord;

	public StandardMove(BoardSquare start, BoardSquare target)
	{
		super(start, target);
		rightsRemovedByThisMove = initRightsRemoved();
		inducedCord = initInducedCord();
	}

	private long initInducedCord()
	{
		Optional<Direction> dir = Direction.ofLineBetween(getSource(), getTarget());
		if (dir.isPresent()) {
			List<BoardSquare> squares = getSource().getAllSquaresInDirections(dir.get(), 10);
			return Iterate.over(squares)
					.takeWhile(sq -> sq != getTarget())
					.insert(getSource())
					.append(getTarget())
					.mapToLong(BoardSquare::asBitboard)
					.fold(0L, (a, b) -> a ^ b);
		}
		else {
			return Long.MIN_VALUE;
		}
	}

	private Set<CastleZone> initRightsRemoved()
	{
		Map<BoardSquare, Set<CastleZone>> rightsSets = MoveConstants.STANDARDMOVE_RIGHTS_SETS;

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
		BoardSquare source = getSource(), target = getTarget();
		Side activeSide = state.getActiveSide(), passiveSide = activeSide.otherSide();
		ChessPiece movingPiece = state.getPieceLocations().getPieceAt(source, activeSide);
		ChessPiece removedPiece = state.getPieceLocations().getPieceAt(target, passiveSide);
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
				BoardSquare newEnpassantSquare = BoardSquare.of(source.ordinal() + squareOrdinalDifference/2);
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
		ChessPiece previouslyMovedPiece = state.getPieceLocations().getPieceAt(getTarget(), state.getActiveSide());
		state.getPieceLocations().removePieceAt(getTarget(), previouslyMovedPiece);
		state.getPieceLocations().addPieceAt(getSource(), previouslyMovedPiece);

		ChessPiece previouslyRemovedPiece = unmakeDataStore.getPieceTaken();
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
