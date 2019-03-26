/**
 *
 */
package jenjinn.fx;


import javafx.scene.canvas.Canvas;

/**
 * @author ThomasB
 */
public final class ResizableCanvas extends Canvas
{
	public ResizableCanvas()
	{
	}

	public ResizableCanvas(final double width, final double height)
	{
		super(width, height);
	}

	@Override
	public boolean isResizable()
	{
		return true;
	}

	@Override
	public void resize(final double width, final double height)
	{
		super.setWidth(width);
		super.setHeight(height);
	}
}
