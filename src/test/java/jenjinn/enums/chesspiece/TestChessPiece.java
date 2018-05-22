/**
 *
 */
package jenjinn.enums.chesspiece;

import static java.util.Arrays.asList;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.engine.bitboards.BitboardUtils.bitwiseOr;
import static jenjinn.engine.enums.Direction.E;
import static jenjinn.engine.enums.Direction.N;
import static jenjinn.engine.enums.Direction.NE;
import static jenjinn.engine.enums.Direction.NEE;
import static jenjinn.engine.enums.Direction.NNE;
import static jenjinn.engine.enums.Direction.NNW;
import static jenjinn.engine.enums.Direction.NW;
import static jenjinn.engine.enums.Direction.NWW;
import static jenjinn.engine.enums.Direction.S;
import static jenjinn.engine.enums.Direction.SE;
import static jenjinn.engine.enums.Direction.SEE;
import static jenjinn.engine.enums.Direction.SSE;
import static jenjinn.engine.enums.Direction.SSW;
import static jenjinn.engine.enums.Direction.SW;
import static jenjinn.engine.enums.Direction.SWW;
import static jenjinn.engine.enums.Direction.W;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.Moveable;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import xawd.jflow.iterators.Flow;
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
			final long allPieces = whitePieces | blackPieces;
			final List<BoardSquare> pushSquares = new ArrayList<>();
			final BoardSquare firstPush = currentLocation.getNextSquareInDirection(N);
			if (firstPush != null && !bitboardsIntersect(firstPush.asBitboard(), allPieces))
			{
				pushSquares.add(firstPush);
				final int locIndex = currentLocation.ordinal();
				if (7 < locIndex && locIndex < 16)
				{
					final BoardSquare secondPush = firstPush.getNextSquareInDirection(N);
					if (!bitboardsIntersect(secondPush.asBitboard(), allPieces))
					{
						pushSquares.add(secondPush);
					}
				}
			}
			return bitwiseOr(pushSquares) | getAttacks(currentLocation, whitePieces, blackPieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(NE, NW);
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
			final List<Direction> directions = asList(NNE, NEE, SEE, SSE, SSW, SWW, NWW, NNW);
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
			final List<Direction> directions = asList(NE, SE, SW, NW);
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
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(N, NE, E, SE, S, SW, W, NW);
			return bitwiseOr(currentLocation.getAllSquaresInDirections(directions, 1));
		}
	},

	BLACK_PAWN
	{
		@Override
		public long getMoves(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final long allPieces = whitePieces | blackPieces;
			final List<BoardSquare> pushSquares = new ArrayList<>();
			final BoardSquare firstPush = currentLocation.getNextSquareInDirection(Direction.S);
			if (firstPush != null && !bitboardsIntersect(firstPush.asBitboard(), allPieces))
			{
				pushSquares.add(firstPush);
				final int locIndex = currentLocation.ordinal();
				if (47 < locIndex && locIndex < 56)
				{
					final BoardSquare secondPush = firstPush.getNextSquareInDirection(Direction.S);
					if (!bitboardsIntersect(secondPush.asBitboard(), allPieces))
					{
						pushSquares.add(secondPush);
					}
				}
			}
			return bitwiseOr(pushSquares) | getAttacks(currentLocation, whitePieces, blackPieces);
		}

		@Override
		public long getAttacks(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(SE, SW);
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
			final List<Direction> directions = asList(NNE, NEE, SEE, SSE, SSW, SWW, NWW, NNW);
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
			final List<Direction> directions = asList(NE, SE, SW, NW);
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
			final List<Direction> directions = asList(N, E, S, W);
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
		public long getSquaresOfControl(final BoardSquare currentLocation, final long whitePieces, final long blackPieces)
		{
			final List<Direction> directions = asList(N, NE, E, SE, S, SW, W, NW);
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

	public static List<TestChessPiece> valuesAsList()
	{
		return Arrays.asList(values());
	}

	public static Flow<TestChessPiece> iterateAll()
	{
		return Iterate.over(valuesAsList());
	}
}