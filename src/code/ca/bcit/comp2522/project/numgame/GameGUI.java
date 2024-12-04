package ca.bcit.comp2522.project.numgame;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * The GameGUI class provides the abstract base for defining the graphical user interface
 * (GUI) elements and layout for a number-based game. This class implements the NumberGameRule
 * interface and provides abstract methods for setting up the layout and game board. It also
 * includes default functionality for determining if the game is over.
 *
 * @author Linh Hoang
 * @version 1.0
 */
public abstract class GameGUI implements NumberGameRule
{

   /**
    * Sets up the initial layout of the game.
    * This method is meant to be implemented by subclasses to define the
    * overall layout and design of the game.
    *
    * @return VBox containing the game's main layout
    */
   abstract VBox setupLayout();

   /**
    * Creates and configures the grid pane for the game.
    * This method is intended to be implemented by subclasses to create and configure
    * the grid that represents the game board.
    *
    * @return GridPane representing the game board
    */
   abstract GridPane setupGridPane();

   /**
    * Generates a random number for the game.
    * Subclasses can provide their own implementation for random number generation
    * based on the game's rules.
    *
    * @return A randomly generated number
    */
   public abstract int generateRandomNumber();

   /**
    * Provides default implementation for checking if the game is over.
    * Subclasses can override this method to provide specific conditions for
    * determining whether the game has been won or not.
    *
    * @return true if the game is over, false otherwise
    */
   public boolean gameWon()
   {
      return false;
   }
}