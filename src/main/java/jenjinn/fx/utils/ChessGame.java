/**
 *
 */
package jenjinn.fx.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;

/**
 * @author ThomasB
 *
 */
public final class ChessGame extends Region
{
	private static final double MIN_MOVETIME = 2.0, MAX_MOVETIME = 10;

	private final Canvas backingCanvas = new ResizableCanvas();
	private final Canvas boardCanvas = new ResizableCanvas();
	private final Canvas markerCanvas = new ResizableCanvas();
	private final Canvas pieceCanvas = new ResizableCanvas();
	private final Canvas interactionLayer = new ResizableCanvas();

	private final double moveTime = 5;
	/**
	 *
	 */
	public ChessGame()
	{
		// TODO Auto-generated constructor stub
	}

}
