/**
 * 
 */
package jenjinn.fx.utils;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 */
public final class GameWrapper extends Region
{
	private static final String CSS_STLYE = "-fx-background-color: #92db95;";
	private static final int MIN_WIDTH = 90, MIN_HEIGHT = 100;

	private Label gameInfoLabel, chooseYourSide;
	private Button chooseWhite, chooseBlack;
	private Optional<ChessGame> chessGame = Optional.empty();

	public GameWrapper()
	{
		setStyle(CSS_STLYE);
		setMinSize(MIN_WIDTH, MIN_HEIGHT);
		setPadding(new Insets(5));

		gameInfoLabel = new Label("Waiting for game start.");
		gameInfoLabel.setAlignment(Pos.CENTER_LEFT);
		gameInfoLabel.setFont(Font.font(12));
		gameInfoLabel.setPadding(new Insets(2));

		chooseYourSide = new Label("Choose your side");
		chooseYourSide.setAlignment(Pos.CENTER);
		chooseYourSide.setFont(Font.font(14));
		chooseYourSide.setPadding(new Insets(2));

		chooseWhite = new Button("White");
		chooseWhite.setFont(Font.font(14));
		chooseWhite.setPadding(new Insets(5));

		chooseBlack = new Button("Black");
		chooseBlack.setFont(Font.font(14));
		chooseBlack.setPadding(new Insets(5));

		getChildren().addAll(gameInfoLabel, chooseYourSide, chooseWhite, chooseBlack);

		chooseWhite.setOnAction(evt -> initGame(Side.WHITE));
		chooseWhite.setOnAction(evt -> initGame(Side.BLACK));
	}
	
	private void initGame(Side humanSide)
	{
		ChessGame newGame = new ChessGame(humanSide, ColorScheme.getDefault());
		getChildren().add(newGame.getFxComponent());
		gameInfoLabel.setText(GameStageMessages.WHITE_TO_MOVE);
		chessGame = Optional.of(newGame);
		hideSideSelectors();
		addPropertyListeners(newGame);
	}

	private void hideSideSelectors()
	{
		chooseYourSide.setVisible(false);
		chooseWhite.setVisible(false);
		chooseBlack.setVisible(false);
	}

	private void addPropertyListeners(ChessGame game)
	{
		game.getSideToMoveProperty().addListener((x, oldSide, newSide) -> {
			Platform.runLater(() -> {
				String message = newSide.isWhite() ? GameStageMessages.WHITE_TO_MOVE : GameStageMessages.BLACK_TO_MOVE;
				gameInfoLabel.setText(message);
			});
		});
		
		game.getTerminationStateProperty().addListener((x, y, termState) -> {
			Platform.runLater(() -> {
				switch (termState) {
				case DRAW:
					gameInfoLabel.setText(GameStageMessages.DRAW);
					break;
				case WHITE_WIN:
					gameInfoLabel.setText(GameStageMessages.WHITE_WIN);
					break;
				case BLACK_WIN:
					gameInfoLabel.setText(GameStageMessages.BLACK_WIN);
					break;
				default:
					gameInfoLabel.setText("Text error");
				}
			});
		});
	}

	protected void layoutChildren()
	{
		getChildren().stream().forEach(x -> x.autosize());
		Insets pad = getPadding();
		double w = getWidth(), h = getHeight();
		gameInfoLabel.relocate(pad.getLeft(), pad.getRight());
		chooseYourSide.relocate((w - chooseYourSide.getWidth()) / 2, h / 3);
		double buttonY = chooseYourSide.getLayoutY() + chooseYourSide.getHeight() + 5;
		chooseWhite.relocate(w / 2 - 5 - chooseWhite.getWidth(), buttonY);
		chooseBlack.relocate(w / 2 + 5, buttonY);

		if (chessGame.isPresent()) {
			double gameY = gameInfoLabel.getLayoutBounds().getMaxY() + 5;
			double gameX = pad.getLeft();
			double gameWidth = w - pad.getLeft() - pad.getRight();
			double gameHeight = h - pad.getTop() - gameY;
			chessGame.get().getFxComponent().resizeRelocate(gameX, gameY, gameWidth, gameHeight);
		}
	}
}
