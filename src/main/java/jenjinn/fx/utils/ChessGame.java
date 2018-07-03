/**
 *
 */
package jenjinn.fx.utils;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.entity.Jenjinn;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.moves.ChessMove;
import xawd.jflow.collections.FlowList;
import xawd.jflow.collections.impl.FlowArrayList;
import xawd.jflow.utilities.Optionals;

/**
 * @author ThomasB
 *
 */
public final class ChessGame extends Region
{
	private static final double MIN_MOVETIME = 2.0, MAX_MOVETIME = 10;
	private final double moveTime = 5;

	private final Jenjinn jenjinn;
	private final BoardState stateOfPlay;
	private final ChessBoard board;
	private final FlowList<ChessMove> movesPlayed;

	private Optional<BoardSquare> firstSelection, secondSelection;

	public ChessGame(ColorScheme colors)
	{
		jenjinn = new Jenjinn();
		stateOfPlay = StartStateGenerator.createStartBoard();
		board = new ChessBoard(colors, stateOfPlay);
		movesPlayed = new FlowArrayList<>();
		firstSelection = Optional.empty();
		secondSelection = Optional.empty();
		getChildren().add(board.getBoard());
		board.getBoard().setMouseClickInteractionProcedure(this::handleMouseClicks);
	}

	private void handleMouseClicks(MouseEvent evt)
	{
		final Point2D clickTarget = new Point2D(evt.getX(), evt.getY());
		final BoardSquare correspondingSquare = board.getClosestSquare(clickTarget);

		if (firstSelection.isPresent()) {
			final BoardSquare src = firstSelection.get();
			final Optional<ChessMove> mv = LegalMoves.getAllMoves(stateOfPlay)
					.filter(m -> m.getSource().equals(src) && m.getTarget().equals(correspondingSquare))
					.safeNext();

			if (mv.isPresent()) {

			}
			else {
				firstSelection = Optional.empty();
				board.setSelectedSquare(firstSelection);
			}
		}
		else {
			if (bitboardsIntersect(correspondingSquare.asBitboard(), getActiveLocations())) {
				firstSelection = Optionals.of(correspondingSquare);
				board.setSelectedSquare(firstSelection);
			}
		}
	}

	private void processMove(ChessMove mv)
	{
		firstSelection = Optional.empty();
		secondSelection = Optional.empty();
		board.setSelectedSquare(firstSelection);
		mv.makeMove(stateOfPlay);
		Platform.runLater(board::redraw);

	}

	private long getActiveLocations()
	{
		return stateOfPlay.getPieceLocations().getSideLocations(stateOfPlay.getActiveSide());
	}

	@Override
	protected void layoutChildren()
	{
		board.getBoard().resizeRelocate(0, 0, getWidth(), getHeight());
	}
}
