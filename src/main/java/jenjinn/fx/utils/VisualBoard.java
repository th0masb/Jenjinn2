/**
 *
 */
package jenjinn.fx.utils;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import xawd.jflow.collections.FlowList;
import xawd.jflow.collections.Lists;

/**
 * @author ThomasB
 *
 */
public final class VisualBoard extends Region
{
	private final Canvas backingCanvas = new ResizableCanvas();
	private final Canvas boardCanvas = new ResizableCanvas();
	private final Canvas markerCanvas = new ResizableCanvas();
	private final Canvas pieceCanvas = new ResizableCanvas();
	private final Canvas interactionLayer = new ResizableCanvas();

	private final FlowList<Canvas> boardCanvasStack = Lists.build(
			boardCanvas, markerCanvas, pieceCanvas, interactionLayer);

	public VisualBoard()
	{
		getChildren().add(backingCanvas);
		getChildren().addAll(boardCanvasStack);
		getChildren().add(interactionLayer);
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
		}
	}

	public double getBoardSize()
	{
		return boardCanvas.getWidth();
	}

	public double getBackingSize()
	{
		return backingCanvas.getWidth();
	}

	public GraphicsContext getBackingGC()
	{
		return backingCanvas.getGraphicsContext2D();
	}

	public GraphicsContext getBoardGC()
	{
		return boardCanvas.getGraphicsContext2D();
	}

	public GraphicsContext getMarkerGC()
	{
		return markerCanvas.getGraphicsContext2D();
	}

	public GraphicsContext getPieceGC()
	{
		return pieceCanvas.getGraphicsContext2D();
	}

	public void setMouseClickInteractionProcedure(EventHandler<? super MouseEvent> evt)
	{
		interactionLayer.setOnMouseClicked(evt);
	}

	public void setInteractionEnabled()
	{
		interactionLayer.toBack();
	}

	public void setInteractionDisabled()
	{
		interactionLayer.toFront();
	}
}
