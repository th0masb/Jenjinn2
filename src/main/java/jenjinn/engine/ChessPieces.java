/**
 *
 */
package jenjinn.engine;

import static java.util.Arrays.asList;
import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;

import java.util.Collections;
import java.util.List;

import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.enums.Side;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.construction.Iterate;

/**
 * @author ThomasB
 *
 */
public final class ChessPieces
{
	private ChessPieces() {}

	private static final List<ChessPiece> ALL_PIECES = Collections.unmodifiableList(asList(ChessPiece.values()));
	private static final List<ChessPiece> WHITE_PIECES = Iterate.over(ALL_PIECES).take(6).toImmutableList();
	private static final List<ChessPiece> BLACK_PIECES = Iterate.over(ALL_PIECES).skip(6).toImmutableList();
	private static final List<ChessPiece> WHITE_PINNING_PIECES = asList(ChessPiece.WHITE_QUEEN, ChessPiece.WHITE_ROOK, ChessPiece.WHITE_BISHOP);
	private static final List<ChessPiece> BLACK_PINNING_PIECES = asList(ChessPiece.BLACK_QUEEN, ChessPiece.BLACK_ROOK, ChessPiece.BLACK_BISHOP);

	public static List<ChessPiece> all()
	{
		return ALL_PIECES;
	}

	public static List<ChessPiece> white()
	{
		return WHITE_PIECES;
	}

	public static List<ChessPiece> black()
	{
		return BLACK_PIECES;
	}

	public static List<ChessPiece> whitePinningPieces()
	{
		return WHITE_PINNING_PIECES;
	}

	public static List<ChessPiece> blackPinningPieces()
	{
		return BLACK_PINNING_PIECES;
	}

	public static List<ChessPiece> pinnersOn(final Side side)
	{
		return side.isWhite()? WHITE_PINNING_PIECES : BLACK_PINNING_PIECES;
	}

	public static List<ChessPiece> ofSide(final Side side)
	{
		return side.isWhite()? white() : black();
	}

	public static Flow<ChessPiece> iterate()
	{
		return Iterate.over(all());
	}

	public static ChessPiece fromIndex(final int index)
	{
		return ChessPiece.values()[index];
	}

	public static ChessPiece king(final Side side)
	{
		return side.isWhite()? tail(white()) : tail(black());
	}

	public static ChessPiece pawn(final Side side)
	{
		return side.isWhite()? head(white()) : head(black());
	}
}
