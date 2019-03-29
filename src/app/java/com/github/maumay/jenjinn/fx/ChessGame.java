/**
 *
 */
package com.github.maumay.jenjinn.fx;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.maumay.jenjinn.base.GameTermination;
import com.github.maumay.jenjinn.base.Side;
import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jenjinn.bitboards.Bitboard;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.boardstate.StartStateGenerator;
import com.github.maumay.jenjinn.boardstate.calculators.LegalMoves;
import com.github.maumay.jenjinn.boardstate.calculators.TerminationState;
import com.github.maumay.jenjinn.entity.Jenjinn;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jflow.utils.Option;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * @author ThomasB
 */
public final class ChessGame
{
	static final double MIN_MOVETIME = 500, MAX_MOVETIME = 10000;
	private long moveTime = 5000;

	private final Property<Side> sideToMove = new SimpleObjectProperty<>(Side.WHITE);
	private final Property<GameTermination> terminationState = new SimpleObjectProperty<>(
			GameTermination.NOT_TERMINAL);
	private final Jenjinn jenjinn;
	private final BoardState stateOfPlay;
	private final ChessBoard board;
	private final List<ChessMove> movesPlayed;

	private Optional<Square> squareSelection;

	public ChessGame(Side humanSide, ColorScheme colors)
	{
		jenjinn = new Jenjinn();
		stateOfPlay = StartStateGenerator.createStartBoard();
		board = new ChessBoard(colors, stateOfPlay);
		movesPlayed = new ArrayList<>();
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
		} else if (evt.getButton() == MouseButton.PRIMARY) {
			Point2D clickTarget = new Point2D(evt.getX(), evt.getY());
			Square correspondingSquare = board.getClosestSquare(clickTarget);

			if (Bitboard.intersects(correspondingSquare.bitboard, getActiveLocations())) {
				setSelection(Option.of(correspondingSquare));
			} else if (squareSelection.isPresent()) {
				Square src = squareSelection.get();
				Optional<ChessMove> mv = LegalMoves.getAllMoves(stateOfPlay)
						.filter(m -> m.getSource().equals(src)
								&& m.getTarget().equals(correspondingSquare))
						.nextOp();

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

	private void setSelection(Optional<Square> selection)
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
		Optional<ChessMove> mv = LegalMoves.getAllMoves(stateOfPlay).nextOp();
		GameTermination termState = TerminationState.of(stateOfPlay, mv.isPresent());
		if (termState.isTerminal()) {
			Platform.runLater(board.getFxComponent()::setInteractionDisabled);
			terminationState.setValue(termState);
			return true;
		} else {
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
		return stateOfPlay.getPieceLocations()
				.getSideLocations(stateOfPlay.getActiveSide());
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
