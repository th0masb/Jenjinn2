/**
 *
 */
package com.github.maumay.jenjinn.enums.chesspiece;

import static com.github.maumay.jenjinn.base.Dir.E;
import static com.github.maumay.jenjinn.base.Dir.N;
import static com.github.maumay.jenjinn.base.Dir.NE;
import static com.github.maumay.jenjinn.base.Dir.NEE;
import static com.github.maumay.jenjinn.base.Dir.NNE;
import static com.github.maumay.jenjinn.base.Dir.NNW;
import static com.github.maumay.jenjinn.base.Dir.NW;
import static com.github.maumay.jenjinn.base.Dir.NWW;
import static com.github.maumay.jenjinn.base.Dir.S;
import static com.github.maumay.jenjinn.base.Dir.SE;
import static com.github.maumay.jenjinn.base.Dir.SEE;
import static com.github.maumay.jenjinn.base.Dir.SSE;
import static com.github.maumay.jenjinn.base.Dir.SSW;
import static com.github.maumay.jenjinn.base.Dir.SW;
import static com.github.maumay.jenjinn.base.Dir.SWW;
import static com.github.maumay.jenjinn.base.Dir.W;
import static com.github.maumay.jenjinn.bitboards.Bitboard.fold;
import static com.github.maumay.jenjinn.bitboards.Bitboard.intersects;
import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.github.maumay.jenjinn.base.Dir;
import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.pieces.Moveable;
import com.github.maumay.jflow.iterators.EnhancedIterator;
import com.github.maumay.jflow.iterators.factories.Iter;
import com.github.maumay.jflow.utils.Tup;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 */
public enum TestChessPiece implements Moveable
{
	WHITE_PAWN {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			long allPieces = whitePieces | blackPieces;
			Predicate<Square> isStartSquare = sq -> 7 < sq.index && sq.index < 16;
			Predicate<Square> isClearSquare = sq -> !intersects(allPieces, sq.bitboard);
			Function<Square, Optional<Square>> nextSquare = sq -> sq.next(N);

			Optional<Square> firstpush = nextSquare.apply(currentLocation)
					.filter(isClearSquare);

			Optional<Square> secondpush = Optional.of(currentLocation)
					.filter(isStartSquare).flatMap(nextSquare).filter(isClearSquare)
					.flatMap(nextSquare).filter(isClearSquare);

			long moves = Iter.over(firstpush, secondpush).filter(Optional::isPresent)
					.mapToLong(x -> x.get().bitboard).fold(0L, (a, b) -> a | b);

			return moves | getAttacks(currentLocation, whitePieces, blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(NE, NW);
			return fold(currentLocation.getAllSquares(directions, 1));
		}
	},

	WHITE_KNIGHT {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(NNE, NEE, SEE, SSE, SSW, SWW, NWW, NNW);
			return fold(currentLocation.getAllSquares(directions, 1));
		}
	},

	WHITE_BISHOP {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(NE, SE, SW, NW);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces,
					currentLocation, directions);
		}
	},

	WHITE_ROOK {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(Dir.N, Dir.S, Dir.W, Dir.E);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces,
					currentLocation, directions);
		}
	},

	WHITE_QUEEN {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			return WHITE_BISHOP.getSquaresOfControl(currentLocation, whitePieces,
					blackPieces)
					| WHITE_ROOK.getSquaresOfControl(currentLocation, whitePieces,
							blackPieces);
		}
	},

	WHITE_KING {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~whitePieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& blackPieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(N, NE, E, SE, S, SW, W, NW);
			return fold(currentLocation.getAllSquares(directions, 1));
		}
	},

	BLACK_PAWN {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			long allPieces = whitePieces | blackPieces;
			Predicate<Square> isStartSquare = sq -> 47 < sq.index && sq.index < 56;
			Predicate<Square> isClearSquare = sq -> !intersects(allPieces, sq.bitboard);
			Function<Square, Optional<Square>> nextSquare = sq -> sq.next(S);

			Optional<Square> firstpush = nextSquare.apply(currentLocation)
					.filter(isClearSquare);

			Optional<Square> secondpush = Optional.of(currentLocation)
					.filter(isStartSquare).flatMap(nextSquare).filter(isClearSquare)
					.flatMap(nextSquare).filter(isClearSquare);

			long moves = Iter.over(firstpush, secondpush).filter(Optional::isPresent)
					.mapToLong(x -> x.get().bitboard).fold(0L, (a, b) -> a | b);

			return moves | getAttacks(currentLocation, whitePieces, blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(SE, SW);
			return fold(currentLocation.getAllSquares(directions, 1));
		}
	},

	BLACK_KNIGHT {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(NNE, NEE, SEE, SSE, SSW, SWW, NWW, NNW);
			return fold(currentLocation.getAllSquares(directions, 1));
		}
	},

	BLACK_BISHOP {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(NE, SE, SW, NW);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces,
					currentLocation, directions);
		}
	},

	BLACK_ROOK {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(N, E, S, W);
			return getSlidingPieceSquaresOfControl(whitePieces | blackPieces,
					currentLocation, directions);
		}
	},

	BLACK_QUEEN {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			return BLACK_BISHOP.getSquaresOfControl(currentLocation, whitePieces,
					blackPieces)
					| BLACK_ROOK.getSquaresOfControl(currentLocation, whitePieces,
							blackPieces);
		}
	},

	BLACK_KING {
		@Override
		public long getMoves(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& (~blackPieces);
		}

		@Override
		public long getAttacks(Square currentLocation, long whitePieces, long blackPieces)
		{
			return getSquaresOfControl(currentLocation, whitePieces, blackPieces)
					& whitePieces;
		}

		@Override
		public long getSquaresOfControl(Square currentLocation, long whitePieces,
				long blackPieces)
		{
			List<Dir> directions = asList(N, NE, E, SE, S, SW, W, NW);
			return fold(currentLocation.getAllSquares(directions, 1));
		}
	};

	private static long getSlidingPieceSquaresOfControl(long allPieces,
			Square startSquare, List<Dir> movementDirections)
	{
		Predicate<Square> isClearSquare = sq -> !intersects(allPieces, sq.bitboard);

		return Iter.over(movementDirections).flatMap(dir -> {
			Vec<Square> allSquares = startSquare.getAllSquares(dir, 8);
			Tup<Vec<Square>, Vec<Square>> spanned = allSquares.span(isClearSquare);
			return spanned._1.iter().append(Iter.option(spanned._2.headOption()));
		}).map(sq -> sq.bitboard).fold((a, b) -> a | b);
	}

	public static List<TestChessPiece> valuesAsList()
	{
		return Arrays.asList(values());
	}

	public static EnhancedIterator<TestChessPiece> iterateAll()
	{
		return Iter.over(valuesAsList());
	}
}
