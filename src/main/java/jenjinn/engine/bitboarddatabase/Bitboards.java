package jenjinn.engine.bitboarddatabase;

import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection1.generateAllEmptyBoardPieceAttackBitboards;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection1.generateAllEmptyBoardPieceMovementBitboards;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection1.generateAntidiagonalBitboards;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection1.generateDiagonalBitboards;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection1.generateFileBitboards;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection1.generateRankBitboards;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection1.generateSingleOccupancyBitboards;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection2.generateAllBishopOccupancyVariations;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection2.generateAllRookOccupancyVariations;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection2.generateBishopMagicBitshifts;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection2.generateBishopOccupancyMasks;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection2.generateRookMagicBitshifts;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection2.generateRookOccupancyMasks;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection3.generateBishopMagicMoveDatabase;
import static jenjinn.engine.bitboarddatabase.BitboardsInitialisationSection3.generateRookMagicMoveDatabase;
import static jenjinn.engine.misc.BitboardUtils.bitwiseOr;

/**
 * A class initialising all the variables we need to implement board
 * representation via the well known magic bitboard approach.
 *
 * @author TB
 * @date 21 Jan 2017
 */
public class Bitboards
{
	// Section 1 - Basic areas of the board.
	
	private static final long[] SOB;
	
	/**
	 * Access array of 64 bitboards representing single squares on a chessboard. They are
	 * ordered from h1 to a8, the reason for this seemingly strange ordering is due
	 * to the fact this is the natural representation taking into account these
	 * bitboards are just numbers growing in size. The bitboard representing h1 is
	 * the smallest, a8 the largest.
	 */
	public static long singleOccupancyBitboard(final int squareIndex)
	{
		return SOB[squareIndex];
	}

	
	private static final long[] RANK;
	
	/**
	 * Access array of 8 bitboards representing the ranks on a chessboard. Ordered rank 1
	 * up to rank 8.
	 */
	public static long rankBitboard(final int rankIndex)
	{
		return RANK[rankIndex];
	}

	
	public static final long[] FILE;
	
	/**
	 * Access array of 8 bitboards representing the files on a chessboard. Ordered from h
	 * to a.
	 */
	public static long fileBitboard(final int fileIndex)
	{
		return FILE[fileIndex];
	}

	/**
	 * Array of 15 bitboards representing the diagonals of gradient 1 on a
	 * chessboard. Ordered from right to left.
	 */
	public static final long[] DGNL;

	/**
	 * Array of 15 bitboards representing the diagonals of gradient -1 on a
	 * chessboard. Ordered from left to right.
	 */
	public static final long[] ADGNL;

	static {
		SOB = generateSingleOccupancyBitboards();
		RANK = generateRankBitboards();
		FILE = generateFileBitboards();
		DGNL = generateDiagonalBitboards();
		ADGNL = generateAntidiagonalBitboards();
	}

	/** Universal set bitboard (all bits set) */
	public static final long USET = bitwiseOr(RANK);

	/** Border set */
	public static final long BSET = bitwiseOr(new long[] { RANK[0], FILE[0], RANK[7], FILE[7] });

	/**
	 * Empty board movesets, ordered wp, bp, Bishop, Knight, Rook, Queen, King
	 */
	public static final long[][] EBM;

	/**
	 * Empty board attacksets, ordered wp, bp, Bishop, Knight, Rook, Queen, King
	 */
	public static final long[][] EBA;

	static {
		EBM = generateAllEmptyBoardPieceMovementBitboards();
		EBA = generateAllEmptyBoardPieceAttackBitboards();
	}

	// -----------------------------------
	// Section 2 - Constants required for Magic bitboard implementation for
	// Rooks, Bishops amd Pawn first moves

	/**
	 * Bishop occupancy masks for each square. An bishop occupancy mask for square i
	 * is the corresponding empty board bishop moveset & ~(border set).
	 */
	public static final long[] BOM;

	/**
	 * Rook occupancy masks for each square. Slightly more complicated to construct
	 * than bishop ones if the rook lies in the border set. Basically you just
	 * remove the end squares of the rook move path on an empty board and keep the
	 * rest (except the square containing the rook).
	 */
	public static final long[] ROM;

	/**
	 * Container of all the possible bishop occupancy variations for each different
	 * square. A bov for square i is BOM[i] & (location of all pieces on the board).
	 * There are 2^(Cardinality(BOM[i])) variations.
	 */
	public static final long[][] BOV;

	/**
	 * Container of all the possible rook occupancy variations for each different
	 * square. A rov for square i is ROM[i] & (location of all pieces on the board).
	 * There are 2^(Cardinality(ROM[i])) variations.
	 */
	public static final long[][] ROV;

	/**
	 * Bishop magic bitshift values for each square. BMB[i] = Cardinality(BOM[i]).
	 * The magic bitshifts form part of the surjective mapping definition behind
	 * magic bitboards
	 */
	public static final byte[] BMB;

	/**
	 * Rook magic bitshift values for each square. RMB[i] = Cardinality(ROM[i]). The
	 * magic bitshifts form part of the surjective mapping definition behind magic
	 * bitboards
	 */
	public static final byte[] RMB;

	/**
	 * For pawns all occ masks have the same cardinality so there is only one magic
	 * bitshift value
	 */
	public static final byte PFMB = 62;

	/**
	 * Bishop magic number values for each square. Used for defining the surjective
	 * map definition used in magic bitboards.
	 */
	public static final long[] BMN = { 0x8480100440302L, 0x200200a1010000L, 0x4010040441508100L, 0x491040080030021L,
									   0x21104080208000L, 0x1032011000000L, 0x41c0128080000L, 0x2002020201040200L,
									   0x120430040040L, 0x201040812084209L, 0x4220801002204L, 0x8044502000000L,
									   0x10031040000102L, 0x51008040004L, 0x10080a0090041000L, 0x4060002208040400L,
									   0x480a000420042410L, 0x20880801041882L, 0x8005408011012L, 0x800048e004000L,
									   0x1001820080000L, 0x203410080821L, 0x4800800410881800L, 0x411400022021000L,
									   0x4200040080100L, 0x3200008024401L, 0x2000480001020402L, 0x1408080000820500L,
									   0x1060004008400L, 0x2200200200b000L, 0xc018004002020200L, 0x1020001084500L,
									   0x208334000086808L, 0x4040404200120L, 0x404004840040400L, 0x600800010104L,
									   0x40004100401100L, 0x80820080041000L, 0xc004010040020820L, 0x12006202010182L,
									   0x400880840040800L, 0xc008404401108L, 0x8011080100c800L, 0x1024010400200L,
									   0x5010124000201L, 0x10a0221000400209L, 0x118080080800400L, 0x4008202000040L,
									   0x905108220200001L, 0x40482008a200008L, 0xa0084040880L, 0x2220084020a80000L,
									   0x241002020004L, 0x2500048408820000L, 0x42020224050000L, 0x20010200810000L,
									   0x2002082086000L, 0x40020104460200L, 0x20084018820L, 0x110000a2420200L,
									   0x2200000010020200L, 0x220021a0200L, 0x402041006020400L, 0x20110102040840L };

	/**
	 * Rook magic number values for each square. Used for defining the surjective
	 * map definition used in magic bitboards.
	 */
	public static final long[] RMN = { 0x10800480a0104000L, 0x40002000403000L, 0x80281000200080L, 0x800c0800500280L,
									   0x8200200410081200L, 0x4a00080200040510L, 0x2180408002000100L, 0x180004100002080L,
									   0x4000800080204000L, 0x802010814000L, 0x444801000822001L, 0x801000680080L,
									   0x1120808004000800L, 0x42000408060010L, 0x80800200110080L, 0x800140800500L,
									   0x208000804008L, 0x140240400a201000L, 0x200808010002000L, 0x1010008201000L,
									   0x8808018018400L, 0x808004000200L, 0x110808002000500L, 0x20021004084L,
									   0x1802080004000L, 0x200040100040L, 0x200080803000L, 0x100080080284L,
									   0x28080080800400L, 0x800040080020080L, 0x80400100a01L, 0x202100090000804aL,
									   0x401282800020L, 0x200040401000L, 0x4200080801000L, 0x40800800801000L,
									   0x800800801400L, 0x800c00800200L, 0x8000500224000801L, 0x800840800100L,
									   0x90824002208000L, 0x420600050004000L, 0x406001010010L, 0x20100008008080L,
									   0x200040801010010L, 0x20004008080L, 0x9008600010004L, 0x100010080420004L,
									   0x800040002004c0L, 0x400080210100L, 0x200200081100080L, 0x8000880080100080L,
									   0x1080082040080L, 0x4068810400020080L, 0x20801100400L, 0x1000202418100L,
									   0x408001102501L, 0x11008042002852L, 0x8800406001043009L, 0x1012000821100442L,
									   0x1000442080011L, 0x1001000c00020801L, 0x400082104821004L, 0x2080010140208402L };

	static {
		BOV = generateAllBishopOccupancyVariations();
		ROV = generateAllRookOccupancyVariations();
		ROM = generateRookOccupancyMasks();
		BOM = generateBishopOccupancyMasks();
		RMB = generateRookMagicBitshifts();
		BMB = generateBishopMagicBitshifts();
	}

	// --------------------------------------------
	// Section 3 - the move databases

	/**
	 * Bishop move database implementing the magic bitboard mapping technique. The
	 * domain of the map is the set of all bishop occupancy variations and the
	 * target of the map is this database.
	 */
	public static final long[][] BMM;

	/**
	 * Rook move database implementing the magic bitboard mapping technique. The
	 * domain of the map is the set of all rook occupancy variations and the target
	 * of the map is this database.
	 */
	public static final long[][] RMM;

	static {
		RMM = generateRookMagicMoveDatabase();
		BMM = generateBishopMagicMoveDatabase();
	}
}
