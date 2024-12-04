package ca.bcit.comp2522.project.numgame;

import javafx.stage.Stage;

/**
 * The NumberGameRule interface defines the essential methods that must be implemented
 * to handle the core rules and operations of a number game. This includes starting
 * the game, resetting the game state, and ending the game.
 *
 * @author Linh Hoang
 * @version 1.0
 */
interface NumberGameRule
{

   /**
    * Starts the game by initializing necessary components and displaying the game window.
    *
    * @param stage the primary stage for the application where the game interface is shown.
    */
   void startGame(final Stage stage);

   /**
    * Resets the game state to its initial condition, clearing any data and preparing for a new round.
    */
   void resetGameState();

   /**
    * Ends the game by performing any necessary cleanup and closing the game window.
    */
   void endGame();
}
