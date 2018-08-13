package jenjinn.engine.pieces;

import static java.util.Arrays.asList;

import java.util.List;

import jenjinn.engine.base.Direction;

/**
 * Convenience class containing the directions each piece type can move /
 * attack.
 * 
 * @author TB
 */
public class PieceMovementDirections
{
	/** White pawn movement directions */
	public static final List<Direction> WHITE_PAWN_MOVE = asList( Direction.N );

	/** Black pawn movement directions */
	public static final List<Direction> BLACK_PAWN_MOVE = asList( Direction.S );

	/** White Pawn attack directions */
	public static final List<Direction> WHITE_PAWN_ATTACK = asList( Direction.NE, Direction.NW );

	/** Black Pawn attack directions */
	public static final List<Direction> BLACK_PAWN_ATTACK = asList( Direction.SE, Direction.SW );

	/** Bishop directions */
	public static final List<Direction> BISHOP = asList( Direction.NE, Direction.NW, Direction.SE, Direction.SW );

	/** Rook directions */
	public static final List<Direction> ROOK = asList( Direction.N, Direction.W, Direction.S, Direction.E );

	/** Knight directions */
	public static final List<Direction> KNIGHT = asList( Direction.NNE, Direction.NNW, Direction.NWW, Direction.NEE, Direction.SEE, Direction.SSE, Direction.SSW, Direction.SWW );

	/** Queen directions */
	public static final List<Direction> QUEEN = asList( Direction.NE, Direction.NW, Direction.N, Direction.E, Direction.SE, Direction.S, Direction.SW, Direction.W );

	/** King directions */
	public static final List<Direction> KING = QUEEN;
}
