/**
 *
 */
package jenjinn.engine.boardstate;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;
import static xawd.jflow.utilities.CollectionUtil.drop;
import static xawd.jflow.utilities.CollectionUtil.take;

import java.util.Arrays;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import jenjinn.engine.utils.ZobristHasher;

/**
 * Handles piece locations as well as tracking the positional evaluation and hash arising
 * from square, piece features.
 *
 * @author ThomasB
 */
public final class DetailedPieceLocations
{
	private final ZobristHasher hashFeatureProvider;
	private long squarePieceFeatureHash;

	private final long[] pieceLocations;
	private long whiteLocations, blackLocations;

	private final PieceSquareTables midgameTables, endgameTables;
	private int midgameEval = 0, endgameEval = 0;

	public DetailedPieceLocations(
			final long[] pieceLocations,
			final PieceSquareTables midgameTables,
			final PieceSquareTables endgameTables,
			final ZobristHasher hashFeatureProvider)
	{
		if (pieceLocations.length != 12) {
			throw new IllegalArgumentException();
		}
		this.pieceLocations = pieceLocations;
		this.whiteLocations = bitwiseOr(take(6, pieceLocations));
		this.blackLocations = bitwiseOr(drop(6, pieceLocations));
		this.midgameTables = midgameTables;
		this.endgameTables = endgameTables;
		this.midgameEval = midgameTables.evaluateLocations(pieceLocations);
		this.endgameEval = endgameTables.evaluateLocations(pieceLocations);
		this.hashFeatureProvider = hashFeatureProvider;
		this.squarePieceFeatureHash = hashFeatureProvider.hashPieceLocations(pieceLocations);
	}

	public void addPieceAt(final BoardSquare location, final ChessPiece pieceToAdd)
	{
		squarePieceFeatureHash ^= hashFeatureProvider.getSquarePieceFeature(location, pieceToAdd);
		midgameEval += midgameTables.getLocationValue(pieceToAdd, location);
		endgameEval += endgameTables.getLocationValue(pieceToAdd, location);
		final long newLocation = location.asBitboard();
		assert !bitboardsIntersect(pieceLocations[pieceToAdd.ordinal()], newLocation);
		pieceLocations[pieceToAdd.ordinal()] |= newLocation;
		if (pieceToAdd.isWhite()) {
			assert !bitboardsIntersect(whiteLocations, newLocation);
			whiteLocations |= newLocation;
		}
		else {
			assert !bitboardsIntersect(blackLocations, newLocation);
			blackLocations |= newLocation;
		}
	}

	public void removePieceAt(final BoardSquare location, final ChessPiece pieceToRemove)
	{
		squarePieceFeatureHash ^= hashFeatureProvider.getSquarePieceFeature(location, pieceToRemove);
		midgameEval -= midgameTables.getLocationValue(pieceToRemove, location);
		endgameEval -= endgameTables.getLocationValue(pieceToRemove, location);
		final long newLocation = location.asBitboard();
		assert bitboardsIntersect(pieceLocations[pieceToRemove.ordinal()], newLocation);
		pieceLocations[pieceToRemove.ordinal()] ^= newLocation;
		if (pieceToRemove.isWhite()) {
			assert bitboardsIntersect(whiteLocations, newLocation);
			whiteLocations ^= newLocation;
		}
		else {
			assert bitboardsIntersect(blackLocations, newLocation);
			blackLocations ^= location.asBitboard();
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

	public long getWhiteLocations()
	{
		return whiteLocations;
	}

	public long getBlackLocations()
	{
		return blackLocations;
	}

	public long locationsOf(final ChessPiece piece)
	{
		return pieceLocations[piece.ordinal()];
	}

	public int getMidgameEval()
	{
		return midgameEval;
	}

	public int getEndgameEval()
	{
		return endgameEval;
	}

	public ZobristHasher getHashFeatureProvider()
	{
		return hashFeatureProvider;
	}

	public long getSquarePieceFeatureHash()
	{
		return squarePieceFeatureHash;
	}

	public DetailedPieceLocations copy()
	{
		return new DetailedPieceLocations(Arrays.copyOf(pieceLocations, 12), midgameTables, endgameTables, hashFeatureProvider);
	}

	// Eclipse generated.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (blackLocations ^ (blackLocations >>> 32));
		result = prime * result + endgameEval;
		result = prime * result + ((endgameTables == null) ? 0 : endgameTables.hashCode());
		result = prime * result + ((hashFeatureProvider == null) ? 0 : hashFeatureProvider.hashCode());
		result = prime * result + midgameEval;
		result = prime * result + ((midgameTables == null) ? 0 : midgameTables.hashCode());
		result = prime * result + Arrays.hashCode(pieceLocations);
		result = prime * result + (int) (squarePieceFeatureHash ^ (squarePieceFeatureHash >>> 32));
		result = prime * result + (int) (whiteLocations ^ (whiteLocations >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DetailedPieceLocations other = (DetailedPieceLocations) obj;
		if (blackLocations != other.blackLocations)
			return false;
		if (endgameEval != other.endgameEval)
			return false;
		if (endgameTables == null) {
			if (other.endgameTables != null)
				return false;
		} else if (!endgameTables.equals(other.endgameTables))
			return false;
		if (hashFeatureProvider == null) {
			if (other.hashFeatureProvider != null)
				return false;
		} else if (!hashFeatureProvider.equals(other.hashFeatureProvider))
			return false;
		if (midgameEval != other.midgameEval)
			return false;
		if (midgameTables == null) {
			if (other.midgameTables != null)
				return false;
		} else if (!midgameTables.equals(other.midgameTables))
			return false;
		if (!Arrays.equals(pieceLocations, other.pieceLocations))
			return false;
		if (squarePieceFeatureHash != other.squarePieceFeatureHash)
			return false;
		if (whiteLocations != other.whiteLocations)
			return false;
		return true;
	}
}
