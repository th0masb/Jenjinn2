/**
 *
 */
package jenjinn.moves;

import jenjinn.base.Side;
import jenjinn.pieces.Piece;

/**
 * @author t
 *
 */
public enum PromotionResult
{
	N {
		@Override
		public Piece toPiece(Side side)
		{
			return side.isWhite()? Piece.WHITE_KNIGHT : Piece.BLACK_KNIGHT;
		}
	},

	B {
		@Override
		public Piece toPiece(Side side)
		{
			return side.isWhite()? Piece.WHITE_BISHOP : Piece.BLACK_BISHOP;
		}
	},

	R {
		@Override
		public Piece toPiece(Side side)
		{
			return side.isWhite()? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
		}
	},

	Q {
		@Override
		public Piece toPiece(Side side)
		{
			return side.isWhite()? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
		}
	};

	public abstract Piece toPiece(Side side);
}
