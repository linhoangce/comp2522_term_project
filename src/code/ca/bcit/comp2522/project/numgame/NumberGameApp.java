package ca.bcit.comp2522.project.numgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * The NumberGameApp class launches and manages the lifecycle of the Number Game application.
 * It extends the JavaFX Application class and provides methods to start the game on the JavaFX application thread,
 * as well as handle threading for the application launch.
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class NumberGameApp extends Application
{
   /**
    * Initializes and starts the Number Game by creating an instance of the NumberGame class
    * and setting up the game window (stage).
    *
    * @param stage The primary stage for the game window.
    */
   @Override
   public void start(final Stage stage)
   {
      final NumberGame numGame;
      numGame = new NumberGame();

      numGame.startGame(stage);
      stage.setTitle("Number Game");
   }

   /**
    * Launches the Number Game application. If the method is called from a non-JavaFX thread,
    * it will create a new thread to launch the application on the JavaFX application thread.
    * Otherwise, it starts the game directly in the current thread.
    */
   public static void playNumberGame()
   {
      if(!Platform.isFxApplicationThread())
      {
         new Thread(() -> Application.launch(NumberGameApp.class)).start();
      }
      else
      {
         final NumberGame numGame;
         final Stage newStage;

         numGame  = new NumberGame();
         newStage = new Stage();

         numGame.startGame(newStage);
         newStage.setTitle("Number Game");
      }
   }
}
