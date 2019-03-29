/**
 *
 */
package com.github.maumay.jenjinn.fx;

import com.github.maumay.jflow.vec.Vec;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

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

	private final Vec<Canvas> boardCanvasStack = Vec.of(boardCanvas, markerCanvas,
			pieceCanvas, interactionLayer);

	public VisualBoard()
	{
		getChildren().add(backingCanvas);
		getChildren().addAll(boardCanvasStack.toList());
	}

	@Override
	protected void layoutChildren()
	{
		double w = getWidth(), h = getHeight();
		if (w > 0.01 && h > 0.01) {
			boolean thin = w < h;
			double sideLength = Math.min(w, h);
			double boardSideLength = 19.0 / 20 * sideLength;
			double canvasLengthDifference = sideLength - boardSideLength;

			Point2D backingUL = new Point2D(thin ? 0 : (w - sideLength) / 2,
					thin ? (h - sideLength) / 2 : 0);
			Point2D boardUL = backingUL.add(canvasLengthDifference / 2,
					canvasLengthDifference / 2);

			backingCanvas.resizeRelocate(backingUL.getX(), backingUL.getY(), sideLength,
					sideLength);
			boardCanvasStack.forEach(canvas -> canvas.resizeRelocate(boardUL.getX(),
					boardUL.getY(), boardSideLength, boardSideLength));
		}
	}

	@Override
	public void resize(double width, double height)
	{
		super.resize(width, height);
		double sideLength = Math.min(width, height);
		double boardSideLength = 19.0 / 20 * sideLength;
		backingCanvas.resize(sideLength, sideLength);
		boardCanvasStack
				.forEach(canvas -> canvas.resize(boardSideLength, boardSideLength));
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
		interactionLayer.toFront();
	}

	public void setInteractionDisabled()
	{
		interactionLayer.toBack();
	}
}
