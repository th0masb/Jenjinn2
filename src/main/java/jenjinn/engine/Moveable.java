/**
 *
 */
package jenjinn.engine;

import jenjinn.engine.enums.BoardSquare;

/**
 * @author ThomasB
 *
 */
public interface Moveable
{
	long getMoves(BoardSquare currentLocation, long whitePieces, long blackPieces);

	long getAttacks(BoardSquare currentLocation, long whitePieces, long blackPieces);

	long getSquaresOfControl(BoardSquare currentLocation, long whitePieces, long blackPieces);
}
