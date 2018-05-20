/**
 *
 */
package jenjinn.engine.boardformatting;

import java.util.Map;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;

/**
 * @author t
 *
 */
public final class FormatBoard {

	private FormatBoard() {
		//		+--+
		//	    |BN|
		//	    +--+
		//	    |WQ|
		//		+--+
	}

	static char[] getGrid()
	{
		final char[] grid = new char[32*17];
		return grid;
	}

	public static String fromMapping(Map<BoardSquare, ChessPiece> pieceLocations)
	{


		throw new RuntimeException();
	}
}
