/**
 * 
 */
package jenjinn.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author ThomasB
 *
 */
public class TestApp extends Application
{
	@Override
	public void start(Stage arg0) throws Exception
	{
		Scene s = new Scene(new GameWrapper(), 300, 300);
		arg0.setScene(s);
		arg0.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
