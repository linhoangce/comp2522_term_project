package ca.bcit.comp2522.project.mygame;

import java.util.List;
import java.util.Set;

/**
 * Interface representing the level of the memory game.
 * This interface defines the methods that each game level should implement,
 * including the generation of a pattern, retrieval of the initial board size,
 * and the logic for leveling up.
 *
 * The levels include:
 * <ul>
 *   <li>{@link #EASY_LEVEL} - Easy level with a smaller board size and fewer pattern elements.</li>
 *   <li>{@link #INTERMEDIATE_LEVEL} - Intermediate level with increased difficulty and larger board size.</li>
 *   <li>{@link #ADVANCED_LEVEL} - Advanced level with the highest difficulty and the largest board size.</li>
 * </ul>
 *
 * @author Linh Hoang
 * @version 1.0
 */
public interface GameLevel
{
   int EASY_LEVEL = 10;
   int DEFAULT_BOARD_SIZE = 10;
   int INTERMEDIATE_LEVEL = 20;
   int INTERMEDIATE_BOARD_SIZE_MUL = 2;
   int ADVANCED_LEVEL = 30;
   int ADVANCED_BOARD_SIZE_MUL = 4;

   /**
    * Generates a pattern for the memory game at the specified round and board size.
    * The pattern consists of a list of squares that need to be memorized by the player.
    *
    * @param round the current round number, influencing the number of squares in the pattern
    * @param boardSize the current size of the game board
    * @param selectedSquares a list of squares previously selected by the player
    * @param occupiedSquares a set of squares that are already occupied on the board
    * @return a list of {@link Square} objects representing the generated pattern for the current round
    */
   List<Square> generatePattern(final int round,
                                final int boardSize,
                                final List<Square> selectedSquares,
                                final Set<Square> occupiedSquares);

   /**
    * Returns the initial size of the board at the start of the game.
    * Each level may have a different starting board size.
    *
    * @return the initial board size for the game level
    */
   int getInitialBoardSize();

   /**
    * Levels up the game, adjusting settings for the next level, such as board size.
    * This method will be called when the player progresses to the next level.
    *
    * @param engine the current game engine instance to update level and other settings
    */
   void levelUp(final MemoryGameEngine engine);
}
