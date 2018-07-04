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
public class JenjinnApp extends Application
{
	@Override
	public void start(Stage arg0) throws Exception
	{
		Scene s = new Scene(new GameWrapper(), 500, 500);
		arg0.setScene(s);
		arg0.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
