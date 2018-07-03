/**
 *
 */
package jenjinn.fx.utils;

import java.util.Map;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.ChessPiece;
import xawd.jflow.collections.FlowList;
import xawd.jflow.collections.Lists;
import xawd.jflow.iterators.misc.Pair;

/**
 * @author ThomasB
 *
 */
public final class ChessBoard extends Region
{
	private final ColorScheme colors;
	private final Canvas backingCanvas = new ResizableCanvas();
	private final Canvas boardCanvas = new ResizableCanvas();
	private final Canvas markerCanvas = new ResizableCanvas();
	private final Canvas pieceCanvas = new ResizableCanvas();
	private final Canvas interactionLayer = new ResizableCanvas();
	private final FlowList<Canvas> boardCanvasStack = Lists.build(boardCanvas, markerCanvas, pieceCanvas,
			interactionLayer);

	private BoardSquareLocations squareLocations = BoardSquareLocations.getDefault();
	private Map<BoardSquare, ChessPiece> pieceLocations;
	private final Side boardPerspective = Side.WHITE;

	public ChessBoard(ColorScheme colors)
	{
		this.colors = colors;
	}

	@Override
	protected void layoutChildren()
	{
		final double w = getWidth(), h = getHeight();
		if (w > 0.01 && h > 0.01) {
			final boolean thin = w < h;
			final double sideLength = Math.min(w, h);
			final double boardSideLength = 19.0 / 20 * sideLength;
			final double canvasLengthDifference = sideLength - boardSideLength;

			final Point2D backingUL = new Point2D(thin ? 0 : (w - sideLength) / 2, thin ? (h - sideLength) / 2 : 0);
			final Point2D boardUL = backingUL.add(canvasLengthDifference / 2, canvasLengthDifference / 2);

			backingCanvas.resizeRelocate(backingUL.getX(), backingUL.getY(), sideLength, sideLength);
			boardCanvasStack.forEach(
					canvas -> canvas.resizeRelocate(boardUL.getX(), boardUL.getY(), boardSideLength, boardSideLength));
			squareLocations = calculateBoardPoints(boardCanvas.getWidth());
			redraw();
		}
	}

	private void redraw()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Calculates an association of BoardSquares to the centre point of their
	 * required visual bounds relative to the local coordinate space of the canvas
	 * they will be drawn onto.
	 *
	 * @param width
	 *            The width of the board canvas.
	 */
	private BoardSquareLocations calculateBoardPoints(double width)
	{
		final double squareWidth = width / 8;
		final BoardSquareLocations locs = BoardSquare.iterateAll()
		.map(sq -> Pair.of(sq, new Point2D((7.5 - sq.file()) * squareWidth, (7.5 - sq.rank()) * squareWidth)))
		.build(BoardSquareLocations::new);

		if (boardPerspective.isWhite()) {
			return locs;
		}
		else {
			return locs.rotate(width);
		}
	}
}
