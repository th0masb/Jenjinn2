/**
 *
 */
package jenjinn.fx;

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
		return new ColorScheme(Color.web("#92db95", 0.8),
				Color.web("#f7fffb", 0.8), 
				Color.web("#74777c", 0.8), // 
				Color.web("#9da2aa", 0.8),
				Color.INDIANRED.deriveColor(0, 0.9, 0.9, 0.8), 
				Color.gray(0.5, 0.8));
	}
}
