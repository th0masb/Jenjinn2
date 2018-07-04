/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.base.Side;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author t
 *
 */
public enum PromotionResult
{
	N {
		@Override
		public ChessPiece toPiece(Side side)
		{
			return side.isWhite()? ChessPiece.WHITE_KNIGHT : ChessPiece.BLACK_KNIGHT;
		}
	},

	B {
		@Override
		public ChessPiece toPiece(Side side)
		{
			return side.isWhite()? ChessPiece.WHITE_BISHOP : ChessPiece.BLACK_BISHOP;
		}
	},

	R {
		@Override
		public ChessPiece toPiece(Side side)
		{
			return side.isWhite()? ChessPiece.WHITE_ROOK : ChessPiece.BLACK_ROOK;
		}
	},

	Q {
		@Override
		public ChessPiece toPiece(Side side)
		{
			return side.isWhite()? ChessPiece.WHITE_QUEEN : ChessPiece.BLACK_QUEEN;
		}
	};

	public abstract ChessPiece toPiece(Side side);
}
