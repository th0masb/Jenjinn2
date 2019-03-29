/**
 *
 */
package com.github.maumay.jenjinn.fx;

import java.util.HashMap;
import java.util.Map;

import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jflow.iterators.RichIterator;
import com.github.maumay.jflow.utils.Tup;

import javafx.geometry.Point2D;

/**
 * @author ThomasB
 */
public final class SquareLocations
{
	private final Map<Square, Point2D> squareToPoint = new HashMap<>();
	private final Map<Point2D, Square> pointToSquare = new HashMap<>();

	public SquareLocations(RichIterator<? extends Tup<Square, Point2D>> src)
	{
		src.forEach(pair -> put(pair._1, pair._2));
	}

	public static SquareLocations getDefault()
	{
		return Square.ALL.iter().map(
				square -> Tup.of(square, new Point2D(7 - square.file, 7 - square.rank)))
				.collect(SquareLocations::new);
	}

	private void put(Square square, Point2D point)
	{
		squareToPoint.put(square, point);
		pointToSquare.put(point, square);
	}

	public Point2D get(Square square)
	{
		if (!squareToPoint.containsKey(square)) {
			throw new IllegalStateException();
		}
		return squareToPoint.get(square);
	}

	public Square get(Point2D point)
	{
		if (!pointToSquare.containsKey(point)) {
			throw new IllegalStateException();
		}
		return pointToSquare.get(point);
	}

	public SquareLocations rotate(double boardWidth)
	{
		final Point2D translation = new Point2D(boardWidth, boardWidth);
		return Square.ALL.iter()
				.map(sq -> Tup.of(sq, get(sq).multiply(-1).add(translation)))
				.collect(SquareLocations::new);
	}
}
