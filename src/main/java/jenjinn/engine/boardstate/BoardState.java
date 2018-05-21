/**
 *
 */
package jenjinn.engine.boardstate;

import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 *
 */
public interface BoardState
{
	Side getActiveSide();

	long getPieceLocations(ChessPiece piece);
}
