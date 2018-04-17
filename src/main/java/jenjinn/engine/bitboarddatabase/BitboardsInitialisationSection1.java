package jenjinn.engine.bitboarddatabase;

import static java.util.stream.Collectors.toList;
import static jenjinn.engine.misc.BitboardUtils.bitwiseOr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.misc.PieceMovementDirectionArrays;

/**
 * First of three utility classes containing only static methods to initialise
 * the constants in the BBDB class. We generate the basic building blocks and
 * then the move and attack sets of all piece types on an empty board.
 *
 * @author TB
 * @date 21 Jan 2017
 */
public class BitboardsInitialisationSection1
{

	public static long[] generateSingleOccupancyBitboards()
	{
		final long[] ans = new long[64];
		for (int i = 0; i < 64; i++) {
			ans[i] = (1L << i);
		}
		return ans;
	}

	public static long[] generateRankBitboards()
	{
		final long[] ans = new long[8];
		for (byte i = 0; i < 8; i++) {
			final BoardSquare start = BoardSquare.fromIndex(8 * i);
			final List<BoardSquare> allConstituents = start.getAllSquaresInDirection(Direction.W, true);
			ans[i] = bitwiseOr(allConstituents);
		}
		return ans;
	}

	public static long[] generateFileBitboards()
	{
		final long[] ans = new long[8];
		for (byte i = 0; i < 8; i++) {
			final BoardSquare start = BoardSquare.fromIndex(i);
			final List<BoardSquare> allConstituents = start.getAllSquaresInDirection(Direction.N, true);
			ans[i] = bitwiseOr(allConstituents);
		}
		return ans;
	}

	public static long[] generateDiagonalBitboards()
	{
		final long[] ans = new long[15];
		for (byte i = 0; i < 15; i++) {
			final BoardSquare start = i < 8 ? BoardSquare.fromIndex(i) : BoardSquare.fromRankAndFileIndices(i - 7,  7);
			final List<BoardSquare> allConstituents = start.getAllSquaresInDirection(Direction.NE, true);
			ans[i] = bitwiseOr(allConstituents);
		}
		return ans;
	}

	public static long[] generateAntidiagonalBitboards()
	{
		final long[] ans = new long[15];
		for (byte i = 0; i < 15; i++) {
			final BoardSquare start = (i < 8) ? BoardSquare.fromIndex(7 - i) : BoardSquare.fromRankAndFileIndices(i - 7, 0);
			final List<BoardSquare> allConstituents = start.getAllSquaresInDirection(Direction.NW, true);
			ans[i] = bitwiseOr(allConstituents);
		}
		return ans;
	}

	public static long[][] generateAllEmptyBoardPieceMovementBitboards()
	{
		final long[][] ans = new long[7][];
		for (int i = 0; i < 7; i++) {
			ans[i] = generateMoves(i, false);
		}
		return ans;
	}

	public static long[][] generateAllEmptyBoardPieceAttackBitboards()
	{
		final long[][] ans = new long[7][];
		for (int i = 0; i < 7; i++) {
			ans[i] = generateMoves(i, true);
		}
		return ans;
	}

	private static long[] generateMoves(final int i, final boolean isAttackset)
	{
		long[] ans = new long[64];

		if (i == 0) {
			ans = generateEmptyBoardPawnBitboards(true, isAttackset);
		}
		if (i == 1) {
			ans = generateEmptyBoardPawnBitboards(false, isAttackset);
		}
		if (i == 2) {
			ans = generateEmptyBoardMinorPieceBitboards(true);
		}
		if (i == 3) {
			ans = generateEmptyBoardMinorPieceBitboards(false);
		}
		if (i == 4) {
			ans = generateEmptyBoardMajorPieceBitboards(true);
		}
		if (i == 5) {
			ans = generateEmptyBoardMajorPieceBitboards(false);
		}
		if (i == 6) {
			ans = generateEmptyBoardKingBitboards();
		}
		return ans;
	}

	private static long[] generateEmptyBoardKingBitboards()
	{
		final long[] ans = new long[64];
		final List<Direction> movementDirections = PieceMovementDirectionArrays.KD;
		for (final BoardSquare startSq : BoardSquare.values()) 
		{
			final List<BoardSquare> possMoveSqs = movementDirections
			.stream()
			.map(dir -> startSq.getAllSquaresInDirection(dir, false, 1))
			.flatMap(List::stream)
			.collect(toList());
			ans[startSq.ordinal()] = bitwiseOr(possMoveSqs);
		}
		return ans;
	}

	private static long[] generateEmptyBoardMajorPieceBitboards(final boolean isRook)
	{
		final long[] ans = new long[64];
		final List<Direction> movementDirections = isRook ? PieceMovementDirectionArrays.RD : PieceMovementDirectionArrays.QD;
		for (final BoardSquare startSq : BoardSquare.values()) {
			final List<BoardSquare> possMoveSqs = new ArrayList<>();
			for (final Direction dir : movementDirections) {
				final BoardSquare[] nextSqs = startSq.getAllSquaresInDirection(dir, false);
				possMoveSqs.addAll(Arrays.asList(nextSqs));
			}
			ans[startSq.ordinal()] = bitwiseOr(possMoveSqs.toArray(new BoardSquare[0]));
		}
		return ans;
	}

	private static long[] generateEmptyBoardMinorPieceBitboards(final boolean isBishop)
	{
		final long[] ans = new long[64];
		final List<Direction> movementDirections = isBishop ? PieceMovementDirectionArrays.BD : PieceMovementDirectionArrays.ND;
		for (final BoardSquare startSq : BoardSquare.values()) {
			final List<BoardSquare> possMoveSqs = new ArrayList<>();
			for (final Direction dir : movementDirections) {
				if (isBishop) {
					final BoardSquare[] nextSqs = startSq.getAllSquaresInDirection(dir, false);
					possMoveSqs.addAll(Arrays.asList(nextSqs));
				}
				else {
					final BoardSquare[] nextSqs = startSq.getAllSquaresInDirection(dir, false,  1);
					possMoveSqs.addAll(Arrays.asList(nextSqs));
				}
			}
			ans[startSq.ordinal()] = bitwiseOr(possMoveSqs.toArray(new BoardSquare[0]));
		}
		return ans;
	}

	private static long[] generateEmptyBoardPawnBitboards(final boolean isWhite, final boolean isAttackset)
	{
		final long[] ans = new long[64];
		final long startRank = isWhite ? Bitboards.RANK[1] : Bitboards.RANK[6];

		List<Direction> movementDirections = null;
		if (isWhite) {
			movementDirections = isAttackset ? PieceMovementDirectionArrays.WPA : PieceMovementDirectionArrays.WPM;
		}
		else {
			movementDirections = isAttackset ? PieceMovementDirectionArrays.BPA : PieceMovementDirectionArrays.BPM;
		}

		for (final BoardSquare startSq : BoardSquare.values()) {
			final List<BoardSquare> possMoveSqs = new ArrayList<>();
			for (final Direction dir : movementDirections) {
				// This is the case of the pawns first move
				if (!isAttackset && (startSq.asBitboard() & startRank) != 0) {
					final BoardSquare[] nextSqs = startSq.getAllSquaresInDirection(dir, false,  2);
					possMoveSqs.addAll(Arrays.asList(nextSqs));
				}
				else {
					final BoardSquare[] nextSqs = startSq.getAllSquaresInDirection(dir, false,  1);
					possMoveSqs.addAll(Arrays.asList(nextSqs));
				}
			}
			ans[startSq.ordinal()] = bitwiseOr(possMoveSqs.toArray(new BoardSquare[0]));
		}
		return ans;
	}
}
