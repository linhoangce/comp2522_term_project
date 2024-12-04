package ca.bcit.comp2522.project.mygame;

import java.util.*;

/**
 * This class implements the {@link GameLevel} interface for the easy level of the memory pattern game.
 * It generates a memory pattern with a default board size and a fixed number of squares to memorize,
 * following the logic defined in the game level interface.
 * The easy level has a smaller board size and generates a pattern with fewer squares compared to higher levels.
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class MemoryPatternGame implements GameLevel
{
   /**
    * Generates a pattern of squares to be memorized by the player. This method is called each round
    * and generates a pattern based on the current round and board size.
    *
    * @param round the current round number, influencing the number of squares in the pattern
    * @param boardSize the size of the game board
    * @param selectedSquares a list of squares previously selected by the player
    * @param occupiedSquares a set of squares that are already occupied on the board
    * @return a list of {@link Square} objects representing the generated pattern for the current round
    */
   @Override
   public List<Square> generatePattern(final int round,
                                       final int boardSize,
                                       final List<Square> selectedSquares,
                                       final Set<Square> occupiedSquares)
   {
      return generatePatternLogic(round, boardSize, selectedSquares, occupiedSquares, 1);
   }

   /**
    * Returns the initial size of the board at the start of the game for the easy level.
    *
    * @return the initial board size for the easy level
    */
   @Override
   public int getInitialBoardSize()
   {
      return GameLevel.DEFAULT_BOARD_SIZE;
   }

   /**
    * Handles leveling up the game. In the easy level, no action is taken as the level does not change.
    *
    * @param engine the current game engine instance
    */
   @Override
   public void levelUp(final MemoryGameEngine engine)
   {
      return;
   }

   /**
    * The logic to generate a pattern of squares based on the current round, board size, and a multiplier
    * for the level. The pattern starts from a random square and adds adjacent squares to form a path.
    *
    * @param round the current round number, influencing the number of squares in the pattern
    * @param boardSize the size of the game board
    * @param selectedSquares a list of squares previously selected by the player
    * @param occupiedSquares a set of squares that are already occupied on the board
    * @param levelMultiplier a multiplier for the number of squares to generate in the pattern
    * @return a list of {@link Square} objects representing the generated pattern for the current round
    */
   private List<Square> generatePatternLogic(final int round,
                                             final int boardSize,
                                             final List<Square> selectedSquares,
                                             final Set<Square> occupiedSquares,
                                             final int levelMultiplier)
   {
      final List<Square> newPattern;
      final Random rand;
      final int left;
      final int noMove;
      final int right;
      Square currentSquare;

      newPattern = new ArrayList<>();
      rand = new Random();
      left = -1;
      noMove = 0;
      right = 1;

      // Directions for adjacent squares: (dx, dy) pairs for 8 directions (horizontal, vertical, diagonal)
      final int[] directions = {left, noMove, right};

      // Make sure no new root square starts from the previous pattern
      do
      {
         final int startX;
         final int startY;
         startX = rand.nextInt(GameLevel.ADVANCED_LEVEL);
         startY = rand.nextInt(GameLevel.ADVANCED_LEVEL);
         currentSquare = new Square(startX, startY);
      } while (occupiedSquares.contains(currentSquare) || selectedSquares.contains(currentSquare));

      newPattern.add(currentSquare);
      occupiedSquares.add(currentSquare);


      for(int i = 0; i < round * levelMultiplier; i++)
      {
         final List<Square> possibleAdjacentSquares;

         possibleAdjacentSquares = new ArrayList<>();

         // Check all 8 adjacent directions for valid moves from the current square
         for(final int dx : directions)
         {
            for(final int dy : directions)
            {
               if(dx == noMove && dy == noMove)
               {
                  continue; // Skip the current square itself
               }

               final int newX;
               final int newY;

               newX = currentSquare.getX() + dx;
               newY = currentSquare.getY() + dy;

               // Ensure the new square is within bounds of the board
               if(newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize)
               {
                  final Square adjacentSquare;

                  adjacentSquare = new Square(newX, newY);

                  // Only add squares that are not already occupied
                  if(!occupiedSquares.contains(adjacentSquare))
                  {
                     possibleAdjacentSquares.add(adjacentSquare);
                  }
               }
            }
         }

         if(!possibleAdjacentSquares.isEmpty())
         {
            // Randomly select one of the possible adjacent squares
            final int randomIndex;
            final Square nextSquare;

            randomIndex = rand.nextInt(possibleAdjacentSquares.size());
            nextSquare = possibleAdjacentSquares.get(randomIndex);

            // Add the selected square to the pattern
            newPattern.add(nextSquare);
            occupiedSquares.add(nextSquare);
            currentSquare = nextSquare;
         } else
         {
            boolean moveFound;

            moveFound = false;

            // Try finding a move adjacent to any square in the current pattern
            for(final Square patternSquare : newPattern)
            {
               possibleAdjacentSquares.clear();

               for(final int dx : directions)
               {
                  for(final int dy : directions)
                  {
                     if(dx == noMove && dy == noMove)
                     {
                        continue;
                     }

                     final int newX;
                     final int newY;

                     newX = currentSquare.getX() + dx;
                     newY = currentSquare.getY() + dy;

                     if(newX >= noMove && newX < boardSize && newY >= noMove && newY < boardSize)
                     {
                        final Square adjacentSquare;

                        adjacentSquare = new Square(newX, newY);

                        if(!occupiedSquares.contains(adjacentSquare))
                        {
                           possibleAdjacentSquares.add(adjacentSquare);
                        }
                     }
                  }
               }

               if(!possibleAdjacentSquares.isEmpty())
               {
                  // Select a random adjacent square
                  final int randomIndex;
                  final Square nextSquare;

                  randomIndex = rand.nextInt(possibleAdjacentSquares.size());
                  nextSquare = possibleAdjacentSquares.get(randomIndex);

                  newPattern.add(nextSquare);
                  occupiedSquares.add(nextSquare);
                  currentSquare = nextSquare;
                  moveFound = true;
                  break;
               }
            }

            if(!moveFound)
            {
               // Find the nearest unoccupied square to the pattern
               Square nearestSquare;
               double minDistance;
               final int power;

               power = 2;
               nearestSquare = null;
               minDistance = Double.MAX_VALUE;

               for(int x = 0; x < boardSize; x++)
               {
                  for(int y = 0; y < boardSize; y++)
                  {
                     final Square square;

                     square = new Square(x, y);

                     if(!occupiedSquares.contains(square))
                     {
                        for(Square patternSquare : newPattern)
                        {
                           final double distance;

                           distance = Math.sqrt(Math.pow(square.getX() - patternSquare.getX(), power) +
                                       Math.pow(square.getY() - patternSquare.getY(), power));

                           if(distance < minDistance)
                           {
                              minDistance = distance;
                              nearestSquare = square;
                           }
                        }
                     }
                  }
               }

               if(nearestSquare != null)
               {
                  newPattern.add(nearestSquare);
                  occupiedSquares.add(nearestSquare);
                  currentSquare = nearestSquare;
               } else
               {
                  break; // No more available moves
               }
            }
         }
      }

      return newPattern;
   }


   /*
    * Generates a random X coordinate within the bounds of the board size.
    *
    * @param boardSize the size of the board
    * @return a random X coordinate
    */
   private int randomX(int boardSize)
   {
      return (int) (Math.random() * boardSize);
   }

   /*
    * Generates a random Y coordinate within the bounds of the board size.
    *
    * @param boardSize the size of the board
    * @return a random Y coordinate
    */
   private int randomY(int boardSize)
   {
      return (int) (Math.random() * boardSize);
   }
}

