package jenjinn.engine.bitboards;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.pieces.ChessPiece;

/**
 * Exposes API methods for accessing cached bitboards.
 *
 * @author TB
 * @date 21 Jan 2017
 */
public final class Bitboards
{
	public static long universal()
	{
		return BitboardsImpl.UNIVERSAL_BITBOARD;
	}

	// Section 1 - Basic areas of the board.
	/**
	 * Access array of 64 bitboards representing single squares on a chessboard. They are
	 * ordered from h1 to a8, the reason for this seemingly strange ordering is due
	 * to the fact this is the natural representation taking into account these
	 * bitboards are just numbers growing in size. The bitboard representing h1 is
	 * the smallest, a8 the largest.
	 */
	public static long singleOccupancyBitboard(int squareIndex)
	{
		return BitboardsImpl.SINGLE_OCCUPANCY_BITBOARDS[squareIndex];
	}

	/**
	 * Access array of 8 bitboards representing the ranks on a chessboard. Ordered rank 1
	 * up to rank 8.
	 */
	public static long rankBitboard(int rankIndex)
	{
		return BitboardsImpl.RANK_BITBOARDS[rankIndex];
	}

	/**
	 * Access array of 8 bitboards representing the files on a chessboard. Ordered from h
	 * to a.
	 */
	public static long fileBitboard(int fileIndex)
	{
		return BitboardsImpl.FILE_BITBOARDS[fileIndex];
	}

	/**
	 * Array of 15 bitboards representing the diagonals of gradient 1 on a
	 * chessboard. Ordered from right to left.
	 */
	public static long diagonalBitboard(int diagonalIndex)
	{
		return BitboardsImpl.DIAGONAL_BITBOARDS[diagonalIndex];
	}

	/**
	 * Array of 15 bitboards representing the diagonals of gradient -1 on a
	 * chessboard. Ordered from left to right.
	 */
	public static long antiDiagonalBitboard(int antiDiagonalIndex)
	{
		return BitboardsImpl.ANTI_DIAGONAL_BITBOARDS[antiDiagonalIndex];
	}

	/**
	 * Function which will return a bitboard representing the available move locations for
	 * a given piece at a given square.
	 */
	public static long emptyBoardMoveset(ChessPiece piece, BoardSquare square)
	{
		int pieceOrdinalModSix = piece.ordinal() % 6;

		if (pieceOrdinalModSix == 0) {
			return BitboardsImpl.EMPTY_BOARD_MOVESETS[piece.isWhite()? 0 : 1][square.ordinal()];
		}
		else {
			return BitboardsImpl.EMPTY_BOARD_MOVESETS[(piece.ordinal() % 6) + 1][square.ordinal()];
		}
	}

	/**
	 * Function which will return a bitboard representing the available attack locations for
	 * a given piece at a given square.
	 */
	public static long emptyBoardAttackset(ChessPiece piece, BoardSquare square)
	{
		int pieceOrdinalModSix = piece.ordinal() % 6;

		if (pieceOrdinalModSix == 0) {
			return BitboardsImpl.EMPTY_BOARD_ATTACKSETS[piece.isWhite()? 0 : 1][square.ordinal()];
		}
		else {
			return BitboardsImpl.EMPTY_BOARD_ATTACKSETS[pieceOrdinalModSix + 1][square.ordinal()];
		}
	}

	// -----------------------------------
	// Section 2 - Constants required for Magic bitboard implementation for
	// Rooks, Bishops amd Pawn first moves

	/**
	 * Bishop occupancy masks for each square. An bishop occupancy mask for square i
	 * is the corresponding empty board bishop moveset & ~(border set).
	 */
	public static long bishopOccupancyMaskAt(BoardSquare square)
	{
		return BitboardsImpl.BISHOP_OCCUPANCY_MASKS[square.ordinal()];
	}

	/**
	 * Rook occupancy masks for each square. Slightly more complicated to construct
	 * than bishop ones if the rook lies in the border set. Basically you just
	 * remove the end squares of the rook move path on an empty board and keep the
	 * rest (except the square containing the rook).
	 */
	public static long rookOccupancyMaskAt(BoardSquare square)
	{
		return BitboardsImpl.ROOK_OCCUPANCY_MASKS[square.ordinal()];
	}

	/**
	 * Container of all the possible bishop occupancy variations for each different
	 * square. A bov for square i is BOM[i] & (location of all pieces on the board).
	 * There are 2^(Cardinality(BOM[i])) variations.
	 */
	public static long[] bishopOccupancyVariationAt(BoardSquare square)
	{
		return BitboardsImpl.BISHOP_OCCUPANCY_VARIATIONS[square.ordinal()];
	}

	/**
	 * Container of all the possible rook occupancy variations for each different
	 * square. A rov for square i is ROM[i] & (location of all pieces on the board).
	 * There are 2^(Cardinality(ROM[i])) variations.
	 */
	public static long[] rookOccupancyVariationAt(BoardSquare square)
	{
		return BitboardsImpl.ROOK_OCCUPANCY_VARIATIONS[square.ordinal()];
	}

	/**
	 * Bishop magic bitshift values for each square. BMB[i] = Cardinality(BOM[i]).
	 * The magic bitshifts form part of the surjective mapping definition behind
	 * magic bitboards
	 */
	public static int bishopMagicBitshiftAt(BoardSquare square)
	{
		return BitboardsImpl.BISHOP_MAGIC_BITSHIFTS[square.ordinal()];
	}

	/**
	 * Rook magic bitshift values for each square. RMB[i] = Cardinality(ROM[i]). The
	 * magic bitshifts form part of the surjective mapping definition behind magic
	 * bitboards
	 */
	public static int rookMagicBitshiftAt(BoardSquare square)
	{
		return BitboardsImpl.ROOK_MAGIC_BITSHIFTS[square.ordinal()];
	}

	/**
	 * Bishop magic number values for each square. Used for defining the surjective
	 * map definition used in magic bitboards.
	 */
	public static long bishopMagicNumberAt(BoardSquare square)
	{
		return BitboardsImpl.BISHOP_MAGIC_NUMBERS[square.ordinal()];
	}

	/**
	 * Rook magic number values for each square. Used for defining the surjective
	 * map definition used in magic bitboards.
	 */
	public static long rookMagicNumberAt(BoardSquare square)
	{
		return BitboardsImpl.ROOK_MAGIC_NUMBERS[square.ordinal()];
	}

	// --------------------------------------------
	// Section 3 - the move databases

	/**
	 * Bishop move database implementing the magic bitboard mapping technique. The
	 * domain of the map is the set of all bishop occupancy variations and the
	 * target of the map is this database.
	 */
	public static long bishopMagicMove(BoardSquare square, int magicIndex)
	{
		return BitboardsImpl.BISHOP_MAGIC_MOVES[square.ordinal()][magicIndex];
	}

	/**
	 * Rook move database implementing the magic bitboard mapping technique. The
	 * domain of the map is the set of all rook occupancy variations and the target
	 * of the map is this database.
	 */
	public static long rookMagicMove(BoardSquare square, int magicIndex)
	{
		return BitboardsImpl.ROOK_MAGIC_MOVES[square.ordinal()][magicIndex];
	}
}
