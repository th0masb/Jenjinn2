/**
 *
 */
package jenjinn.engine.boardstate;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;
import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.take;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 */
public final class DetailedPieceLocations
{
	private final long[] pieceLocations;
	private long whiteLocations, blackLocations;

	public DetailedPieceLocations(final long[] pieceLocations)
	{
		if (pieceLocations.length != 12) {
			throw new IllegalArgumentException();
		}
		this.pieceLocations = pieceLocations;
		whiteLocations = bitwiseOr(take(6, pieceLocations));
		blackLocations = bitwiseOr(drop(6, pieceLocations));
	}

	public long getWhiteLocations()
	{
		return whiteLocations;
	}

	public long getBlackLocations()
	{
		return blackLocations;
	}

	public long getPieceLocations(ChessPiece piece)
	{
		return pieceLocations[piece.ordinal()];
	}

	public void addPieceAt(BoardSquare location, ChessPiece pieceToAdd)
	{
		final long newLocation = location.asBitboard();
		assert !bitboardsIntersect(pieceLocations[pieceToAdd.ordinal()], newLocation);
		pieceLocations[pieceToAdd.ordinal()] |= newLocation;
		if (pieceToAdd.isWhite()) {
			assert bitboardsIntersect(whiteLocations, newLocation);
			whiteLocations |= newLocation;
		}
		else {
			assert bitboardsIntersect(blackLocations, newLocation);
			blackLocations |= newLocation;
		}
	}

	public void removePieceAt(BoardSquare location, ChessPiece pieceToAdd)
	{
		final long newLocation = location.asBitboard();
		assert bitboardsIntersect(pieceLocations[pieceToAdd.ordinal()], newLocation);
		pieceLocations[pieceToAdd.ordinal()] ^= newLocation;
		if (pieceToAdd.isWhite()) {
			assert bitboardsIntersect(whiteLocations, newLocation);
			whiteLocations ^= newLocation;
		}
		else {
			assert bitboardsIntersect(blackLocations, newLocation);
			blackLocations |= location.asBitboard();
		}
	}

	public ChessPiece getPieceAt(final BoardSquare square)
	{
		final long squareAsBitboard = square.asBitboard();
		for (int i = 0; i < 12; i++) {
			if (bitboardsIntersect(pieceLocations[i], squareAsBitboard)) {
				return ChessPieces.fromIndex(i);
			}
		}
		return null;
	}

	public ChessPiece getPieceAt(final BoardSquare square, final Side side)
	{
		final long squareAsBitboard = square.asBitboard();
		final int lowerBound = side.isWhite() ? 0 : 6, upperBound = lowerBound + 6;
		for (int i = lowerBound; i < upperBound; i++) {
			if (bitboardsIntersect(pieceLocations[i], squareAsBitboard)) {
				return ChessPieces.fromIndex(i);
			}
		}
		return null;
	}

	public static DetailedPieceLocations getStartLocations()
	{
		final long[] startLocs = new long[] {
				0b11111111L << 8,
				0b01000010L,
				0b00100100L,
				0b10000001L,
				0b00010000L,
				0b00001000L,

				0b11111111L << 48,
				0b01000010L << 56,
				0b00100100L << 56,
				0b10000001L << 56,
				0b00010000L << 56,
				0b00001000L << 56,
		};
		return new DetailedPieceLocations(startLocs);
	}
}
