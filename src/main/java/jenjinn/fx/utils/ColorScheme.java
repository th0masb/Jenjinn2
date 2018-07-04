/**
 *
 */
package jenjinn.fx.utils;

import javafx.scene.paint.Color;

/**
 * @author ThomasB
 *
 */
public final class ColorScheme
{
	public final Color darkSquares, lightSquares;
	public final Color locationMarker, moveMarker, attackMarker;
	public final Color backingColor;

	public ColorScheme(Color darkSquares, Color lightSquares, Color locationMarker, Color moveMarker,
			Color attackMarker, Color backingColor)
	{
		this.darkSquares = darkSquares;
		this.lightSquares = lightSquares;
		this.locationMarker = locationMarker;
		this.moveMarker = moveMarker;
		this.attackMarker = attackMarker;
		this.backingColor = backingColor;
	}
	
	public static ColorScheme getDefault()
	{
		throw new RuntimeException();
	}
}
