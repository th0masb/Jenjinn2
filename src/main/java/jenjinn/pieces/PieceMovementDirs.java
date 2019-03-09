package jenjinn.pieces;

import static com.github.maumay.jflow.vec.Vec.vec;

import com.github.maumay.jflow.vec.Vec;

import jenjinn.base.Dir;

/**
 * Convenience class containing the directions each piece type can move /
 * attack.
 * 
 * @author TB
 */
public class PieceMovementDirs
{
	/** White pawn movement directions */
	public static final Vec<Dir> WHITE_PAWN_MOVE = vec(Dir.N);

	/** Black pawn movement directions */
	public static final Vec<Dir> BLACK_PAWN_MOVE = vec(Dir.S);

	/** White Pawn attack directions */
	public static final Vec<Dir> WHITE_PAWN_ATTACK = vec(Dir.NE, Dir.NW);

	/** Black Pawn attack directions */
	public static final Vec<Dir> BLACK_PAWN_ATTACK = vec(Dir.SE, Dir.SW);

	/** Bishop directions */
	public static final Vec<Dir> BISHOP = vec(Dir.NE, Dir.NW, Dir.SE, Dir.SW);

	/** Rook directions */
	public static final Vec<Dir> ROOK = vec(Dir.N, Dir.W, Dir.S, Dir.E);

	/** Knight directions */
	public static final Vec<Dir> KNIGHT = vec(Dir.NNE, Dir.NNW, Dir.NWW, Dir.NEE, Dir.SEE,
			Dir.SSE, Dir.SSW, Dir.SWW);

	/** Queen directions */
	public static final Vec<Dir> QUEEN = vec(Dir.NE, Dir.NW, Dir.N, Dir.E, Dir.SE, Dir.S,
			Dir.SW, Dir.W);

	/** King directions */
	public static final Vec<Dir> KING = QUEEN;
}
