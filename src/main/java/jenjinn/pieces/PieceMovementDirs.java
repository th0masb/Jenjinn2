package jenjinn.pieces;

import jenjinn.base.Dir;
import jflow.seq.Seq;

/**
 * Convenience class containing the directions each piece type can move /
 * attack.
 * 
 * @author TB
 */
public class PieceMovementDirs
{
	/** White pawn movement directions */
	public static final Seq<Dir> WHITE_PAWN_MOVE = Seq.of( Dir.N );

	/** Black pawn movement directions */
	public static final Seq<Dir> BLACK_PAWN_MOVE = Seq.of( Dir.S );

	/** White Pawn attack directions */
	public static final Seq<Dir> WHITE_PAWN_ATTACK = Seq.of( Dir.NE, Dir.NW );

	/** Black Pawn attack directions */
	public static final Seq<Dir> BLACK_PAWN_ATTACK = Seq.of( Dir.SE, Dir.SW );

	/** Bishop directions */
	public static final Seq<Dir> BISHOP = Seq.of( Dir.NE, Dir.NW, Dir.SE, Dir.SW );

	/** Rook directions */
	public static final Seq<Dir> ROOK = Seq.of( Dir.N, Dir.W, Dir.S, Dir.E );

	/** Knight directions */
	public static final Seq<Dir> KNIGHT = Seq.of( Dir.NNE, Dir.NNW, Dir.NWW, Dir.NEE, Dir.SEE, Dir.SSE, Dir.SSW, Dir.SWW );

	/** Queen directions */
	public static final Seq<Dir> QUEEN = Seq.of( Dir.NE, Dir.NW, Dir.N, Dir.E, Dir.SE, Dir.S, Dir.SW, Dir.W );

	/** King directions */
	public static final Seq<Dir> KING = QUEEN;
}
