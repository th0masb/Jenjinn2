/**
 *
 */
package jenjinn.pieces;

import jenjinn.base.Square;
import jenjinn.utils.BasicPieceLocations;

/**
 * @author ThomasB
 *
 */
public interface Moveable
{
	long getMoves(Square currentLocation, long whitePieces, long blackPieces);

	long getAttacks(Square currentLocation, long whitePieces, long blackPieces);

	long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces);

	default long getMoves(final Square currentLocation, final BasicPieceLocations pieceLocations)
	{
		return getMoves(currentLocation, pieceLocations.getWhite(), pieceLocations.getBlack());
	}

	default long getAttacks(final Square currentLocation, final BasicPieceLocations pieceLocations)
	{
		return getAttacks(currentLocation, pieceLocations.getWhite(), pieceLocations.getBlack());
	}

	default long getSquaresOfControl(final Square currentLocation, final BasicPieceLocations pieceLocations)
	{
		return getSquaresOfControl(currentLocation, pieceLocations.getWhite(), pieceLocations.getBlack());
	}
}
