/**
 *
 */
package jenjinn.enums.chesspiece;

import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.Moveable;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author ThomasB
 */
public enum TestChessPiece implements Moveable
{
	WHITE_PAWN
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			throw new RuntimeException();
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(Direction.NE, Direction.NW);
			return bitwiseOr(currentLocation.getAllSquaresInDirections(directions, 1));
		}
	},

	WHITE_KNIGHT
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(
					Direction.NNE,
					Direction.NEE,
					Direction.SEE,
					Direction.SSE,
					Direction.SSW,
					Direction.SWW,
					Direction.NWW,
					Direction.NNW);

			return bitwiseOr(currentLocation.getAllSquaresInDirections(directions, 1));
		}
	},

	WHITE_BISHOP
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(Direction.NE, Direction.SE, Direction.SW, Direction.NW);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces, currentLocation, directions);
		}
	},

	WHITE_ROOK
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(Direction.N, Direction.S, Direction.W, Direction.E);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces, currentLocation, directions);
		}
	},

	WHITE_QUEEN
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return WHITE_BISHOP.getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					| WHITE_ROOK.getSquaresOfControl(currentLocation, whitePieces, blackPieces);
		}
	},

	WHITE_KING
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~whitePieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(BoardSquare currentLocation, long whitePieces, long blackPieces)
		{
			final List<Direction> directions = asList(Direction.N, Direction.E, Direction.S, Direction.W);
			return bitwiseOr(currentLocation.getAllSquaresInDirections(directions, 1));
		}
	},

	BLACK_PAWN
	{
		@Override
		public long getMoves(BoardSquare currentLocation, long whitePieces, long blackPieces)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getAttacks(BoardSquare currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(BoardSquare currentLocation, long whitePieces, long blackPieces)
		{
			final List<Direction> directions = asList(Direction.SE, Direction.SW);
			return bitwiseOr(currentLocation.getAllSquaresInDirections(directions, 1));
		}
	},

	BLACK_KNIGHT
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(
					Direction.NNE,
					Direction.NEE,
					Direction.SEE,
					Direction.SSE,
					Direction.SSW,
					Direction.SWW,
					Direction.NWW,
					Direction.NNW);

			return bitwiseOr(currentLocation.getAllSquaresInDirections(directions, 1));
		}
	},

	BLACK_BISHOP
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(Direction.NE, Direction.SE, Direction.SW, Direction.NW);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces, currentLocation, directions);
		}
	},

	BLACK_ROOK
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(Direction.N, Direction.E, Direction.S, Direction.W);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces, currentLocation, directions);
		}
	},

	BLACK_QUEEN
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return BLACK_BISHOP.getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					| BLACK_ROOK.getSquaresOfControl(currentLocation, whitePieces, blackPieces);
		}
	},

	BLACK_KING
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & (~blackPieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(BoardSquare currentLocation, long whitePieces, long blackPieces)
		{
			final List<Direction> directions = asList(Direction.N, Direction.E, Direction.S, Direction.W);
			return bitwiseOr(currentLocation.getAllSquaresInDirections(directions, 1));
		}
	};

	private static long getSlidingPieceSquaresOfControl(final long allPieces, final BoardSquare startSquare, final List<Direction> movementDirections)
	{
		final List<BoardSquare> controlSquares = new ArrayList<>(64);
		Iterate.over(movementDirections).forEach(direction ->
		{
			BoardSquare current = startSquare.getNextSquareInDirection(direction);

			while (current != null && !bitboardsIntersect(current.asBitboard(), allPieces)) {
				controlSquares.add(current);
				current = current.getNextSquareInDirection(direction);
			}
			if (current != null) {
				controlSquares.add(current);
			}
		});
		return bitwiseOr(controlSquares);
	}
}
