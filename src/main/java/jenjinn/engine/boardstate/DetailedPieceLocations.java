/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.List;

import jenjinn.engine.ChessPieces;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;
import jenjinn.engine.eval.piecesquaretables.PieceSquareTables;
import jenjinn.engine.utils.ZobristHasher;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.construction.Iterate;

/**
 * Handles piece locations as well as tracking the positional evaluation and hash arising
 * from (square, piece) features.
 *
 * @author ThomasB
 */
public final class DetailedPieceLocations
{
	private final ZobristHasher hashFeatureProvider;
	private long squarePieceFeatureHash;

	private final List<LocationTracker> pieceLocations;
	private long whiteLocations, blackLocations;

	private final PieceSquareTables midgameTables, endgameTables;
	private int midgameEval = 0, endgameEval = 0;

	public DetailedPieceLocations(
			final List<LocationTracker> pieceLocations,
			final PieceSquareTables midgameTables,
			final PieceSquareTables endgameTables,
			final ZobristHasher hashFeatureProvider)
	{
		if (pieceLocations.size() != 12) {
			throw new IllegalArgumentException();
		}
		this.pieceLocations = pieceLocations;
		this.whiteLocations = Iterate.over(pieceLocations).take(6).mapToLong(x -> x.allLocs()).reduce(0L, (a, b) -> a | b);
		this.blackLocations = Iterate.over(pieceLocations).drop(6).mapToLong(x -> x.allLocs()).reduce(0L, (a, b) -> a | b);
		this.midgameTables = midgameTables;
		this.endgameTables = endgameTables;
		this.midgameEval = midgameTables.evaluateLocations(pieceLocations);
		this.endgameEval = endgameTables.evaluateLocations(pieceLocations);
		this.hashFeatureProvider = hashFeatureProvider;
		this.squarePieceFeatureHash = hashFeatureProvider.hashPieceLocations(pieceLocations);
	}

	public DetailedPieceLocations(
			final long[] pieceLocations,
			final PieceSquareTables midgameTables,
			final PieceSquareTables endgameTables,
			final ZobristHasher hashFeatureProvider)
	{
		this (Iterate.over(pieceLocations).mapToObject(LocationTracker::new).toList(),
				midgameTables,
				endgameTables,
				hashFeatureProvider);
	}

	public void addPieceAt(final BoardSquare location, final ChessPiece pieceToAdd)
	{
		squarePieceFeatureHash ^= hashFeatureProvider.getSquarePieceFeature(location, pieceToAdd);
		midgameEval += midgameTables.getLocationValue(pieceToAdd, location);
		endgameEval += endgameTables.getLocationValue(pieceToAdd, location);
		pieceLocations.get(pieceToAdd.ordinal()).addLoc(location);
		final long newLocation = location.asBitboard();
		if (pieceToAdd.isWhite()) {
			whiteLocations |= newLocation;
		}
		else {
			blackLocations |= newLocation;
		}
	}

	public void removePieceAt(final BoardSquare location, final ChessPiece pieceToRemove)
	{
		squarePieceFeatureHash ^= hashFeatureProvider.getSquarePieceFeature(location, pieceToRemove);
		midgameEval -= midgameTables.getLocationValue(pieceToRemove, location);
		endgameEval -= endgameTables.getLocationValue(pieceToRemove, location);
		pieceLocations.get(pieceToRemove.ordinal()).removeLoc(location);
		final long newLocation = location.asBitboard();
		if (pieceToRemove.isWhite()) {
			whiteLocations ^= newLocation;
		}
		else {
			blackLocations ^= newLocation;
		}
	}

	public ChessPiece getPieceAt(final BoardSquare square)
	{
		for (int i = 0; i < 12; i++) {
			if (pieceLocations.get(i).contains(square)) {
				return ChessPieces.fromIndex(i);
			}
		}
		return null;
	}

	public ChessPiece getPieceAt(final BoardSquare square, final Side side)
	{
		final int lowerBound = side.isWhite() ? 0 : 6, upperBound = lowerBound + 6;
		for (int i = lowerBound; i < upperBound; i++) {
			if (pieceLocations.get(i).contains(square)) {
				return ChessPieces.fromIndex(i);
			}
		}
		return null;
	}

	public long getSideLocations(final Side query)
	{
		return query.isWhite()? whiteLocations : blackLocations;
	}

	public long getWhiteLocations()
	{
		return whiteLocations;
	}

	public long getBlackLocations()
	{
		return blackLocations;
	}

	public long getAllLocations()
	{
		return whiteLocations | blackLocations;
	}

	public long locationOverviewOf(final ChessPiece piece)
	{
		return pieceLocations.get(piece.ordinal()).allLocs();
	}

	public Flow<BoardSquare> iterateLocs(final ChessPiece piece)
	{
		return pieceLocations.get(piece.ordinal()).iterator();
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
		final List<LocationTracker> locTrackerCopy = Iterate.over(pieceLocations).map(LocationTracker::copy).toList();
		return new DetailedPieceLocations(locTrackerCopy, midgameTables, endgameTables, hashFeatureProvider);
	}


	// Eclipse generated
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
		result = prime * result + ((pieceLocations == null) ? 0 : pieceLocations.hashCode());
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
		if (pieceLocations == null) {
			if (other.pieceLocations != null)
				return false;
		} else if (!pieceLocations.equals(other.pieceLocations))
			return false;
		if (squarePieceFeatureHash != other.squarePieceFeatureHash)
			return false;
		if (whiteLocations != other.whiteLocations)
			return false;
		return true;
	}
}
