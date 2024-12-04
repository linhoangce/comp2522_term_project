package ca.bcit.comp2522.project.mygame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * The MemoryGameApp class is responsible for initializing and launching the Memory Game application.
 * It sets up the game environment, including the game engine, user interface, and the game scene.
 * This class extends {@link javafx.application.Application} and serves as the entry point for the JavaFX application.
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class MemoryGameApp extends Application
{

   /**
    * The entry point for starting the JavaFX application.
    * Initializes the MemoryGameEngine, creates the graphical user interface (GUI), and sets up the primary stage.
    *
    * @param primaryStage the primary stage for the application, to be set with the scene and show the game window
    */

   @Override
   public void start(Stage primaryStage)
   {
      final MemoryGameEngine engine;
      final GameLevel gameLevel;
      final MemoryGameGUI gameGUI;
      final VBox layout;
      final Scene scene;
      final int boardSize;
      final double screenWidth;
      final double screenHeight;
      final double stageWidth;
      final double stageHeight;

      gameLevel = new MemoryPatternGame();
      engine = MemoryGameEngine.getInstance(gameLevel);
      gameGUI = new MemoryGameGUI(engine, primaryStage);
      boardSize = engine.getBoardSize();
      layout = gameGUI.createUI(boardSize);
      screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
      screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
      stageWidth = primaryStage.getWidth(

      );
      stageHeight = primaryStage.getHeight();

      scene = new Scene(layout, screenWidth - 100, screenHeight - 50);
      scene.setFill(Color.DARKGRAY);

      primaryStage.setTitle("Memory Pattern Game");
      primaryStage.setScene(scene);
      primaryStage.show();

      gameGUI.showInstructionsPopup(primaryStage);

   }

   /**
    * The main method that launches the JavaFX application.
    * This method serves as the starting point for the JavaFX application.
    *
    * @param args the command line arguments (not used in this case)
    */
   public static void main(String[] args)
   {
      launch(args);
   }
}
