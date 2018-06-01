/**
 *
 */
package jenjinn.engine.moves;

import static java.util.Collections.unmodifiableSet;
import static jenjinn.engine.enums.CastleZone.BLACK_KINGSIDE;
import static jenjinn.engine.enums.CastleZone.BLACK_QUEENSIDE;
import static jenjinn.engine.enums.CastleZone.WHITE_KINGSIDE;
import static jenjinn.engine.enums.CastleZone.WHITE_QUEENSIDE;

import java.util.EnumSet;
import java.util.Set;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.DataForReversingMove;
import jenjinn.engine.enums.CastleZone;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.DevelopmentPiece;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 *
 */
public final class CastleMove extends AbstractChessMove
{
	private static final Set<CastleZone> WHITE_REMOVALS = unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE, WHITE_KINGSIDE));
	private static final Set<CastleZone> BLACK_REMOVALS = unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE, BLACK_KINGSIDE));

	private final CastleZone wrappedZone;
	private final Set<CastleZone> rightsRemovedByThisMove;

	public CastleMove(final CastleZone wrappedZone)
	{
		super(wrappedZone.getKingSource(), wrappedZone.getKingTarget());
		this.wrappedZone = wrappedZone;
		this.rightsRemovedByThisMove = wrappedZone.isWhiteZone()? WHITE_REMOVALS : BLACK_REMOVALS;
	}

	@Override
	void updateDevelopedPieces(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		unmakeDataStore.setPieceDeveloped(null);
	}

	@Override
	void updatePieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final Side currentActiveSide = state.getActiveSide();
		final boolean whiteActive = currentActiveSide.isWhite();

		final ChessPiece king = whiteActive ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
		state.getPieceLocations().removePieceAt(wrappedZone.getKingSource(), king);
		state.getPieceLocations().addPieceAt(wrappedZone.getKingTarget(), king);

		final ChessPiece rook = whiteActive ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK;
		state.getPieceLocations().removePieceAt(wrappedZone.getRookSource(), rook);
		state.getPieceLocations().addPieceAt(wrappedZone.getRookTarget(), rook);

		unmakeDataStore.setPieceTaken(null);

		// handle enpassant
		unmakeDataStore.setDiscardedEnpassantSquare(state.getEnPassantSquare());
		state.setEnPassantSquare(null);
		// handle half move clock
		unmakeDataStore.setDiscardedHalfMoveClock(state.getHalfMoveClock().getValue());
		state.getHalfMoveClock().incrementValue();
	}

	@Override
	void resetPieceLocations(final BoardState state, final DataForReversingMove unmakeDataStore)
	{
		final boolean whiteActive = state.getActiveSide().isWhite();
		final ChessPiece king = whiteActive ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING;
		state.getPieceLocations().removePieceAt(wrappedZone.getKingTarget(), king);
		state.getPieceLocations().addPieceAt(wrappedZone.getKingSource(), king);

		final ChessPiece rook = whiteActive ? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK;
		state.getPieceLocations().removePieceAt(wrappedZone.getRookTarget(), rook);
		state.getPieceLocations().addPieceAt(wrappedZone.getRookSource(), rook);
	}

	@Override
	Set<CastleZone> getAllRightsToBeRemoved()
	{
		return rightsRemovedByThisMove;
	}

	@Override
	DevelopmentPiece getPieceDeveloped()
	{
		return null;
	}

	public CastleZone getWrappedZone()
	{
		return wrappedZone;
	}

	@Override
	public String toString()
	{
		return new StringBuilder("CastleMove[zone=")
				.append(wrappedZone.name())
				.append("]")
				.toString();
	}
}
