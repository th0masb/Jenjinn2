/**
 *
 */
package jenjinn.fx;

import static java.util.Comparator.comparing;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.Side;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.collections.FList;
import xawd.jflow.iterators.misc.Pair;

/**
 * @author ThomasB
 *
 */
public final class ChessBoard
{
	private final ColorScheme colors;
	private final BoardState state;
	private final VisualBoard board = new VisualBoard();

	private BoardSquareLocations squareLocations = BoardSquareLocations.getDefault();
	private Side boardPerspective = Side.WHITE;
	private Optional<BoardSquare> selectedSquare = Optional.empty();

	public ChessBoard(ColorScheme colors, BoardState stateToWatch)
	{
		this.colors = colors;
		this.state = stateToWatch;
		board.widthProperty().addListener((x, y, z) -> Platform.runLater(this::redraw));
		board.heightProperty().addListener((x, y, z) -> Platform.runLater(this::redraw));
	}

	public void redraw()
	{
		squareLocations = calculateBoardPoints(board.getBoardSize());
		redrawBackground();
		redrawSquares();
		redrawMarkers();
		redrawPieces();
	}

	public void redrawBackground()
	{
		final double size = board.getBackingSize();
		final GraphicsContext gc = board.getBackingGC();
		gc.clearRect(0, 0, size, size);
		gc.setFill(colors.backingColor);
		gc.fillRect(0, 0, size, size);
	}

	public void redrawSquares()
	{
		final double size = board.getBoardSize(), sqSize = size / 8;
		final GraphicsContext gc = board.getBoardGC();
		gc.clearRect(0, 0, size, size);
		BoardSquare.iterateAll().forEach(square -> {
			final Point2D c = squareLocations.get(square);
			final boolean lightSquare = (square.ordinal() + square.rank()) % 2 == 0;
			gc.setFill(lightSquare ? colors.lightSquares : colors.darkSquares);
			gc.fillRect(c.getX() - sqSize / 2, c.getY() - sqSize / 2, sqSize, sqSize);
		});
	}

	public void redrawMarkers()
	{
		final double size = board.getBoardSize(), sqSize = size / 8;
		final GraphicsContext gc = board.getMarkerGC();
		gc.clearRect(0, 0, size, size);
		if (selectedSquare.isPresent()) {
			final BoardSquare sq = selectedSquare.get();
			drawLocationMarker(sq, gc, sqSize);
			final FList<ChessMove> legalMoves = LegalMoves.getAllMoves(state)
					.filter(mv -> mv.getSource().equals(sq))
					.toList();

			final long allPieces = state.getPieceLocations().getAllLocations();
			legalMoves.forEach(mv -> {
				if (bitboardsIntersect(allPieces, mv.getTarget().asBitboard())) {
					drawAttackMarker(mv.getTarget(), gc, sqSize);
				}
				else {
					drawMovementMarker(mv.getTarget(), gc, sqSize);
				}
			});
		}
	}

	private void drawAttackMarker(BoardSquare square, GraphicsContext gc, double size)
	{
		final Point2D loc = squareLocations.get(square);
		final Bounds renderBounds = RenderUtils.getSquareBounds(loc, size, 1);
		RenderUtils.strokeTarget(gc, renderBounds, colors.attackMarker);
	}

	private void drawMovementMarker(BoardSquare square, GraphicsContext gc, double size)
	{
		final Point2D centre = squareLocations.get(square);
		final Bounds locBounds = RenderUtils.getSquareBounds(centre, size, 0.9);
		RenderUtils.strokeOval(gc, locBounds, size / 20, colors.moveMarker);
	}

	private void drawLocationMarker(BoardSquare square, GraphicsContext gc, double size)
	{
		gc.setFill(colors.locationMarker);
		final Point2D centre = squareLocations.get(square);
		final Bounds locBounds = RenderUtils.getSquareBounds(centre, size, 0.9);
		gc.fillOval(locBounds.getMinX(), locBounds.getMinY(), locBounds.getWidth(), locBounds.getHeight());
	}

	public void redrawPieces()
	{
		final double size = board.getBoardSize(), sqSize = size / 8;
		final GraphicsContext gc = board.getPieceGC();
		gc.clearRect(0, 0, size, size);

		for (final ChessPiece piece : ChessPieces.all()) {
			final Image image = ImageCache.INSTANCE.getImageOf(piece);
			state.getPieceLocations().iterateLocs(piece).forEach(sq -> {
				final Point2D loc = squareLocations.get(sq);
				final Bounds b = RenderUtils.getSquareBounds(loc, sqSize, 1);
				gc.drawImage(image, b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
			});
		}
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
		} else {
			return locs.rotate(width);
		}
	}

	public void setPerspective(Side side)
	{
		if (!side.equals(boardPerspective)) {
			boardPerspective = side;
			squareLocations = calculateBoardPoints(board.getBoardSize());
			Platform.runLater(this::redraw);
		}
	}

	public void switchPerspective()
	{
		setPerspective(boardPerspective.otherSide());
	}

	public Optional<BoardSquare> getSelectedSquare()
	{
		return selectedSquare;
	}

	public void setSelectedSquare(Optional<BoardSquare> selectedSquare)
	{
		this.selectedSquare = selectedSquare;
		Platform.runLater(this::redrawMarkers);
	}

	public BoardSquare getClosestSquare(Point2D query)
	{
		return BoardSquare.iterateAll().min(comparing(sq -> query.distance(squareLocations.get(sq)))).get();
	}

	public VisualBoard getFxComponent()
	{
		return board;
	}
}
