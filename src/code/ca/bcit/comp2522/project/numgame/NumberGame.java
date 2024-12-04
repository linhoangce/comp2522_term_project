package ca.bcit.comp2522.project.numgame;

import java.util.Arrays;
import java.util.Random;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class represents the NumberGame, a game that involves placing numbers on a grid in an ascending order.
 * The player aims to place a random number on the grid until they either win by filling all grid cells or lose by
 * making an invalid move. The game tracks statistics such as games won, games lost, and successful placements.
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class NumberGame extends GameGUI
{
   private static final int ROWS          = 4;
   private static final int COLS          = 5;
   private static final int WIN_MOVES     = ROWS * COLS;
   private static final int STAGE_WIDTH   = 600;
   private static final int STAGE_HEIGHT  = 500;
   private static final int BUTTON_SIZE   = 80;
   private static final int SPACING       = 20;

   private static final int LOWER_BOUND_RAND_VAL = 1;
   private static final int UPPER_BOUND_RAND_VAL = 1001;
   private static final int DEFAULT_VALUE        = 0;

   private static final String NUM_PREFIX  = "Number: ";
   private static final String LOST_MESSAGE = "You've lost!";
   private static final String WON_MESSAGE = "CONGRATS! You Won. Press 'Quit' to return home or 'Try Again'";
   private static final String INVALID_MOVE = "Invalid!";

   private final Label label;
   private final Integer[] board;
   private int randomNumber;
   private int moves;
   private int totalGamesPlayed;
   private int gamesWon;
   private int gamesLost;
   private int totalSuccessfulPlacements;

   /**
    * Constructs a new NumberGameGUI instance and initializes game state variables.
    */
   public NumberGame()
   {
      this.label = new Label();
      this.board = new Integer[ROWS * COLS];
      this.randomNumber = generateRandomNumber();

      this.moves                     = DEFAULT_VALUE;
      this.totalGamesPlayed          = DEFAULT_VALUE;
      this.gamesWon                  = DEFAULT_VALUE;
      this.gamesLost                 = DEFAULT_VALUE;
      this.totalSuccessfulPlacements = DEFAULT_VALUE;
   }

   /**
    * Starts the game and displays the main game window.
    *
    * @param stage the primary stage for this application.
    */
   @Override
   public void startGame(final Stage stage)
   {
      resetGameState();

      final VBox root;
      final Scene scene;

      root = setupLayout();
      scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT);

      stage.setScene(scene);
      stage.show();
   }

   /**
    * Sets up the layout for the game window, including the label and the grid of buttons.
    *
    * @return the root layout container, which is a VBox containing the label and the grid.
    */
   @Override
   public VBox setupLayout()
   {
      final VBox vBox;
      final GridPane gridPane;

      vBox = new VBox(SPACING);
      gridPane = setupGridPane();

      label.setText(NUM_PREFIX  + randomNumber);
      label.setStyle("-fx-font-size: 10px;");
      label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green;");
      vBox.setAlignment(Pos.CENTER);
      vBox.getChildren().addAll(label, gridPane);

      return vBox;
   }

   /**
    * Configures the grid layout for the game, including initializing the buttons for number placement.
    *
    * @return the configured GridPane for the game grid.
    */
   @Override
   public GridPane setupGridPane()
   {
      final GridPane gridPane;

      gridPane = new GridPane();

      gridPane.setHgap(20);
      gridPane.setVgap(20);

      gridPane.setAlignment(Pos.CENTER);

      // Ensure GridPane takes up only as much space as it needs
      gridPane.setMaxWidth(Double.MAX_VALUE);
      gridPane.setMaxHeight(Double.MAX_VALUE);

      for(int row = 0; row < ROWS; row++)
      {
         for(int col = 0; col < COLS; col++)
         {
            final int buttonIndex;

            buttonIndex = row * COLS + col;

            board[buttonIndex] = null;

            final Button button;

            button = new Button("[ ]");

            button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
            button.setStyle("-fx-font-size: 18px; -fx-text-fill: green; " +
                    "-fx-background-color: pink;");

            gridPane.add(button, col, row);

            button.setOnAction(event -> handleButtonClick(button, buttonIndex));
         }
      }

      return gridPane;
   }

   /**
    * Resets the game state, clearing the grid and generating a new random number.
    */
   @Override
   public void resetGameState()
   {
      this.randomNumber = generateRandomNumber();
      this.moves = DEFAULT_VALUE;

      Arrays.fill(board, null);
   }

   /**
    * Generates a random number within the configured range.
    *
    * @return a random number between {@code LOWER_BOUND_RAND_VAL} and {@code UPPER_BOUND_RAND_VAL}.
    */
   @Override
   public int generateRandomNumber()
   {
      final Random rand;
      final int randomNum;

      rand = new Random();
      randomNum = rand.nextInt(LOWER_BOUND_RAND_VAL, UPPER_BOUND_RAND_VAL);

      return randomNum;
   }

   /**
    * Checks whether the current game state satisfies the winning condition.
    *
    * @return {@code true} if the maximum number of moves has been made, {@code false} otherwise.
    */
   @Override
   public boolean gameWon()
   {
      return moves == WIN_MOVES;
   }

   /**
    * Ends the game by closing the application window.
    */
   @Override
   public void endGame()
   {
      final Stage stage;

      stage = (Stage) label.getScene().getWindow();

      stage.close();
   }

   /*
    * Updates the score when the user loses a round. This increments the total number of games played,
    * and the number of games lost, and adds to the total number of successful placements.
    */
   private void updateScoreWhenLost()
   {
      totalGamesPlayed++;
      gamesLost++;
      totalSuccessfulPlacements += moves;
   }

   /*
    * Updates the score when the user wins a round. This increments the total number of games played,
    * and the number of games won, and adds to the total number of successful placements.
    */
   private void updateScoreWhenWon()
   {
      totalGamesPlayed++;
      gamesWon++;
      totalSuccessfulPlacements += moves;
   }

   /*
    * Generates a detailed score summary, including games won, lost, and the average number of placements.
    *
    * @return a formatted string containing the statistics of the game performance.
    */
   private String generateScoreStats()
   {
      final double averagePlacements;

      averagePlacements = (double) totalSuccessfulPlacements / totalGamesPlayed;

      return String.format(
              "You won %d out of %d games and lost %d out of %d games, " +
                      "with %d successful placements, an average of %.2f per game.",
              gamesWon, totalGamesPlayed,
              gamesLost, totalGamesPlayed,
              totalSuccessfulPlacements,
              averagePlacements
      );
   }

   /*
    * Displays an alert with options to retry the game or quit.
    * Updates the score based on the game's outcome.
    *
    * @param message the message to display in the alert dialog.
    */
   private void displayOptionsOnGameOver(final String message)
   {
      final Alert alert;
      final ButtonType tryAgainButton;
      final ButtonType quitButton;
      final Label messageLabel;

      alert = new Alert(Alert.AlertType.INFORMATION);
      tryAgainButton = new ButtonType("Try Again");
      quitButton = new ButtonType("Quit");
      messageLabel = new Label(message);

      alert.setTitle("Game Over");
      alert.setHeaderText("Game Over! Thanks for playing.");
      alert.setContentText(message);
      alert.getButtonTypes().setAll(tryAgainButton, quitButton);
      messageLabel.setStyle("-fx-font-size: 18px;");
      messageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
      messageLabel.setAlignment(Pos.CENTER);

      alert.showAndWait().ifPresent(res ->
      {
         if(res == tryAgainButton)
         {
            final Stage stage;

            stage = (Stage) label.getScene().getWindow();
            startGame(stage);
         }
         else if(res == quitButton)
         {
            displayFinalScoreOnGameEnd();
         }
      });
   }

   /*
    * Displays the final score and ends the game when the user chooses to quit.
    */
   private void displayFinalScoreOnGameEnd()
   {
      final Alert scoreAlert;

      scoreAlert = new Alert(Alert.AlertType.INFORMATION);

      scoreAlert.setTitle("Game Statistics");
      scoreAlert.setHeaderText("Final Game Score");
      scoreAlert.setContentText(generateScoreStats());
      scoreAlert.showAndWait();

      endGame();
   }

   /*
    * Handles a button click event during the game, updating the grid and game state accordingly.
    *
    * @param button the button that was clicked.
    * @param buttonIndex the index of the button within the grid.
    */
   private void handleButtonClick(final Button button,
                                  final int    buttonIndex)
   {
      if(board[buttonIndex] == null)
      {
         button.setText(String.valueOf(randomNumber));
         board[buttonIndex] = randomNumber;

         randomNumber = generateRandomNumber();
         label.setText(NUM_PREFIX + randomNumber);

         moves++;
         button.setStyle("-fx-background-color: green;");
         button.setStyle("-fx-background-color: green; -fx-text-fill: white;");

         if(!validatePlacement())
         {
            updateScoreWhenLost();
            displayOptionsOnGameOver(LOST_MESSAGE);
            button.setDisable(true);
         }

         if(gameWon())
         {
            updateScoreWhenWon();
            displayOptionsOnGameOver(WON_MESSAGE);
         }
      }
      else
      {
         System.out.println(INVALID_MOVE);
      }
   }

   /**
    * Validates whether the current number placement is correct.
    *
    * @return true if the current placement is valid, false otherwise.
    */
   private boolean validatePlacement()
   {
      int prevPlacement;
      int currentPlacement;

      prevPlacement = 0;

      for(final Integer number : board)
      {
         if(number != null)
         {
            currentPlacement = number;

            if (prevPlacement > currentPlacement)
            {
               return false;
            }
            else
            {
               prevPlacement = currentPlacement;
            }
         }
      }
      return true;
   }
}
