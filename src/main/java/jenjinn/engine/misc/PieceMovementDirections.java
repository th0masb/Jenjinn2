package jenjinn.engine.misc;

import static java.util.Arrays.asList;

import java.util.List;

import jenjinn.engine.enums.Direction;

/**
 * Convenience class containing the directions each piece type can move /
 * attack.
 * 
 * @author TB
 * @date 22 Jan 2017
 */
public class PieceMovementDirections
{
	/** White pawn movement directions */
	public static final List<Direction> WPM = asList( Direction.N );

	/** Black pawn movement directions */
	public static final List<Direction> BPM = asList( Direction.S );

	/** White Pawn attack directions */
	public static final List<Direction> WPA = asList( Direction.NE, Direction.NW );

	/** Black Pawn attack directions */
	public static final List<Direction> BPA = asList( Direction.SE, Direction.SW );

	/** Bishop directions */
	public static final List<Direction> BD = asList( Direction.NE, Direction.NW, Direction.SE, Direction.SW );

	/** Rook directions */
	public static final List<Direction> RD = asList( Direction.N, Direction.W, Direction.S, Direction.E );

	/** Knight directions */
	public static final List<Direction> ND = asList( Direction.NNE, Direction.NNW, Direction.NWW, Direction.NEE, Direction.SEE, Direction.SSE, Direction.SSW, Direction.SWW );

	/** Queen directions */
	public static final List<Direction> QD = asList( Direction.NE, Direction.NW, Direction.N, Direction.E, Direction.SE, Direction.S, Direction.SW, Direction.W );

	/** King directions */
	public static final List<Direction> KD = QD;
}
