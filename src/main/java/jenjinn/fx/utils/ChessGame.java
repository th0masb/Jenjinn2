/**
 *
 */
package jenjinn.fx.utils;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.boardstate.calculators.TerminationState;
import jenjinn.engine.entity.Jenjinn;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.GameTermination;
import jenjinn.engine.enums.Side;
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
//	private static final double MIN_MOVETIME = 2.0, MAX_MOVETIME = 10;
	private final double moveTime = 5;

	private final Property<Side> sideToMove = new SimpleObjectProperty<>(Side.WHITE);
	private final Property<GameTermination> terminationState = new SimpleObjectProperty<>();
	private final Jenjinn jenjinn;
	private final BoardState stateOfPlay;
	private final ChessBoard board;
	private final FlowList<ChessMove> movesPlayed;

	private Optional<BoardSquare> squareSelection;

	public ChessGame(Side humanSide, ColorScheme colors)
	{
		jenjinn = new Jenjinn();
		stateOfPlay = StartStateGenerator.createStartBoard();
		board = new ChessBoard(colors, stateOfPlay);
		movesPlayed = new FlowArrayList<>();
		squareSelection = Optional.empty();
		getChildren().add(board.getBoard());
		board.getBoard().setMouseClickInteractionProcedure(this::handleMouseClicks);
		board.getBoard().setInteractionEnabled();

		if (humanSide.isBlack()) {
			board.getBoard().setInteractionDisabled();
			performJenjinnMove();
		}
	}

	private void handleMouseClicks(MouseEvent evt)
	{
		final Point2D clickTarget = new Point2D(evt.getX(), evt.getY());
		final BoardSquare correspondingSquare = board.getClosestSquare(clickTarget);

		if (bitboardsIntersect(correspondingSquare.asBitboard(), getActiveLocations())) {
			squareSelection = Optionals.of(correspondingSquare);
			board.setSelectedSquare(squareSelection);
		} else if (squareSelection.isPresent()) {
			final BoardSquare src = squareSelection.get();
			final Optional<ChessMove> mv = LegalMoves.getAllMoves(stateOfPlay)
					.filter(m -> m.getSource().equals(src) && m.getTarget().equals(correspondingSquare)).safeNext();

			squareSelection = Optional.empty();
			board.setSelectedSquare(squareSelection);

			if (mv.isPresent()) {
				processHumanMove(mv.get());
			}
		}
	}

	private void processHumanMove(ChessMove mv)
	{
		board.getBoard().setInteractionDisabled();
		mv.makeMove(stateOfPlay);
		sideToMove.setValue(stateOfPlay.getActiveSide());
		movesPlayed.add(mv);
		Platform.runLater(board::redraw);

		if (!terminalStateReached()) {
			performJenjinnMove();
		}
	}

	/**
	 * If the state of play is in a terminal state this method must lock the UI and
	 * submit the type of termination to the terminationState property to signal to
	 * the outside this game is finished. Otherwise no actions need to be performed.
	 *
	 * @return true if the state of play is terminal, false otherwise.
	 */
	private boolean terminalStateReached()
	{
		final Optional<ChessMove> mv = LegalMoves.getAllMoves(stateOfPlay).safeNext();
		final GameTermination termState = TerminationState.of(stateOfPlay, mv.isPresent());
		if (termState.isTerminal()) {
			Platform.runLater(board.getBoard()::setInteractionDisabled);
			terminationState.setValue(termState);
			return true;
		}
		else {
			return false;
		}
	}

	private void performJenjinnMove()
	{
		final Thread jenjinnCalculationThread = new Thread(() -> {
			final ChessMove jenjinnChoice = Optionals.getOrError(jenjinn.calculateBestMove(stateOfPlay));
			jenjinnChoice.makeMove(stateOfPlay);
			sideToMove.setValue(stateOfPlay.getActiveSide());
			movesPlayed.add(jenjinnChoice);
			Platform.runLater(board::redraw);
			if (!terminalStateReached()) {
				Platform.runLater(board.getBoard()::setInteractionEnabled);
			}
		});
		jenjinnCalculationThread.start();

		final Thread timer = new Thread(() -> {
			try {
				Thread.sleep((long) (moveTime * 1000));
				if (jenjinnCalculationThread.isAlive()) {
					jenjinnCalculationThread.interrupt();
				}
			} catch (final InterruptedException e) {
				throw new AssertionError();
			}
		});
		timer.start();
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
