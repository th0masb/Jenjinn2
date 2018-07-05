/**
 *
 */
package jenjinn.fx;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import jenjinn.engine.base.Side;
import jenjinn.engine.pieces.ChessPiece;
import xawd.jflow.utilities.Optionals;

/**
 * @author ThomasB
 */
public final class GameWrapper extends Region
{
	private static final String CSS_STYLE = "-fx-background-color: #aeb5c1;";
	private static final int MIN_WIDTH = 20, MIN_HEIGHT = 20;

	private final Label gameInfoLabel, chooseYourSide, timeLabel;
	private final Button chooseWhite, chooseBlack, playAgain;
	private final Slider timeSlider;
	private Optional<ChessGame> chessGame = Optional.empty();

	public GameWrapper()
	{
		setStyle(CSS_STYLE);
		setMinSize(MIN_WIDTH, MIN_HEIGHT);
		setPadding(new Insets(5));
		setSnapToPixel(true);

		timeSlider = createSlider();

		gameInfoLabel = new Label(GameStageMessages.WAITING_FOR_GAME_START);
		gameInfoLabel.setAlignment(Pos.CENTER_LEFT);
		gameInfoLabel.setFont(Font.font(16));
		gameInfoLabel.setPadding(new Insets(2));

		chooseYourSide = new Label("Choose your side");
		chooseYourSide.setAlignment(Pos.CENTER);
		chooseYourSide.setFont(Font.font(16));
		chooseYourSide.setPadding(new Insets(2));

		timeLabel = new Label(formatTime(timeSlider.valueProperty().get()));
		timeLabel.setAlignment(Pos.CENTER_LEFT);
		timeLabel.setFont(Font.font(16));
		timeLabel.setPadding(new Insets(2));
		timeLabel.setVisible(false);

		chooseWhite = createSideSelectionButton(Side.WHITE);
		chooseBlack = createSideSelectionButton(Side.BLACK);

		playAgain = new Button("Play again");
		playAgain.setAlignment(Pos.CENTER);
		playAgain.setFont(Font.font(12));
		playAgain.setPadding(new Insets(2, 5, 2, 5));
		playAgain.setVisible(false);
		playAgain.setOnAction(evt -> reset());

		getChildren().addAll(gameInfoLabel, chooseYourSide, chooseWhite, chooseBlack, playAgain, timeSlider, timeLabel);
	}

	private String formatTime(double time)
	{
		final long ms = (long) (1000 * time);
		return Long.toString(ms) + " ms";
	}

	private Slider createSlider()
	{
		final double minTime = ChessGame.MIN_MOVETIME / 1000.0;
		final double maxTime = ChessGame.MAX_MOVETIME / 1000.0;
		final double start = (minTime + maxTime) / 2;
		final Slider slider = new Slider(minTime, maxTime, start);
		slider.valueProperty()
		.addListener((x, y, newVal) -> {
			chessGame.ifPresent(z -> z.setMoveTime(newVal.doubleValue()));
			timeLabel.setText(formatTime(newVal.doubleValue()));
			layoutChildren();
		});
		slider.setVisible(false);
		return slider;
	}

	private Button createSideSelectionButton(Side side)
	{
		final ChessPiece toDisplay = side.isWhite() ? ChessPiece.WHITE_QUEEN : ChessPiece.BLACK_QUEEN;
		final Button button = new Button();
		button.setStyle(CSS_STYLE);
		button.setGraphic(new ImageView(ImageCache.INSTANCE.getImageOf(toDisplay)));
		button.setAlignment(Pos.CENTER);
		button.setFont(Font.font(14));
		button.setPadding(new Insets(5));
		button.setOnAction(evt -> initGame(side));
		return button;
	}

	private void reset()
	{
		final ChessGame toRemove = Optionals.getOrError(chessGame);
		chessGame = Optional.empty();
		getChildren().remove(toRemove.getFxComponent());
		gameInfoLabel.setText(GameStageMessages.WAITING_FOR_GAME_START);
		setSideSelectorVisibility(true);
		playAgain.setVisible(false);
	}

	private void initGame(Side humanSide)
	{
		final ChessGame newGame = new ChessGame(humanSide, ColorScheme.getDefault());
		getChildren().add(newGame.getFxComponent());
		gameInfoLabel.setText(GameStageMessages.WHITE_TO_MOVE);
		chessGame = Optional.of(newGame);
		setSideSelectorVisibility(false);
		timeSlider.setVisible(true);
		timeLabel.setVisible(true);
		addPropertyListeners(newGame);
		Platform.runLater(this::layoutChildren);
	}

	private void setSideSelectorVisibility(boolean visible)
	{
		chooseYourSide.setVisible(visible);
		chooseWhite.setVisible(visible);
		chooseBlack.setVisible(visible);
	}

	private void addPropertyListeners(ChessGame game)
	{
		game.getSideToMoveProperty().addListener((x, oldSide, newSide) -> {
			Platform.runLater(() -> {
				final String message = newSide.isWhite() ? GameStageMessages.WHITE_TO_MOVE
						: GameStageMessages.BLACK_TO_MOVE;
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
				playAgain.setVisible(true);
			});
		});
	}

	@Override
	protected void layoutChildren()
	{
		getChildren().stream().forEach(x -> x.autosize());
		final Insets pad = getPadding();
		final double lpad = pad.getLeft(), tpad = pad.getTop(), rpad = pad.getRight(), bpad = pad.getBottom();
		final double w = getWidth(), h = getHeight();
		gameInfoLabel.relocate(lpad, tpad);
		playAgain.relocate(w - rpad - playAgain.getWidth(), tpad);
		chooseYourSide.relocate((w - chooseYourSide.getWidth()) / 2, h / 3);
		final double buttonY = chooseYourSide.getLayoutY() + chooseYourSide.getHeight() + 5;
		chooseWhite.relocate(w / 2 - 5 - chooseWhite.getWidth(), buttonY);
		chooseBlack.relocate(w / 2 + 5, buttonY);

		if (chessGame.isPresent()) {
			final double gameWidth = snapSize(w - lpad - rpad);
			final double y1 = gameInfoLabel.getLayoutBounds().getMaxY();
			final double y2 = playAgain.getLayoutBounds().getMaxY();
			final double gameY = snapSize(Math.max(y1, y2) + 5);
			final double gameX = snapSize(lpad);

			final double timeLabelWidth = timeLabel.getWidth(), timeLabelHeight = timeLabel.getHeight();
			final double sliderWidth = Math.max(30, gameWidth - timeLabelWidth - 5);
			final double sliderHeight = Math.max(timeLabel.getHeight(), 0.1*h);

			timeSlider.resize(sliderWidth, sliderHeight);
			timeSlider.relocate(lpad, h - bpad - sliderHeight);
			timeLabel.relocate(lpad + timeSlider.getWidth() + 5, h - bpad - 0.5 * (sliderHeight + timeLabelHeight));

			final double gameHeight = snapSize(h - tpad - gameY - timeSlider.getHeight() - 5);
			chessGame.get().getFxComponent().resizeRelocate(gameX, gameY, gameWidth, gameHeight);
		}
	}

	public void interpolateTimeLimit(double fraction)
	{
		if (chessGame.isPresent()) {
			chessGame.get().interpolateMoveTime(fraction);
		}
	}
}
