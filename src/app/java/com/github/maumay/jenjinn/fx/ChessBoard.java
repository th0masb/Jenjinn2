/**
 *
 */
package com.github.maumay.jenjinn.fx;

import static java.util.Comparator.comparing;

import java.util.Optional;

import com.github.maumay.jenjinn.base.Side;
import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.bitboards.Bitboard;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.calculators.LegalMoves;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jenjinn.pieces.ChessPieces;
import com.github.maumay.jenjinn.pieces.Piece;
import com.github.maumay.jflow.utils.Tup;
import com.github.maumay.jflow.vec.Vec;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * @author ThomasB
 *
 */
public final class ChessBoard
{
	private final ColorScheme colors;
	private final BoardState state;
	private final VisualBoard board = new VisualBoard();

	private SquareLocations squareLocations = SquareLocations.getDefault();
	private Side boardPerspective = Side.WHITE;
	private Optional<Square> selectedSquare = Optional.empty();

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
		Square.ALL.iter().forEach(square -> {
			final Point2D c = squareLocations.get(square);
			final boolean lightSquare = (square.ordinal() + square.rank) % 2 == 0;
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
			final Square sq = selectedSquare.get();
			drawLocationMarker(sq, gc, sqSize);
			Vec<ChessMove> legalMoves = LegalMoves.getAllMoves(state)
					.filter(mv -> mv.getSource().equals(sq)).toVec();

			final long allPieces = state.getPieceLocations().getAllLocations();
			legalMoves.forEach(mv -> {
				if (Bitboard.intersects(allPieces, mv.getTarget().bitboard)) {
					drawAttackMarker(mv.getTarget(), gc, sqSize);
				} else {
					drawMovementMarker(mv.getTarget(), gc, sqSize);
				}
			});
		}
	}

	private void drawAttackMarker(Square square, GraphicsContext gc, double size)
	{
		final Point2D loc = squareLocations.get(square);
		final Bounds renderBounds = RenderUtils.getSquareBounds(loc, size, 1);
		RenderUtils.strokeTarget(gc, renderBounds, colors.attackMarker);
	}

	private void drawMovementMarker(Square square, GraphicsContext gc, double size)
	{
		final Point2D centre = squareLocations.get(square);
		final Bounds locBounds = RenderUtils.getSquareBounds(centre, size, 0.9);
		RenderUtils.strokeOval(gc, locBounds, size / 20, colors.moveMarker);
	}

	private void drawLocationMarker(Square square, GraphicsContext gc, double size)
	{
		gc.setFill(colors.locationMarker);
		final Point2D centre = squareLocations.get(square);
		final Bounds locBounds = RenderUtils.getSquareBounds(centre, size, 0.9);
		gc.fillOval(locBounds.getMinX(), locBounds.getMinY(), locBounds.getWidth(),
				locBounds.getHeight());
	}

	public void redrawPieces()
	{
		final double size = board.getBoardSize(), sqSize = size / 8;
		final GraphicsContext gc = board.getPieceGC();
		gc.clearRect(0, 0, size, size);

		for (Piece piece : ChessPieces.ALL) {
			final Image image = ImageCache.INSTANCE.getImageOf(piece);
			state.getPieceLocations().iterateLocs(piece).forEach(sq -> {
				final Point2D loc = squareLocations.get(sq);
				final Bounds b = RenderUtils.getSquareBounds(loc, sqSize, 1);
				gc.drawImage(image, b.getMinX(), b.getMinY(), b.getWidth(),
						b.getHeight());
			});
		}
	}

	/**
	 * Calculates an association of Squares to the centre point of their required
	 * visual bounds relative to the local coordinate space of the canvas they will
	 * be drawn onto.
	 *
	 * @param width The width of the board canvas.
	 */
	private SquareLocations calculateBoardPoints(double width)
	{
		final double squareWidth = width / 8;
		final SquareLocations locs = Square.ALL.iter()
				.map(sq -> Tup.of(sq,
						new Point2D((7.5 - sq.file) * squareWidth,
								(7.5 - sq.rank) * squareWidth)))
				.collect(SquareLocations::new);

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

	public Optional<Square> getSelectedSquare()
	{
		return selectedSquare;
	}

	public void setSelectedSquare(Optional<Square> selectedSquare)
	{
		this.selectedSquare = selectedSquare;
		Platform.runLater(this::redrawMarkers);
	}

	public Square getClosestSquare(Point2D query)
	{
		return Square.ALL.iter()
				.min(comparing(sq -> query.distance(squareLocations.get(sq))));
	}

	public VisualBoard getFxComponent()
	{
		return board;
	}
}
