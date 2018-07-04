/**
 *
 */
package jenjinn.fx;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Point2D;
import jenjinn.engine.base.BoardSquare;
import xawd.jflow.iterators.Flow;
import xawd.jflow.iterators.misc.Pair;

/**
 * @author ThomasB
 */
public final class BoardSquareLocations
{
	private final Map<BoardSquare, Point2D> squareToPoint = new HashMap<>();
	private final Map<Point2D, BoardSquare> pointToSquare = new HashMap<>();

	public BoardSquareLocations(Flow<Pair<BoardSquare, Point2D>> src)
	{
		src.forEach(pair -> put(pair.first(), pair.second()));
	}

	public static BoardSquareLocations getDefault()
	{
		return BoardSquare.iterateAll()
		.map(square -> Pair.of(square, new Point2D(7 - square.file(), 7 - square.rank())))
		.build(BoardSquareLocations::new);
	}

	private void put(BoardSquare square, Point2D point)
	{
		squareToPoint.put(square, point);
		pointToSquare.put(point, square);
	}

	public Point2D get(BoardSquare square)
	{
		if (!squareToPoint.containsKey(square)) {
			throw new IllegalStateException();
		}
		return squareToPoint.get(square);
	}

	public BoardSquare get(Point2D point)
	{
		if (!pointToSquare.containsKey(point)) {
			throw new IllegalStateException();
		}
		return pointToSquare.get(point);
	}

	public BoardSquareLocations rotate(double boardWidth)
	{
		final Point2D translation = new Point2D(boardWidth, boardWidth);
		return BoardSquare.iterateAll()
				.map(sq -> Pair.of(sq, get(sq).multiply(-1).add(translation)))
				.build(BoardSquareLocations::new);
	}
}
