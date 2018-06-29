/**
 *
 */
package jenjinn.engine.pieces;

import static xawd.jflow.utilities.CollectionUtil.head;
import static xawd.jflow.utilities.CollectionUtil.tail;

import jenjinn.engine.enums.Side;
import xawd.jflow.collections.FlowList;
import xawd.jflow.collections.impl.ImmutableFlowList;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.factories.Iterate;

/**
 * @author ThomasB
 *
 */
public final class ChessPieces
{
	private ChessPieces() {}

	private static final FlowList<ChessPiece> ALL_PIECES = new ImmutableFlowList<>(ChessPiece.values());
	private static final FlowList<ChessPiece> WHITE_PIECES = Iterate.over(ALL_PIECES).take(6).toList();
	private static final FlowList<ChessPiece> BLACK_PIECES = Iterate.over(ALL_PIECES).drop(6).toList();
	private static final FlowList<ChessPiece> WHITE_PINNING_PIECES = new ImmutableFlowList<>(ChessPiece.WHITE_QUEEN, ChessPiece.WHITE_ROOK, ChessPiece.WHITE_BISHOP);
	private static final FlowList<ChessPiece> BLACK_PINNING_PIECES = new ImmutableFlowList<>(ChessPiece.BLACK_QUEEN, ChessPiece.BLACK_ROOK, ChessPiece.BLACK_BISHOP);

	public static FlowList<ChessPiece> all()
	{
		return ALL_PIECES;
	}

	public static FlowList<ChessPiece> white()
	{
		return WHITE_PIECES;
	}

	public static FlowList<ChessPiece> black()
	{
		return BLACK_PIECES;
	}

	public static FlowList<ChessPiece> whitePinningPieces()
	{
		return WHITE_PINNING_PIECES;
	}

	public static FlowList<ChessPiece> blackPinningPieces()
	{
		return BLACK_PINNING_PIECES;
	}

	public static FlowList<ChessPiece> pinnersOn(final Side side)
	{
		return side.isWhite()? WHITE_PINNING_PIECES : BLACK_PINNING_PIECES;
	}

	public static FlowList<ChessPiece> ofSide(final Side side)
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
