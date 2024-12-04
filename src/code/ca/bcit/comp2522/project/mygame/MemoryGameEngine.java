package ca.bcit.comp2522.project.mygame;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * This class implements the engine for a memory game, managing game state such as rounds, patterns,
 * score, and level. It controls the progression of the game, verifies user input, and adjusts the game
 * difficulty based on the score.
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class MemoryGameEngine implements MemoryGameRule
{
   public static final String SCORE_FILE = "score.txt";
   public static final int DEFAULT_ROUND = 1;
   public static final int LEVEL_UP = 10;
   public static final int BOARD_SIZE_EXPANDED = 2;
   public static final int LEVEL_1_SCORE_THRESHOLD = 10;
   public static final int LEVEL_2_SCORE_THRESHOLD = 20;
   public static final int SCORE_EASY_LEVEL = 1;
   public static final int PENALTY_EASY_LEVEL = -2;
   public static final int SCORE_INTERMEDIATE_LEVEL = 2;
   public static final int PENALTY_INTERMEDIATE_LEVEL = -1;
   public static final int SCORE_ADVANCED_LEVEL = 10;
   public static final int DEFAULT_SCORE = 0;
   public static final int WIN_STREAK_MIN = 3;

   private static MemoryGameEngine gameEngine;

   private final IntegerProperty scoreProperty;
   private final GameLevel gameLevel;
   private final List<Square> generatedPattern;
   private final Set<Square> occupiedSquares;
   private int currentRound;
   private int boardSize;
   private int level;
   private int currentWinStreak;
   private int totalWinStreaks;
   private boolean isGameEnded;
   private Consumer<Integer> winStreakListener;

   static
   {
      gameEngine = null;
   }

   /*
    * Private constructor to initialize a MemoryGameEngine with a given game level.
    *
    * @param gameLevel the {@link GameLevel} for this game instance
    * @throws IllegalGameLevelException if the provided game level is null
    */
   private MemoryGameEngine(final GameLevel gameLevel)
   {
      validateGameLevel(gameLevel);

      this.gameLevel = gameLevel;
      this.generatedPattern = new ArrayList<>();
      this.occupiedSquares = new HashSet<>();
      this.currentRound = DEFAULT_ROUND;
      this.boardSize = GameLevel.ADVANCED_LEVEL;
      scoreProperty = new SimpleIntegerProperty(0);
      currentWinStreak = 0;
      totalWinStreaks = 0;
      isGameEnded = false;
   }

   /**
    * Returns the singleton instance of MemoryGameEngine, creating it if necessary.
    *
    * @param gameLevel the {@link GameLevel} for this game instance
    * @return the singleton {@link MemoryGameEngine} instance
    */
   public static MemoryGameEngine getInstance(final GameLevel gameLevel)
   {
      if(gameEngine == null)
      {
         gameEngine = new MemoryGameEngine(gameLevel);
      }

      return gameEngine;
   }

   /**
    * Sets the current round number in the game.
    *
    * @param currentRound the current round number to be set.
    */
   public void setCurrentRound(final int currentRound)
   {
      this.currentRound = currentRound;
   }

   /**
    * Sets the size of the game board.
    *
    * @param boardSize the new board size to be set.
    */
   public void setBoardSize(final int boardSize)
   {
      this.boardSize = boardSize;
   }

   /**
    * Sets the current difficulty level of the game.
    *
    * @param level the difficulty level to be set.
    */
   public void setLevel(final int level)
   {
      this.level = level;
   }

   /**
    * Sets the current winning streak in the game.
    *
    * @param currentWinStreak the number of consecutive wins to be set.
    */
   public void setCurrentWinStreak(final int currentWinStreak)
   {
      this.currentWinStreak = currentWinStreak;
   }

   /**
    * Sets the total number of win streaks achieved in the game.
    *
    * @param totalWinStreaks the total win streaks to be set.
    */
   public void setTotalWinStreaks(final int totalWinStreaks)
   {
      this.totalWinStreaks = totalWinStreaks;
   }

   /**
    * Sets the score property value.
    *
    * @param score the score value to be set.
    */
   public void setScoreProperty(final int score)
   {
      scoreProperty.set(score);
   }

   /**
    * Returns the generated pattern for the current round.
    *
    * @return the list of {@link Square} objects representing the pattern
    */
   public List<Square> getGeneratedPattern()
   {
      return generatedPattern;
   }

   /**
    * Returns the current instance of the MemoryGameEngine.
    *
    * @return the {@link MemoryGameEngine} instance
    */
   public MemoryGameEngine getGameEngine()
   {
      return gameEngine;
   }

   /**
    * Returns the current board size for the game.
    *
    * @return the size of the board
    */
   public int getBoardSize()
   {
      return boardSize;
   }

   /**
    * Retrieves the current score value.
    *
    * @return the current score.
    */
   public int getScore()
   {
      return scoreProperty.get();
   }

   /**
    * Retrieves the score property (observable) for the game.
    *
    * @return the IntegerProperty representing the score.
    */
   public IntegerProperty scoreProperty()
   {
      return scoreProperty;
   }

   /**
    * Retrieves the current win streak.
    *
    * @return the current win streak count.
    */
   public int getCurrentWinStreak()
   {
      return currentWinStreak;
   }

   /**
    * Retrieves the total number of win streaks achieved.
    *
    * @return the total win streaks count.
    */
   public int getTotalWinStreaks()
   {
      return totalWinStreaks;
   }

   /**
    * Returns the set of occupied squares on the game board.
    *
    * @return a {@link Set} of {@link Square} objects representing the occupied squares
    */
   public Set<Square> getOccupiedSquares()
   {
      return occupiedSquares;
   }


   /**
    * Retrieves the current round number.
    *
    * @return the current round number.
    */
   public int getCurrentRound()
   {
      return currentRound;
   }

   /**
    * Sets a listener for the win streak updates. The listener will be notified whenever the win streak changes.
    *
    * @param listener the Consumer that will handle win streak updates.
    */
   public void setWinStreakListener(Consumer<Integer> listener)
   {
      this.winStreakListener = listener;
   }

   /**
    * Notifies the listener about a change in the current win streak.
    * If a listener is set, it is called with the current win streak value.
    * If no listener is set, a message is printed to the console.
    */
   private void notifyWinStreakListener()
   {
      if(winStreakListener != null)
      {
         winStreakListener.accept(currentWinStreak);
      }
      else
      {
         System.out.println("Win streak listener is not set.");
      }
   }

   /**
    * Updates the score based on the current round and whether the user's selection was correct.
    *
    * @param isCorrect whether the user's selection was correct
    */
   private void updateScore(final boolean isCorrect)
   {
      if(isCorrect)
      {
         currentWinStreak++;

         // Notify listener if win streak reaches a milestone (e.g., 7)
         if(currentWinStreak >= WIN_STREAK_MIN)
         {
            totalWinStreaks++;
            notifyWinStreakListener();
         }

         if(currentRound <= GameLevel.EASY_LEVEL)
         {
            scoreProperty.set(scoreProperty.get() + SCORE_EASY_LEVEL);
         }
         else if(currentRound <= GameLevel.INTERMEDIATE_LEVEL)
         {
            scoreProperty.set(scoreProperty.get() + SCORE_INTERMEDIATE_LEVEL);
         }
         else
         {
            scoreProperty.set(scoreProperty.get() + SCORE_ADVANCED_LEVEL);
         }
      }
      else
      {
         currentWinStreak = 0; // Reset streak on wrong answer

         if(currentRound <= GameLevel.EASY_LEVEL)
         {
            scoreProperty.set(scoreProperty.get() + PENALTY_EASY_LEVEL);
         }
         else if (currentRound <= GameLevel.INTERMEDIATE_LEVEL)
         {
            scoreProperty.set(scoreProperty.get() + PENALTY_INTERMEDIATE_LEVEL);
         }
         else
         {
            scoreProperty.set(scoreProperty.get() + DEFAULT_SCORE);
         }
      }
   }


   /**
    * Proceeds to the next round by generating a new pattern and updating the occupied squares.
    */
   public void nextRound()
   {
      final List<Square> newPattern;

      newPattern = gameLevel.generatePattern(currentRound, boardSize, generatedPattern, occupiedSquares);

      // Update the occupiedSquares set to include the newly generated pattern
      occupiedSquares.addAll(newPattern);
      generatedPattern.clear();
      generatedPattern.addAll(newPattern);
   }

   /**
    * Verifies the user's selected pattern against the generated pattern. If the selection is correct,
    * the score is incremented and the game progresses to the next round.
    *
    * @param userSelection the list of squares selected by the user
    * @return true if the user's selection is correct, false otherwise
    * @throws IllegalPatternSubmissionException if the user selection is invalid
    */
   public boolean verifySelection(final List<Square> userSelection)
           throws IllegalPatternSubmissionException
   {
      final boolean isCorrect;

      if(userSelection == null || userSelection.isEmpty())
      {
         throw new IllegalPatternSubmissionException("You have not selected a pattern yet!");
      }

      isCorrect = checkPattern(userSelection, generatedPattern);

      updateScore(isCorrect);

      if(isCorrect)
      {
         if(currentRound % LEVEL_UP == 0)
         {
            boardSize *= BOARD_SIZE_EXPANDED;
         }

         currentRound++;
         gameLevel.levelUp(gameEngine);
      }

      return isCorrect;
   }

   /**
    * Checks if the user's selected pattern matches the generated pattern.
    *
    * @param userSelections the list of squares selected by the user
    * @param generatedPattern the list of squares representing the generated pattern
    * @return true if the selected pattern matches the generated pattern, false otherwise
    * @throws IllegalPatternSubmissionException if the user selection is invalid
    * @throws IllegalArgumentException if the generated pattern is invalid
    */
   @Override
   public boolean checkPattern(final List<Square> userSelections,
                               final List<Square> generatedPattern) throws IllegalPatternSubmissionException
   {
      if(userSelections == null || userSelections.isEmpty())
      {
         throw new IllegalPatternSubmissionException("You have not selected a pattern yet!");
      }

      if(generatedPattern == null || generatedPattern.isEmpty())
      {
         throw new IllegalArgumentException("Illegal Generated Pattern");
      }

      if(userSelections.size() != generatedPattern.size())
      {
         return false;
      }

      return new HashSet<>(userSelections).containsAll(generatedPattern);
   }

   /**
    * Sets the board size based on the current score. Increases the size for higher levels.
    */
   public void setBoardSize()
   {
      if(scoreProperty.get() >= LEVEL_2_SCORE_THRESHOLD)
      {
         level = GameLevel.ADVANCED_LEVEL;
         boardSize = GameLevel.DEFAULT_BOARD_SIZE * GameLevel.ADVANCED_BOARD_SIZE_MUL;
      } else if(scoreProperty.get() >= LEVEL_1_SCORE_THRESHOLD)
      {
         level = GameLevel.INTERMEDIATE_LEVEL;
         boardSize = GameLevel.DEFAULT_BOARD_SIZE * GameLevel.INTERMEDIATE_BOARD_SIZE_MUL;
      } else
      {
         level = GameLevel.EASY_LEVEL;
         boardSize = GameLevel.DEFAULT_BOARD_SIZE;
      }
   }

   /**
    * Ends the current game and saves the score to a file.
    * If the game has already ended, it prevents duplicate end actions.
    * The score, including the current round, score, win streaks, and time of play, is saved
    * in the specified score file. Afterward, the game is reset for a new session.
    *
    */
   public void endGame()
   {
      if(isGameEnded)
      {
         System.out.println("Game has already ended.");
         return; // Prevent duplicate end actions
      }

      final MyScore score;

      score = new MyScore(
              LocalDateTime.now(),
              currentRound,
              scoreProperty.get(),
              currentWinStreak,
              totalWinStreaks
      );

      try
      {
         MyScore.appendScoreToFile(score, SCORE_FILE);
         System.out.println("Score successfully saved.");
      }
      catch(final IOException e)
      {
         System.out.println("Error writing score to file: " + e.getMessage());
      }

      isGameEnded = true; // Mark the game as ended
      resetGame();
   }

   /**
    * Resets the game state to its initial values, allowing a new game session to start.
    * This includes resetting the score, round number, win streaks, and clearing generated patterns
    * and occupied squares.
    */
   public void resetGame()
   {
      final int resetValue = 0;

      isGameEnded = false; // Allow new game to start
      scoreProperty.set(resetValue);
      currentRound = DEFAULT_ROUND;
      currentWinStreak = resetValue;
      totalWinStreaks = resetValue;

      generatedPattern.clear();
      occupiedSquares.clear();

      System.out.println("Game has been reset!");
   }

   /*
    * Validates that the given game level is not null.
    *
    * @param gameLevel the {@link GameLevel} to validate
    * @throws IllegalGameLevelException if the provided game level is null
    */
   private static void validateGameLevel(final GameLevel gameLevel)
   {
      if(gameLevel == null)
      {
         throw new IllegalGameLevelException("Game level must not be null!");
      }
   }
}

