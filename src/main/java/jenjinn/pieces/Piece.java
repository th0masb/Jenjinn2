/**
 *
 */
package jenjinn.pieces;


import jenjinn.base.Side;
import jenjinn.base.Square;
import jenjinn.bitboards.Bitboards;


/**
 * @author ThomasB
 */
public enum Piece implements Moveable
{
	// DON'T CHANGE ORDER
	WHITE_PAWN
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			int locationIndex = currentLocation.ordinal();
			assert 7 < locationIndex && locationIndex < 56;

			long emptySquares = ~(whitePieces | blackPieces);
			long push = (1L << (locationIndex + 8)) & emptySquares;

			if (locationIndex < 16 && push != 0) {
				push |= (1L << (locationIndex + 16)) & emptySquares;
			}

			return push | getAttacks(currentLocation, whitePieces, blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			return Bitboards.emptyBoardAttackset(WHITE_PAWN, currentLocation);
		}
	},

	WHITE_KNIGHT
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			return Bitboards.emptyBoardMoveset(WHITE_KNIGHT, currentLocation);
		}
	},

	WHITE_BISHOP
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			int magicIndex = getMagicMoveIndex(
					whitePieces | blackPieces,
					Bitboards.bishopOccupancyMaskAt(currentLocation),
					Bitboards.bishopMagicNumberAt(currentLocation),
					Bitboards.bishopMagicBitshiftAt(currentLocation)
					);

			return Bitboards.bishopMagicMove(currentLocation, magicIndex);
		}
	},

	WHITE_ROOK
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			int magicIndex = getMagicMoveIndex(
					whitePieces | blackPieces,
					Bitboards.rookOccupancyMaskAt(currentLocation),
					Bitboards.rookMagicNumberAt(currentLocation),
					Bitboards.rookMagicBitshiftAt(currentLocation)
					);

			return Bitboards.rookMagicMove(currentLocation, magicIndex);
		}
	},

	WHITE_QUEEN
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			return WHITE_BISHOP.getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					| WHITE_ROOK.getSquaresOfControl(currentLocation, whitePieces, blackPieces);
		}
	},

	WHITE_KING
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			return Bitboards.emptyBoardMoveset(WHITE_KING, currentLocation);
		}
	},



	BLACK_PAWN
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			int locationIndex = currentLocation.ordinal();
			assert 7 < locationIndex && locationIndex < 56;

			long emptySquares = ~(whitePieces | blackPieces);
			long push = (1L << (locationIndex - 8)) & emptySquares;

			if (locationIndex > 47 && push != 0) {
				push |= (1L << (locationIndex - 16)) & emptySquares;
			}

			return push | getAttacks(currentLocation, whitePieces, blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			return Bitboards.emptyBoardAttackset(BLACK_PAWN, currentLocation);
		}
	},

	BLACK_KNIGHT
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			return Bitboards.emptyBoardMoveset(BLACK_KNIGHT, currentLocation);
		}
	},

	BLACK_BISHOP
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			int magicIndex = getMagicMoveIndex(
					whitePieces | blackPieces,
					Bitboards.bishopOccupancyMaskAt(currentLocation),
					Bitboards.bishopMagicNumberAt(currentLocation),
					Bitboards.bishopMagicBitshiftAt(currentLocation)
					);

			return Bitboards.bishopMagicMove(currentLocation, magicIndex);
		}
	},

	BLACK_ROOK
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			int magicIndex = getMagicMoveIndex(
					whitePieces | blackPieces,
					Bitboards.rookOccupancyMaskAt(currentLocation),
					Bitboards.rookMagicNumberAt(currentLocation),
					Bitboards.rookMagicBitshiftAt(currentLocation)
					);

			return Bitboards.rookMagicMove(currentLocation, magicIndex);
		}
	},

	BLACK_QUEEN
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			return BLACK_BISHOP.getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					| BLACK_ROOK.getSquaresOfControl(currentLocation, whitePieces, blackPieces);
		}
	},

	BLACK_KING
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			return Bitboards.emptyBoardMoveset(BLACK_KING, currentLocation);
		}
	};

	public boolean isPawn()
	{
		return ordinal() % 6 == 0;
	}

	public boolean isKing()
	{
		return ordinal() % 6 == 5;
	}
	
	public boolean isKnight()
	{
		return ordinal() % 6 == 1;
	}

	public Side getSide()
	{
		return isWhite()? Side.WHITE : Side.BLACK;
	}

	public boolean isWhite()
	{
		return ordinal() < 6;
	}

	public boolean isSlidingPiece()
	{
		int index = ordinal() % 6;
		return index == 2 || index == 3 || index == 4;
	}

	private static int getMagicMoveIndex(long allPieces, long occupancyMask, long magicNumber, int magicBitshift)
	{
		return (int) (((occupancyMask & allPieces) * magicNumber) >>> magicBitshift);
	}
}
