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
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
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
public final class ChessGame
{
//	private static final double MIN_MOVETIME = 2.0, MAX_MOVETIME = 10;
	private final long moveTime = 5000;

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
		board.getFxComponent().setMouseClickInteractionProcedure(this::handleMouseClicks);
		board.getFxComponent().setInteractionEnabled();

		if (humanSide.isBlack()) {
			board.getFxComponent().setInteractionDisabled();
			performJenjinnMove();
		}
	}

	private void handleMouseClicks(MouseEvent evt)
	{
		final Point2D clickTarget = new Point2D(evt.getX(), evt.getY());
		final BoardSquare correspondingSquare = board.getClosestSquare(clickTarget);

		if (bitboardsIntersect(correspondingSquare.asBitboard(), getActiveLocations())) {
			setSelection(Optionals.of(correspondingSquare));
		} else if (squareSelection.isPresent()) {
			final BoardSquare src = squareSelection.get();
			final Optional<ChessMove> mv = LegalMoves.getAllMoves(stateOfPlay)
					.filter(m -> m.getSource().equals(src) && m.getTarget().equals(correspondingSquare)).safeNext();

			setSelection(Optional.empty());
			if (mv.isPresent()) {
				processHumanMove(mv.get());
			}
		}
	}

	private void processHumanMove(ChessMove mv)
	{
		board.getFxComponent().setInteractionDisabled();
		mv.makeMove(stateOfPlay);
		sideToMove.setValue(stateOfPlay.getActiveSide());
		movesPlayed.add(mv);
		Platform.runLater(board::redraw);

		if (!terminalStateReached()) {
			performJenjinnMove();
		}
	}
	
	private void setSelection(Optional<BoardSquare> selection)
	{
		squareSelection = selection;
		board.setSelectedSquare(selection);
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
			Platform.runLater(board.getFxComponent()::setInteractionDisabled);
			terminationState.setValue(termState);
			return true;
		}
		else {
			return false;
		}
	}

	private void performJenjinnMove()
	{
		new Thread(() -> {
			final ChessMove jenjinnChoice = Optionals.getOrError(jenjinn.calculateBestMove(stateOfPlay, moveTime));
			jenjinnChoice.makeMove(stateOfPlay);
			sideToMove.setValue(stateOfPlay.getActiveSide());
			movesPlayed.add(jenjinnChoice);
			Platform.runLater(board::redraw);
			if (!terminalStateReached()) {
				Platform.runLater(board.getFxComponent()::setInteractionEnabled);
			}
		}).start();
	}

	private long getActiveLocations()
	{
		return stateOfPlay.getPieceLocations().getSideLocations(stateOfPlay.getActiveSide());
	}

	public Node getFxComponent()
	{
		return board.getFxComponent();
	}
}
