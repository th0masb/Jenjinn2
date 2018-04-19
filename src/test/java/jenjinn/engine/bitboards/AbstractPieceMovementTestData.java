/**
 * 
 */
package jenjinn.engine.bitboards;

import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;

/**
 * @author ThomasB
 *
 */
public abstract class AbstractPieceMovementTestData 
{
	private ChessPiece piece;
	private BoardSquare location;
	
	private List<BoardSquare> expectedMoveLocations;

	public AbstractPieceMovementTestData(final ChessPiece piece, final BoardSquare location, final List<BoardSquare> expectedMoveLocations) {
		this.piece = piece;
		this.location = location;
		this.expectedMoveLocations = expectedMoveLocations;
	}
	
	public abstract long getActualMoveBitboard();
	
	public long getExpectedMoveBitboard()
	{
		return bitwiseOr(expectedMoveLocations);
	}

	public ChessPiece getPiece() 
	{
		return piece;
	}

	public BoardSquare getLocation() 
	{
		return location;
	}

	public List<BoardSquare> getExpectedMoveLocations() 
	{
		return expectedMoveLocations;
	}
	
	@Override
	public String toString()
	{
		return piece.name() + " at " + location.name();
	}
}
