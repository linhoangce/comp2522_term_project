package ca.bcit.comp2522.project.mygame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * The {@code MemoryGameGUI} class represents the graphical user interface (GUI) for a memory pattern game.
 * It allows the user to interact with the game, view generated patterns, and select squares to submit their answer.
 *
 * <p>
 * This class is responsible for rendering the grid, handling user selections, and submitting the user's choices.
 * It also manages showing the pattern to the user and provides feedback on the correctness of the submission.
 * </p>
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class MemoryGameGUI 
{
   private static final int DEFAULT_OPACITY = 1;
   private static final double DISABLED_OPACITY = 0.5;
   private static final int TILE_SIZE = 20;
   private static final Color DEFAULT_COLOR = Color.LIGHTGRAY;
   private static final Color PATTERN_COLOR = Color.RED;
   private static final Color SELECTED_COLOR = Color.ORANGE;
   private static final Color SUCCESS_COLOR = Color.GREEN;
   private static final int SPACING = 20;
   private final List<Square> userSelection;

   private final Stage primaryStage;
   private final MemoryGameEngine engine;
   private final Button submitButton;
   private GridPane gridPane;
   private final Label scoreDisplay;
   private final Label winStreak;
   private final Set<Square> correctlySelectedSquares;
   private final Button endGameButton;
   private final Button resetGameButton;
   private final VBox layout;


   /**
    * Constructs a new {@code MemoryGameGUI} with the specified game engine.
    *
    * @param engine the {@link MemoryGameEngine} to be used for the game logic
    * @param primaryStage the stage
    * @throws IllegalArgumentException if the {@code engine} is {@code null}
    */
   public MemoryGameGUI(final MemoryGameEngine engine, final Stage primaryStage)
   {
      validateGameEngine(engine);

      this.primaryStage = primaryStage;
      this.engine  = engine;
      submitButton = new Button("Submit");
      gridPane     = createGrid(engine.getBoardSize());
      scoreDisplay = new Label("Your current score: ");
      winStreak    = new Label("Win streak: ");
      correctlySelectedSquares = new HashSet<>();
      userSelection = new ArrayList<>();
      endGameButton = new Button("End Game!");
      resetGameButton = new Button("Reset Game");
      layout = new VBox(SPACING);
   }

   /**
    * Creates the user interface (UI) layout for the memory game.
    *
    * @param boardSize the size of the grid to be displayed
    * @return a {@link VBox} containing the grid and submit button for the game UI
    */
   public VBox createUI(final int boardSize)
   {
      final VBox scoreLayout;
      final HBox recordLayout;
      final HBox buttonGroup;
      final int spacing;

      spacing = 50;

      scoreLayout = new VBox(scoreDisplay, winStreak);
      recordLayout = new HBox(spacing, scoreLayout, winStreak);
      buttonGroup = new HBox(spacing, resetGameButton, submitButton, endGameButton);

      submitButton.setDefaultButton(true);
      scoreDisplay.setVisible(false);
      winStreak.setVisible(false);
      submitButton.setVisible(false);
      endGameButton.setVisible(false);
      resetGameButton.setVisible(false);

      gridPane = createGrid(boardSize);

      scoreLayout.setAlignment(Pos.CENTER);
      recordLayout.setAlignment(Pos.CENTER);
      buttonGroup.setAlignment(Pos.CENTER);
      gridPane.setAlignment(Pos.CENTER);

      // Ensure GridPane takes up only as much space as it needs
      gridPane.setMaxWidth(Double.MAX_VALUE);
      gridPane.setMaxHeight(Double.MAX_VALUE);

      scoreDisplay.setStyle("-fx-font-size: 16px; -fx-text-fill: green; -fx-font-weight: bold;");
      winStreak.setStyle("-fx-font-size: 16px; -fx-text-fill: blue; -fx-font-weight: bold;");

      submitButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
              "-fx-background-color: green; -fx-text-fill: white;");
      resetGameButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
              "-fx-background-color: green; -fx-text-fill: white;");
      endGameButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
              "-fx-background-color: green; -fx-text-fill: white;");

      scoreDisplay.textProperty().bind(Bindings.createStringBinding(
              () -> "Your current score: " + engine.getScore(),
              engine.scoreProperty()));

      engine.setWinStreakListener(this::showWinStreakPopup);

      endGameButton.setOnAction(event -> handleEndGame());
      resetGameButton.setOnAction(actionEvent -> handleResetGame());

      // Set submitButton as the default and ensure it retains focus
      submitButton.setOnAction(event ->
      {
         handleSubmit();
         submitButton.setDefaultButton(true);
         submitButton.requestFocus(); // Ensure focus stays on submitButton
      });

      submitButton.visibleProperty().addListener((observable, oldValue, newValue) ->
      {
         if(newValue)
         {
            submitButton.setDefaultButton(true);
            submitButton.requestFocus(); // Force focus on visibility change
         }
      });

      resetGameButton.setOnAction(actionEvent ->
      {
         handleResetGame();
         resetGameButton.setDefaultButton(false); // Reset shouldn't claim default
      });

      // Ensure no other button steals the default behavior
      resetGameButton.visibleProperty().addListener((observable, oldValue, newValue) ->
      {
         if(newValue && submitButton.isVisible())
         {
            resetGameButton.setDefaultButton(false);
         }
      });

      endGameButton.visibleProperty().addListener((observable, oldValue, newValue) ->
      {
         if(newValue && submitButton.isVisible())
         {
            endGameButton.setDefaultButton(false);
         }
      });

      VBox.setVgrow(gridPane, Priority.ALWAYS);
      layout.setPadding(new Insets(15));
      layout.getChildren().addAll(recordLayout, gridPane, buttonGroup);
      layout.setAlignment(Pos.CENTER);

      return layout;
   }

   /*
    * Creates the grid for the game board.
    *
    * @param boardSize the size of the grid (number of rows and columns)
    * @return a {@link GridPane} representing the grid for the game board
    */
   private GridPane createGrid(final int boardSize)
   {
      final GridPane gridPane;

      gridPane = new GridPane();

      for(int x = 0; x < boardSize; x++)
      {
         for(int y = 0; y < boardSize; y++)
         {
            final Rectangle tile;

            tile = new Rectangle(TILE_SIZE, TILE_SIZE, DEFAULT_COLOR);
            tile.setStroke(Color.BLACK);
            gridPane.add(tile, y, x);

            // Add user click behavior
            final int finalX;
            final int finalY;

            finalX = x;
            finalY = y;

            submitButton.setDisable(true);

            tile.setOnMouseClicked(event -> handleUserSelection(finalX, finalY, tile));
         }
      }

      return gridPane;
   }

   /*
    * Displays the generated pattern on the game board by highlighting the squares sequentially.
    * The pattern will be shown for a brief moment before the squares return to their default color.
    */
   private void showPattern()
   {
      final List<Square> pattern;
      final double baseDelay;  // Constant delay for all squares
      final double extraSecondDelay; // Extra time before hiding each square
      final List<PauseTransition> transitions;

      pattern = engine.getGeneratedPattern();
      baseDelay = 0.2;
      extraSecondDelay = 0.2;
      transitions = new ArrayList<>();

      engine.nextRound();
      submitButton.setDisable(true);

      disableAllTiles();

      for(final Square square : pattern)
      {
         final Rectangle tile;

         tile = getTile(square.getX(), square.getY());

         if(tile != null)
         {
            // Set a consistent delay for all squares (no multiplication)
            final PauseTransition showTile;
            final PauseTransition hideTile;

            showTile = new PauseTransition(Duration.seconds(baseDelay));
            hideTile = new PauseTransition(Duration.seconds(baseDelay + extraSecondDelay));

            tile.setOpacity(DEFAULT_OPACITY);
            showTile.setOnFinished(event -> tile.setFill(PATTERN_COLOR));
            hideTile.setOnFinished(event -> tile.setFill(DEFAULT_COLOR));

            transitions.add(showTile);
            transitions.add(hideTile);
         }
      }

      // Create a SequentialTransition for smooth animation
      SequentialTransition sequence = new SequentialTransition();
      sequence.getChildren().addAll(transitions);

      // Re-enable the submit button after the pattern is shown
      sequence.setOnFinished(event ->
      {
         enableAllTilesAfterPatternGenerated();
         updateSubmitButtonState();
      });

      sequence.play();
   }

   /*
    * Handles the user's selection of a square on the grid.
    *
    * @param x the row index of the selected square
    * @param y the column index of the selected square
    * @param tile the {@link Rectangle} representing the selected square on the grid
    */
   private void handleUserSelection(final int x, final int y, Rectangle tile)
   {
      final Square clickedSquare;

      clickedSquare = new Square(x, y);

      if(!userSelection.contains(clickedSquare))
      {
         userSelection.add(clickedSquare);
         tile.setFill(SELECTED_COLOR);
      } else      // Unselect the selected square
      {
         userSelection.remove(clickedSquare);
         tile.setFill(DEFAULT_COLOR);
      }

      // Disable the submit button if no square is selected
      updateSubmitButtonState();
      resetGameButton.setDisable(true);
      endGameButton.setDisable(true);
   }

   /*
    * Handles the submission of the user's selections.
    * Verifies if the selected pattern matches the generated pattern and provides feedback.
    */
   private void handleSubmit()
   {
      final List<Square> generatedPattern;
      boolean isCorrect;

      isCorrect = false;

      correctlySelectedSquares.addAll(userSelection);
      submitButton.setDisable(true);

      if(userSelection.isEmpty())
      {
         showEmptyPatternWarning(primaryStage);
         updateSubmitButtonState();  // Disable submit if no selection
         return;
      }

      generatedPattern = engine.getGeneratedPattern();

      try
      {
         isCorrect = engine.verifySelection(userSelection);
      } catch(final IllegalPatternSubmissionException e)
      {
         showIllegalPatternPopup(primaryStage, e.getMessage());
         updateSubmitButtonState();  // Re-enable submit button after error
         return;
      }

      if(isCorrect)
      {
         // Update the tiles if the pattern is correct
         for(final Square square : generatedPattern)
         {
            final Rectangle tile;

            tile = getTile(square.getX(), square.getY());

            if(tile != null)
            {
               tile.setFill(SUCCESS_COLOR);
               tile.setDisable(true);
            }
         }

         // Proceed to the next round if correct
         userSelection.clear();
         engine.nextRound();
         showPattern();  // Show the new pattern
      } else
      {
         // Incorrect selection: reset tiles to default color
         submitButton.setVisible(false);
         showIncorrectPatternPopup(primaryStage);

         for(final Square square : userSelection)
         {
            final Rectangle incorrectSelectedTile;

            incorrectSelectedTile = getTile(square.getX(), square.getY());

            if(incorrectSelectedTile != null)
            {
               incorrectSelectedTile.setFill(DEFAULT_COLOR);
            }
         }
      }

      // Always re-enable the submit button after checking the pattern
      updateSubmitButtonState();
      resetGameButton.setDisable(false);
      endGameButton.setDisable(false);
      userSelection.clear();  // Reset user selection after checking
   }

   /*
    * Disables all tiles on the game board by making them non-interactive.
    * The opacity of each tile is reduced to indicate that the tile is disabled.
    * The mouse click event handler for each tile is also removed to prevent user interaction.
    */
   private void disableAllTiles()
   {
      for(int x = 0; x < engine.getBoardSize(); x++)
      {
         for(int y = 0; y < engine.getBoardSize(); y++)
         {
            final Rectangle tile;

            tile = getTile(x, y);

            if (tile != null)
            {
               // Disable the tile completely by making it non-interactive
               tile.setOpacity(DISABLED_OPACITY);
               tile.setOnMouseClicked(null);
            }
         }
      }
   }

   /*
    * Enables user interaction with all tiles on the game board after the pattern has been generated.
    * The opacity of each tile is restored to its default value, and the mouse click event handler
    * is set to allow users to interact with the tiles again by selecting them.
    */
   private void enableAllTilesAfterPatternGenerated()
   {
      for(int x = 0; x < engine.getBoardSize(); x++)
      {
         for(int y = 0; y < engine.getBoardSize(); y++)
         {
            final Rectangle tile;

            tile = getTile(x, y);

            if(tile != null)
            {
               // Enable user interaction with the tiles again
               final int finalX;
               final int finalY;

               finalX = x;
               finalY = y;

               tile.setOpacity(1);
               tile.setOnMouseClicked(event1 -> handleUserSelection(finalX, finalY, tile));
            }
         }
      }
   }

   /*
    * Retrieves the tile corresponding to the given coordinates in the grid.
    *
    * @param x the row index of the tile
    * @param y the column index of the tile
    * @return the {@link Rectangle} tile at the specified coordinates, or {@code null} if not found
    */
   private Rectangle getTile(final int x, final int y)
   {
      for(javafx.scene.Node node : gridPane.getChildren())
      {
         Integer rowIndex = GridPane.getRowIndex(node);
         Integer colIndex = GridPane.getColumnIndex(node);

         // If row or column index is null, default to 0
         if((rowIndex == null ? 0 : rowIndex) == x && (colIndex == null ? 0 : colIndex) == y)
         {
            return (Rectangle) node;
         }
      }
      return null;
   }


   /**
    * Displays a popup showing the game instructions to the user.
    *
    * @param primaryStage the primary stage of the game to which the popup is shown
    */
   public void showInstructionsPopup(final Stage primaryStage)
   {
      final Popup popup;
      final VBox popupLayout;
      final Label instructionLabel;
      final Label instructionsText;
      final Button startGameButton;
      popup = new Popup();

      popupLayout = new VBox(20);
      popupLayout.setAlignment(Pos.CENTER);
      popupLayout.setAlignment(Pos.CENTER);
      popupLayout.setStyle("-fx-background-color: #3E8EDE; -fx-padding: 20;");

      instructionLabel = new Label("Welcome to the Memory Pattern Game!");
      instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
      popupLayout.getChildren().add(instructionLabel);

      // Add the instructions
      instructionsText = new Label("""
              In this game, a pattern will be shown to you.
              Your task is to select the correct squares as shown in the pattern.
              Every right pattern in the first 10 easy rounds wins you 1 score, but double the penalty if wrong.
              Every right pattern in the 10 intermediate rounds wins you 2 scores, and half the penalty if wrong.
              Every right pattern in the advanced rounds wins you 10 scores, and no penalty.
              
              Click the squares to select them, and press 'Submit' to check your pattern.
              Have fun and Good luck!""");
      instructionsText.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
      popupLayout.getChildren().add(instructionsText);

      startGameButton = new Button("Start Game");
      startGameButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");

      scoreDisplay.setVisible(false);
      winStreak.setVisible(false);
      startGameButton.setOnAction(event ->
      {
         scoreDisplay.setVisible(true);
         resetGameButton.setVisible(true);
         endGameButton.setVisible(true);
         popup.hide();
         submitButton.setVisible(true);
         submitButton.setDefaultButton(true);
         showPattern();
      });

      popupLayout.getChildren().add(startGameButton);
      popup.getContent().add(popupLayout);
      popup.show(primaryStage);
   }

   /*
    * Displays a popup informing the user that their pattern submission is incorrect.
    *
    * @param primaryStage the primary stage of the game to which the popup is shown
    */
   private void showIncorrectPatternPopup(final Stage primaryStage)
   {
      final Popup popup;
      final VBox popupLayout;
      final Label errorMessage;
      final Label scoreLabel;
      final Button tryAgainButton;
      final int spacing;

      spacing = 20;
      popup = new Popup();
      popupLayout = new VBox(spacing);
      popupLayout.setAlignment(Pos.CENTER);
      popupLayout.setStyle("-fx-background-color: beige; -fx-padding: 20px;");

      errorMessage = new Label("Incorrect Pattern! Please try again.");
      errorMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16px;");
      popupLayout.getChildren().add(errorMessage);

      scoreLabel = new Label("Your current score: ");
      scoreLabel.setStyle("-fx-font-size: 14px;");
      popupLayout.getChildren().add(scoreLabel);

      tryAgainButton = new Button("Try Again");
      tryAgainButton.setStyle("-fx-background-color: green; -fx-text-fill: white; " +
              "-fx-font-size: 14px; -fx-padding: 10px;");

      tryAgainButton.setOnAction(event -> handleTryAgain(popup));

      popupLayout.getChildren().add(tryAgainButton);
      popup.getContent().add(popupLayout);
      popup.show(primaryStage);
   }

   /*
    * Displays a popup informing the user that their pattern submission is illegal.
    *
    * @param primaryStage the primary stage of the game to which the popup is shown
    * @param errorMessage the error message to be displayed in the popup
    */
   private void showIllegalPatternPopup(final Stage primaryStage, final String message)
   {
      final Popup popup;
      final VBox popupLayout;
      final Label errorMessage;
      final Button tryAgainButton;
      final int spacing;

      spacing = 20;
      popup = new Popup();
      popupLayout = new VBox(spacing);
      popupLayout.setAlignment(Pos.CENTER);
      popupLayout.setStyle("-fx-background-color: beige; -fx-padding: 20px;");

      errorMessage = new Label(message);
      errorMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16px;");
      popupLayout.getChildren().add(errorMessage);

      tryAgainButton = new Button("Try Again");
      tryAgainButton.setStyle("-fx-background-color: green; -fx-text-fill: white; " +
              "-fx-font-size: 14px; -fx-padding: 10px;");

      tryAgainButton.setOnAction(event -> handleTryAgain(popup));

      popupLayout.getChildren().add(tryAgainButton);
      popup.getContent().add(popupLayout);
      popup.show(primaryStage);
   }

   // Show warning popup when the user tries to submit an empty pattern
   private void showEmptyPatternWarning(final Stage primaryStage)
   {
      final Popup popup;
      final VBox popupLayout;
      final Label errorMessage;
      final Button tryAgainButton;
      final int spacing;

      spacing = 20;
      popup = new Popup();
      popupLayout = new VBox(spacing);
      popupLayout.setAlignment(Pos.CENTER);
      popupLayout.setStyle("-fx-background-color: beige; -fx-padding: 20px;");

      errorMessage = new Label("Cannot submit an empty pattern");
      errorMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16px;");
      popupLayout.getChildren().add(errorMessage);

      tryAgainButton = new Button("Try Again");
      tryAgainButton.setStyle("-fx-background-color: green; -fx-text-fill: white; " +
              "-fx-font-size: 14px; -fx-padding: 10px;");

      tryAgainButton.setOnAction(event -> handleTryAgain(popup));

      popupLayout.getChildren().add(tryAgainButton);
      popup.getContent().add(popupLayout);
      popup.show(primaryStage);
   }

   /*
    * Displays a popup with a congratulatory message showing the current win streak.
    * The popup will show a message like "Well Done! You're on X win streaks", where X
    * is the current win streak. The popup automatically closes after 1.5 seconds.
    * This method also updates the win streak display and makes it visible on the UI.
    *
    * @param streak The current win streak, which is used to display the win streak message.
    */
   private void showWinStreakPopup(final int streak)
   {
      final Popup popup;
      final VBox popupLayout;
      final Label errorMessage;
      final int spacing;

      spacing = 20;
      popup = new Popup();
      popupLayout = new VBox(spacing);
      errorMessage = new Label("Well Done! You're on " + engine.getCurrentWinStreak() + " win streaks.");

      winStreak.setText("Win streak: " + engine.getCurrentWinStreak());
      winStreak.setVisible(true);
      popupLayout.setAlignment(Pos.CENTER);
      popupLayout.setStyle("-fx-background-color: beige; -fx-padding: 20px;");
      errorMessage.setStyle("-fx-text-fill: Green; -fx-font-weight: bold; -fx-font-size: 16px;");
      popupLayout.getChildren().add(errorMessage);

      popup.getContent().add(popupLayout);
      popup.show(primaryStage);

      // Create a PauseTransition to close the popup after 1.5 seconds
      final PauseTransition closePopupTransition;

      closePopupTransition = new PauseTransition(Duration.seconds(1.5));

      closePopupTransition.setOnFinished(event -> popup.hide());

      // Start the transition
      closePopupTransition.play();
   }

   /*
    * Handles the "Try Again" action when the user chooses to retry the current round.
    * This method clears the previous user selections, shows the pattern again, and makes the submit button visible.
    *
    * @param popup the {@link Popup} that will be closed after the "Try Again" action is performed
    * @see #userSelection
    * @see #submitButton
    * @see #showPattern()
    */
   private void handleTryAgain(final Popup popup)
   {
      submitButton.setDisable(true);
      engine.nextRound();
      userSelection.clear();
      submitButton.setVisible(true);
      submitButton.setDefaultButton(true);
      showPattern();
      popup.hide();
   }

   /*
    * Handles the end of the game by calling the engine's endGame method, resetting the board,
    * showing an instructions popup, hiding the reset and end game buttons, and reapplying the styles
    * to ensure consistency in the UI after the game ends.
    */
   private void handleEndGame()
   {
      engine.endGame();
      resetBoard();
      showInstructionsPopup(primaryStage);
      // Set visibility of buttons
      resetGameButton.setVisible(false);
      endGameButton.setVisible(false);
      // Reapply styles to ensure consistency after the game ends
      applyStylesToButtons();
      applyStylesToScoreDisplay();
   }

   /*
    * Handles the resetting of the game by calling the engine's resetGame method, resetting the board,
    * showing an instructions popup, hiding the reset and end game buttons, and reapplying the styles
    * to ensure consistency in the UI after resetting the game.
    */
   private void handleResetGame()
   {
      engine.resetGame();
      resetBoard();
      showInstructionsPopup(primaryStage);
      // Set visibility of buttons
      resetGameButton.setVisible(false);
      endGameButton.setVisible(false);
      // Reapply styles to ensure consistency after resetting the game
      applyStylesToButtons();
      applyStylesToScoreDisplay();
   }

   /*
    * Resets the board by removing the old grid from the layout, recreating a new grid,
    * and re-adding it to the layout at the correct position. Also ensures the new grid
    * fills the available space and applies necessary styles.
    */
   private void resetBoard()
   {
      layout.getChildren().remove(gridPane); // Remove the old grid from the layout
      gridPane = createGrid(engine.getBoardSize()); // Recreate the grid
      VBox.setVgrow(gridPane, Priority.ALWAYS); // Ensure the new grid fills space
      layout.getChildren().add(1, gridPane); // Re-add the new grid at the correct position
      // Reapply styles for the grid (if needed)
      gridPane.setAlignment(Pos.CENTER);
   }

   /*
    * Applies consistent styles to the game action buttons (submit, reset, end game),
    * ensuring a uniform look and feel throughout the game interface.
    */
   private void applyStylesToButtons()
   {
      submitButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
              "-fx-background-color: green; -fx-text-fill: white;");
      resetGameButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
              "-fx-background-color: green; -fx-text-fill: white;");
      endGameButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;" +
              " -fx-background-color: green; -fx-text-fill: white;");
   }

   /*
    * Applies consistent styles to the score display, ensuring that the score is styled
    * clearly with appropriate size, color, and weight.
    */
   private void applyStylesToScoreDisplay()
   {
      scoreDisplay.setStyle("-fx-font-size: 16px; -fx-text-fill: green; -fx-font-weight: bold;");
   }

   /*
    * Updates the state of the submit button, disabling it when there is no user selection,
    * and enabling it when a valid selection is made.
    */
   private void updateSubmitButtonState()
   {
      submitButton.setDisable(userSelection.isEmpty());
   }

   /*
    * Validates the provided game engine.
    *
    * @param engine the {@link MemoryGameEngine} to validate
    * @throws IllegalArgumentException if the provided {@code engine} is {@code null}
    */
   private static void validateGameEngine(final MemoryGameEngine engine)
   {
      if(engine == null)
      {
         throw new IllegalArgumentException("Memory Game Engine must not be null");
      }
   }
}
