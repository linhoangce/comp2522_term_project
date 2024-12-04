package ca.bcit.comp2522.project.mygame;

/**
 * The {@code Square} class represents a 2D coordinate on a grid.
 * Each {@code Square} object stores the {@code x} and {@code y} coordinates
 * of the square, which can be used for various game logic or grid-based
 * operations.
 * <p>
 * This class provides methods for comparing squares, calculating hash codes,
 * and converting the square's coordinates to a string representation.
 * </p>
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class Square
{
   private static final int HASH_CODE_MULTIPLIER = 31;

   private final int x;
   private final int y;

   /**
    * Constructs a new {@code Square} with the specified {@code x} and {@code y} coordinates.
    *
    * @param x the {@code x} coordinate of the square
    * @param y the {@code y} coordinate of the square
    */
   public Square(int x, int y)
   {
      this.x = x;
      this.y = y;
   }

   /**
    * Returns the {@code x} coordinate of the square.
    *
    * @return the {@code x} coordinate
    */
   public int getX()
   {
      return x;
   }

   /**
    * Returns the {@code y} coordinate of the square.
    *
    * @return the {@code y} coordinate
    */
   public int getY()
   {
      return y;
   }

   /**
    * Compares the current {@code Square} object to another object for equality.
    * Two squares are considered equal if they have the same {@code x} and {@code y} coordinates.
    *
    * @param obj the object to compare to
    * @return {@code true} if the squares are equal, {@code false} otherwise
    */
   @Override
   public boolean equals(final Object obj)
   {
      if(obj == null)
      {
         return false;
      }

      if(!obj.getClass().equals(this.getClass()))
      {
         return false;
      }

      final Square square;

      square = (Square) obj;

      return x == square.x && y == square.y;
   }

   /**
    * Returns a hash code for the {@code Square} object.
    * The hash code is computed using the {@code x} and {@code y} coordinates.
    *
    * @return the hash code for the square
    */
   @Override
   public int hashCode()
   {
      return HASH_CODE_MULTIPLIER * Integer.hashCode(x) + Integer.hashCode(y);
   }

   /**
    * Returns a string representation of the {@code Square} object.
    * The string format is {@code "{x, y}"} where {@code x} and {@code y} are the coordinates.
    *
    * @return the string representation of the square
    */
   @Override
   public String toString()
   {
      return "{" + x + ", " + y + "}";
   }
}
