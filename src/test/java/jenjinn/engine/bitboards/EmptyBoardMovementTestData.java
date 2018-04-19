package jenjinn.engine.bitboards;

import static jenjinn.engine.bitboards.Bitboards.emptyBoardMovesetBitboard;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;


/**
 * @author ThomasB
 */
public class EmptyBoardMovementTestData extends AbstractPieceMovementTestData 
{
	public EmptyBoardMovementTestData(final ChessPiece piece, final BoardSquare location, final List<BoardSquare> expectedMoveLocations) 
	{
		super(piece, location, expectedMoveLocations);
	}

	@Override
	public long getActualMoveBitboard() 
	{
		return emptyBoardMovesetBitboard(getPiece(), getLocation());
	}
}
