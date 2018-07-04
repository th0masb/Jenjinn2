/**
 *
 */
package jenjinn.fx;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * @author ThomasB
 *
 */
public class RenderUtils
{
	private static final double TARGET_OUTER_BOUNDS_LENGTH = 0.9;
	private static final double TARGET_MIDDLE_BOUNDS_LENGTH = 0.65;
	private static final double TARGET_INNER_BOUNDS_LENGTH = 0.2;
	private static final double XHAIR_EXTENSION_FACTOR = 1.3;

	public static Bounds getSquareBounds(final Point2D p, double squareLength, double lengthCap)
	{
		assert 0 < lengthCap && lengthCap <= 1;

		final double sl = squareLength * lengthCap;
		return new BoundingBox(p.getX() - sl / 2, p.getY() - sl / 2, sl, sl);
	}

	public static void strokeOval(GraphicsContext gc, Bounds b, double lineWidth, Paint p)
	{
		gc.setStroke(p);
		gc.setLineWidth(lineWidth);
		gc.strokeOval(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
	}

	public static void strokeTarget(GraphicsContext gc, Bounds b, Paint p)
	{
		gc.setStroke(p);
		final Point2D centre = new Point2D(b.getMinX() + b.getWidth() / 2, b.getMinY() + b.getHeight() / 2);

		final Bounds outer = getSquareBounds(centre, b.getWidth(), TARGET_OUTER_BOUNDS_LENGTH);
		final Bounds middle = getSquareBounds(centre, b.getWidth(), TARGET_MIDDLE_BOUNDS_LENGTH);
		final Bounds inner = getSquareBounds(centre, b.getWidth(), TARGET_INNER_BOUNDS_LENGTH);

		final double outerLW = b.getWidth() / 23, middleLW = b.getWidth() / 29, innerLW = b.getWidth() / 29;

		strokeOval(gc, outer, outerLW, p);
		strokeOval(gc, middle, middleLW, p);
		strokeOval(gc, inner, innerLW, p);

		final double xHairWidth = b.getWidth() / 29;
		gc.setLineWidth(xHairWidth);

		final double ex = XHAIR_EXTENSION_FACTOR, innerR = inner.getWidth() / 2, outerR = ex * outer.getWidth() / 2;

		final List<Point2D> pathPoints = new ArrayList<>();
		pathPoints.add(centre.add(new Point2D(0, innerR)));
		pathPoints.add(centre.add(new Point2D(0, outerR)));
		pathPoints.add(centre.add(new Point2D(0, -innerR)));
		pathPoints.add(centre.add(new Point2D(0, -outerR)));
		pathPoints.add(centre.add(new Point2D(innerR, 0)));
		pathPoints.add(centre.add(new Point2D(outerR, 0)));
		pathPoints.add(centre.add(new Point2D(-innerR, 0)));
		pathPoints.add(centre.add(new Point2D(-outerR, 0)));

		strokePath(gc, pathPoints);
	}

	private static void strokePath(GraphicsContext gc, List<Point2D> pathPoints)
	{
		gc.beginPath();
		for (int i = 0; i < pathPoints.size() - 1; i += 2) {
			final Point2D first = pathPoints.get(i), second = pathPoints.get(i + 1);
			gc.moveTo(first.getX(), first.getY());
			gc.lineTo(second.getX(), second.getY());
		}
		gc.stroke();
		gc.closePath();
	}

}
