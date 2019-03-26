/**
 *
 */
package jenjinn.fx;

import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.GameTermination;
import jenjinn.engine.base.Side;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.StartStateGenerator;
import jenjinn.engine.boardstate.calculators.LegalMoves;
import jenjinn.engine.boardstate.calculators.TerminationState;
import jenjinn.engine.entity.Jenjinn;
import jenjinn.engine.moves.ChessMove;
import xawd.jflow.collections.FList;
import xawd.jflow.collections.impl.FlowArrayList;
import xawd.jflow.utilities.Optionals;

/**
 * @author ThomasB
 */
public final class ChessGame
{
	static final double MIN_MOVETIME = 500, MAX_MOVETIME = 10000;
	private long moveTime = 5000;

	private final Property<Side> sideToMove = new SimpleObjectProperty<>(Side.WHITE);
	private final Property<GameTermination> terminationState = new SimpleObjectProperty<>(GameTermination.NOT_TERMINAL);
	private final Jenjinn jenjinn;
	private final BoardState stateOfPlay;
	private final ChessBoard board;
	private final FList<ChessMove> movesPlayed;

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
			board.setPerspective(Side.BLACK);
			board.getFxComponent().setInteractionDisabled();
			performJenjinnMove();
		}
	}

	private void handleMouseClicks(MouseEvent evt)
	{
		if (evt.getButton() == MouseButton.SECONDARY) {
			board.switchPerspective();
		}
		else if (evt.getButton() == MouseButton.PRIMARY) {
			Point2D clickTarget = new Point2D(evt.getX(), evt.getY());
			BoardSquare correspondingSquare = board.getClosestSquare(clickTarget);

			if (bitboardsIntersect(correspondingSquare.asBitboard(), getActiveLocations())) {
				setSelection(Optionals.of(correspondingSquare));
			} else if (squareSelection.isPresent()) {
				BoardSquare src = squareSelection.get();
				Optional<ChessMove> mv = LegalMoves.getAllMoves(stateOfPlay)
						.filter(m -> m.getSource().equals(src) && m.getTarget().equals(correspondingSquare)).safeNext();

				setSelection(Optional.empty());
				if (mv.isPresent()) {
					processHumanMove(mv.get());
				}
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
		Optional<ChessMove> mv = LegalMoves.getAllMoves(stateOfPlay).safeNext();
		GameTermination termState = TerminationState.of(stateOfPlay, mv.isPresent());
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
			BoardState cpy = stateOfPlay.copy();
			ChessMove jenjinnChoice = jenjinn.calculateBestMove(cpy, moveTime).get();
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

	public Property<Side> getSideToMoveProperty()
	{
		return sideToMove;
	}

	public Property<GameTermination> getTerminationStateProperty()
	{
		return terminationState;
	}

	public void forceRedraw()
	{
		Platform.runLater(board::redraw);
	}

	void interpolateMoveTime(double fraction)
	{
		double interpolated = (1 - fraction) * MIN_MOVETIME + fraction * MAX_MOVETIME;
		moveTime = (long) Math.min(MAX_MOVETIME, Math.max(MIN_MOVETIME, interpolated));
	}

	void setMoveTime(double moveTimeInSeconds)
	{
		moveTime = (long) (1000 * moveTimeInSeconds);
	}
}
