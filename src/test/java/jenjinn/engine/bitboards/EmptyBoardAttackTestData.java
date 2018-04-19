package jenjinn.engine.bitboards;

import static jenjinn.engine.bitboards.Bitboards.emptyBoardAttacksetBitboard;

import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;

public class EmptyBoardAttackTestData extends AbstractPieceMovementTestData 
{
	public EmptyBoardAttackTestData(final ChessPiece piece, final BoardSquare location, final List<BoardSquare> expectedMoveLocations) 
	{
		super(piece, location, expectedMoveLocations);
	}

	@Override
	public long getActualMoveBitboard() 
	{
		return emptyBoardAttacksetBitboard(getPiece(), getLocation());
	}
}
