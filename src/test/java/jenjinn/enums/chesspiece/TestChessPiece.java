/**
 *
 */
package jenjinn.enums.chesspiece;

import static java.util.Arrays.asList;
import static jenjinn.base.Dir.E;
import static jenjinn.base.Dir.N;
import static jenjinn.base.Dir.NE;
import static jenjinn.base.Dir.NEE;
import static jenjinn.base.Dir.NNE;
import static jenjinn.base.Dir.NNW;
import static jenjinn.base.Dir.NW;
import static jenjinn.base.Dir.NWW;
import static jenjinn.base.Dir.S;
import static jenjinn.base.Dir.SE;
import static jenjinn.base.Dir.SEE;
import static jenjinn.base.Dir.SSE;
import static jenjinn.base.Dir.SSW;
import static jenjinn.base.Dir.SW;
import static jenjinn.base.Dir.SWW;
import static jenjinn.base.Dir.W;
import static jenjinn.bitboards.BitboardUtils.bitboardsIntersect;
import static jenjinn.bitboards.BitboardUtils.bitwiseOr;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import jenjinn.base.Dir;
import jenjinn.base.Square;
import jenjinn.pieces.Moveable;
import jflow.iterators.Flow;
import jflow.iterators.factories.Iter;
import jflow.iterators.misc.Pair;
import jflow.seq.Seq;

/**
 * @author ThomasB
 */
public enum TestChessPiece implements Moveable
{
	WHITE_PAWN
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			long allPieces = whitePieces | blackPieces;
			Predicate<Square> isStartSquare = sq -> 7 < sq.index && sq.index < 16;
			Predicate<Square> isClearSquare = sq -> !bitboardsIntersect(allPieces, sq.bitboard);
			Function<Square, Optional<Square>> nextSquare = sq -> sq.getNextSquare(N);
			
			Optional<Square> firstpush = nextSquare.apply(currentLocation).filter(isClearSquare);
			
			Optional<Square> secondpush = Optional.of(currentLocation)
					.filter(isStartSquare)
					.flatMap(nextSquare)
					.filter(isClearSquare)
					.flatMap(nextSquare)
					.filter(isClearSquare);
			
			long moves = Iter.over(firstpush, secondpush)
					.filter(Optional::isPresent)
					.mapToLong(x -> x.get().bitboard)
					.fold(0L, (a, b) -> a | b);
			
			return moves | getAttacks(currentLocation, whitePieces, blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			List<Dir> directions = asList(NE, NW);
			return bitwiseOr(currentLocation.getAllSquares(directions, 1));
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
			List<Dir> directions = asList(NNE, NEE, SEE, SSE, SSW, SWW, NWW, NNW);
			return bitwiseOr(currentLocation.getAllSquares(directions, 1));
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
			List<Dir> directions = asList(NE, SE, SW, NW);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces, currentLocation, directions);
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
			List<Dir> directions = asList(Dir.N, Dir.S, Dir.W, Dir.E);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces, currentLocation, directions);
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
			List<Dir> directions = asList(N, NE, E, SE, S, SW, W, NW);
			return bitwiseOr(currentLocation.getAllSquares(directions, 1));
		}
	},

	BLACK_PAWN
	{
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			long allPieces = whitePieces | blackPieces;
			Predicate<Square> isStartSquare = sq -> 47 < sq.index && sq.index < 56;
			Predicate<Square> isClearSquare = sq -> !bitboardsIntersect(allPieces, sq.bitboard);
			Function<Square, Optional<Square>> nextSquare = sq -> sq.getNextSquare(S);
			
			Optional<Square> firstpush = nextSquare.apply(currentLocation).filter(isClearSquare);
			
			Optional<Square> secondpush = Optional.of(currentLocation)
					.filter(isStartSquare)
					.flatMap(nextSquare)
					.filter(isClearSquare)
					.flatMap(nextSquare)
					.filter(isClearSquare);
			
			long moves = Iter.over(firstpush, secondpush)
					.filter(Optional::isPresent)
					.mapToLong(x -> x.get().bitboard)
					.fold(0L, (a, b) -> a | b);
			
			return moves | getAttacks(currentLocation, whitePieces, blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces) & whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces, long blackPieces)
		{
			List<Dir> directions = asList(SE, SW);
			return bitwiseOr(currentLocation.getAllSquares(directions, 1));
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
			List<Dir> directions = asList(NNE, NEE, SEE, SSE, SSW, SWW, NWW, NNW);
			return bitwiseOr(currentLocation.getAllSquares(directions, 1));
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
			List<Dir> directions = asList(NE, SE, SW, NW);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces, currentLocation, directions);
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
			List<Dir> directions = asList(N, E, S, W);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces, currentLocation, directions);
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
			List<Dir> directions = asList(N, NE, E, SE, S, SW, W, NW);
			return bitwiseOr(currentLocation.getAllSquares(directions, 1));
		}
	};

	private static long getSlidingPieceSquaresOfControl(long allPieces, Square startSquare, List<Dir> movementDirections)
	{
		Predicate<Square> isClearSquare = sq -> !bitboardsIntersect(allPieces, sq.bitboard);
		
		return Iter.over(movementDirections).flatMap(dir -> {
			Seq<Square> allSquares = startSquare.getAllSquares(dir, 8);
			Pair<Seq<Square>, Seq<Square>> spanned = allSquares.span(isClearSquare);
			return spanned._1.flow().append(spanned._2.headOption());
		}).map(sq -> sq.bitboard).fold((a, b) -> a | b);
	}

	public static List<TestChessPiece> valuesAsList()
	{
		return Arrays.asList(values());
	}

	public static Flow<TestChessPiece> iterateAll()
	{
		return Iter.over(valuesAsList());
	}
}
