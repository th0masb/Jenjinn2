/**
 *
 */
package jenjinn.engine.moves;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.unmodifiableSet;
import static jenjinn.engine.enums.CastleZone.BLACK_KINGSIDE;
import static jenjinn.engine.enums.CastleZone.BLACK_QUEENSIDE;
import static jenjinn.engine.enums.CastleZone.WHITE_KINGSIDE;
import static jenjinn.engine.enums.CastleZone.WHITE_QUEENSIDE;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 *
 */
public final class StandardMove extends AbstractChessMove
{
	private static final Set<CastleZone> UNMODIFIABLE_EMPTY = unmodifiableSet(EnumSet.noneOf(CastleZone.class));

	private static final Map<BoardSquare, Set<CastleZone>> RIGHTS_REMOVAL_MAP;
	static
	{
		final Map<BoardSquare, Set<CastleZone>> removalMap = new HashMap<>();
		removalMap.put(BoardSquare.A1, unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE)));
		removalMap.put(BoardSquare.E1, unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE, WHITE_KINGSIDE)));
		removalMap.put(BoardSquare.H1, unmodifiableSet(EnumSet.of(WHITE_KINGSIDE)));

		removalMap.put(BoardSquare.A8, unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE)));
		removalMap.put(BoardSquare.E8, unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE, BLACK_KINGSIDE)));
		removalMap.put(BoardSquare.H8, unmodifiableSet(EnumSet.of(BLACK_KINGSIDE)));

		RIGHTS_REMOVAL_MAP = Collections.unmodifiableMap(removalMap);
	}

	private final Set<CastleZone> rightsRemovedByThisMove;
	private final long inducedCord;

	public StandardMove(final BoardSquare start, final BoardSquare target)
	{
		super(start, target);
		rightsRemovedByThisMove = initRightsRemoved();
		inducedCord = initInducedCord();
	}

	private long initInducedCord()
	{
		if (isKnightMove()) {
			return Long.MIN_VALUE;
		}
		else {
			throw new RuntimeException();
		}
	}

	private Set<CastleZone> initRightsRemoved()
	{
		final Predicate<Object> p = RIGHTS_REMOVAL_MAP::containsKey;
		if (p.test(getSource()) || p.test(getTarget())) {
			final Set<CastleZone> x = p.test(getSource())? RIGHTS_REMOVAL_MAP.get(getSource()) : EnumSet.noneOf(CastleZone.class);
			final Set<CastleZone> y = p.test(getTarget())? RIGHTS_REMOVAL_MAP.get(getTarget()) : EnumSet.noneOf(CastleZone.class);
			final Set<CastleZone> mutableResult = EnumSet.noneOf(CastleZone.class);
			mutableResult.addAll(x);
			mutableResult.addAll(y);
			return unmodifiableSet(mutableResult);
		}
		else {
			return UNMODIFIABLE_EMPTY;
		}
	}

	public boolean isKnightMove()
	{
		final int rankChange = abs(getSource().rank() - getTarget().rank());
		final int fileChange = abs(getSource().file() - getTarget().file());
		return max(rankChange, fileChange) == 2 && min(rankChange, fileChange) == 1;
	}

	public long getInducedCord()
	{
		assert !isKnightMove();
		return inducedCord;
	}

	@Override
	void updatePieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final BoardSquare source = getSource(), target = getTarget();
		final Side activeSide = state.getActiveSide(), passiveSide = activeSide.otherSide();
		final ChessPiece movingPiece = state.getPieceLocations().getPieceAt(source, activeSide);
		final ChessPiece removedPiece = state.getPieceLocations().getPieceAt(target, passiveSide);
		final boolean pieceWasRemoved = removedPiece != null;

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
		final boolean pawnWasMoved = movingPiece.isPawn();
		if (pawnWasMoved) {
			final int squareOrdinalDifference = target.ordinal() - source.ordinal();
			if (abs(squareOrdinalDifference) == 16) {
				final BoardSquare newEnpassantSquare = BoardSquare.of(source.ordinal() + squareOrdinalDifference/2);
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
	void resetPieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		// Reset locations
		final ChessPiece previouslyMovedPiece = state.getPieceLocations().getPieceAt(getTarget(), state.getActiveSide());
		state.getPieceLocations().removePieceAt(getTarget(), previouslyMovedPiece);
		state.getPieceLocations().addPieceAt(getSource(), previouslyMovedPiece);

		final ChessPiece previouslyRemovedPiece = unmakeDataStore.getPieceTaken();
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
}
