/**
 *
 */
package jenjinn.engine.pieces;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.utils.BasicPieceLocations;

/**
 * @author ThomasB
 *
 */
public interface Moveable
{
	long getMoves(BoardSquare currentLocation, long whitePieces, long blackPieces);

	long getAttacks(BoardSquare currentLocation, long whitePieces, long blackPieces);

	long getSquaresOfControl(BoardSquare currentLocation, long whitePieces, long blackPieces);

	default long getMoves(final BoardSquare currentLocation, final BasicPieceLocations pieceLocations)
	{
		return getMoves(currentLocation, pieceLocations.getWhite(), pieceLocations.getBlack());
	}

	default long getAttacks(final BoardSquare currentLocation, final BasicPieceLocations pieceLocations)
	{
		return getAttacks(currentLocation, pieceLocations.getWhite(), pieceLocations.getBlack());
	}

	default long getSquaresOfControl(final BoardSquare currentLocation, final BasicPieceLocations pieceLocations)
	{
		return getSquaresOfControl(currentLocation, pieceLocations.getWhite(), pieceLocations.getBlack());
	}
}
