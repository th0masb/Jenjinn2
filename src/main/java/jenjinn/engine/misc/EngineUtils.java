package jenjinn.engine.misc;

import static io.xyz.chains.utilities.CollectionUtil.take;

import jenjinn.engine.enums.BoardSquare;

/**
 * A general utility class containing various useful static methods.
 * @author TB
 * @date 21 Jan 2017
 */
public class EngineUtils
{
	/** Takes a bitboard and returns a 64 character string representation */
	public static String bitboardToString(final long l)
	{
		final String lString = Long.toBinaryString(l);
		final byte paddingZeros = (byte) (64 - lString.length());
		final StringBuilder b = new StringBuilder(64);
		for (byte i = 0; i < paddingZeros; i++) {
			b.append("0");
		}
		for (byte i = 0; i < lString.length(); i++) {
			b.append(lString.charAt(i));
		}
		return b.toString();
	}

	/** Prints n tab separated bitboards side by side to the console */
	public static void printNbitBoards(final long... args)
	{
		final byte n = (byte) args.length;
		final String gap = "\t";
		final String[] asStrings = new String[n];
		for (byte i = 0; i < n; i++) {
			asStrings[i] = bitboardToString(args[i]);
		}
		for (byte i = 0; i < 8; i++) {
			final StringBuilder builder = new StringBuilder();
			for (byte j = 0; j < n; j++) {
				builder.append(asStrings[j].substring(8 * i, 8 * (i + 1)));
				if (j < n - 1) {
					builder.append(gap);
				}
			}
			System.out.println(builder.toString());
		}
	}
	
	
	/**
	 * Recursive method to calculate and return all possible bitboards arising from
	 * performing bitwise | operation on each element of each subset of the powerset
	 * of the given array. The size of the returned array is 2^(array.length).
	 */
	public static long[] findAllPossibleOrCombos(final long[] array)
	{
		final int length = array.length;
		if (length == 1) {
			return new long[] { 0L, array[0] };
		}
		else {
			final long[] ans = new long[(int) Math.pow(2.0, length)];
			final long[] recursiveAns = findAllPossibleOrCombos(take(length - 1, array));
			int ansIndexCounter = 0;
			int recursiveAnsIndexCounter = 0;
			for (int j = 0; j < recursiveAns.length; j++) {
				for (long i = 0; i < 2; i++) {
					ans[ansIndexCounter] = recursiveAns[recursiveAnsIndexCounter] | (array[length - 1] * i);
					ansIndexCounter++;
				}
				recursiveAnsIndexCounter++;
			}
			return ans;
		}
	}

//	public static StandardMove[] bitboardToMoves(final byte loc, final long bitboard)
//	{
//		final int bitboardCard = Long.bitCount(bitboard);
//
//		final StandardMove[] mvs = new StandardMove[bitboardCard];
//
//		int ctr = 0;
//		final byte[] setBits = getSetBits(bitboard);
//		for (final byte b : setBits) {
//			mvs[ctr++] = StandardMove.get(loc, b);
//		}
//
//		return mvs;
//	}

//	public static long[] getStartingPieceLocs()
//	{
//		final long[] start = new long[12];
//
//		for (int i = 0; i < 12; i++) {
//			start[i] = ChessPiece.get(i).getStartBitboard();
//		}
//
//		return start;
//	}

//	public static long getStartingDevStatus()
//	{
//		final long[] startLocs = EngineUtils.getStartingPieceLocs();
//
//		return startLocs[1] | startLocs[2] | startLocs[7] | startLocs[8] | ((startLocs[0] | startLocs[6]) & (Bitboards.FILE[3] | Bitboards.FILE[4]));
//	}


//	public static void writeMoves(final List<ChessMove> toWrite, final Path path) throws IOException
//	{
//		final List<String> asStrings = new ArrayList<>();
//
//		for (final ChessMove mv : toWrite) {
//			asStrings.add(mv.toCompactString());
//		}
//
//		Files.write(path, asStrings);
//	}

//	public static List<ChessMove> readMoves(final Path path) throws IOException
//	{
//		final List<String> lines = Files.readAllLines(path);
//		final List<ChessMove> mvs = new ArrayList<>();
//
//		for (final String line : lines) {
//			mvs.add(ChessMove.fromCompactString(line));
//		}
//
//		return mvs;
//	}

	public static String formatPieceTable(final short[] ptable)
	{
		assert ptable.length == 64;
		int maxlen = 0;
		for (final short val : ptable) {
			maxlen = Math.max(Integer.toString(val).length(), maxlen);
		}

		final StringBuilder sb = new StringBuilder();
		int ctr = 63;
		for (int i = 63; i >= 0; i--) {
			final int val = ptable[i];
			sb.append(getPaddedString(val, maxlen));
			sb.append(" ");
			if ((--ctr) % 8 == 7) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private static String getPaddedString(final int i, final int len)
	{
		final StringBuilder sb = new StringBuilder(Integer.toString(i));
		while (sb.length() < len) {
			sb.append(" ");
		}
		return sb.toString();
	}

	public static void main(final String[] args)
	{
		// System.out.println(Arrays.toString(getSetBits(33746390L)));
		// System.out.println(BBDB.SOB[0]);

		final short[] testP = new short[64];
		testP[0] = 1;
		testP[BoardSquare.c4.ordinal()] = -3;
		System.out.println(formatPieceTable(testP));
	}
}
