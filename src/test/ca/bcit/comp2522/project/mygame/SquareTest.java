package ca.bcit.comp2522.project.mygame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SquareTest {

   // Test 1: Testing constructor and getter methods
   @Test
   public void testSquareConstructorAndGetters() {
      Square square = new Square(3, 4);
      assertEquals(3, square.getX(), "X coordinate should be 3");
      assertEquals(4, square.getY(), "Y coordinate should be 4");
   }

   // Test 2: Testing equality of two Square objects
   @Test
   public void testEquals() {
      Square square1 = new Square(2, 5);
      Square square2 = new Square(2, 5);
      Square square3 = new Square(1, 5);
      Square square4 = new Square(2, 6);

      // Squares with the same coordinates should be equal
      assertEquals(square1, square2, "Squares with the same coordinates should be equal");

      // Squares with different x coordinates should not be equal
      assertNotEquals(square1, square3, "Squares with different x coordinates should not be equal");

      // Squares with different y coordinates should not be equal
      assertNotEquals(square1, square4, "Squares with different y coordinates should not be equal");

      // Comparing to null should return false
      assertNotEquals(square1, null, "Should return false when comparing to null");

      // Comparing to an object of a different type should return false
      assertNotEquals(square1, new Object(), "Should return false when comparing to an object of different type");
   }

   // Test 3: Testing hashCode method
   @Test
   public void testHashCode() {
      Square square1 = new Square(3, 4);
      Square square2 = new Square(3, 4);
      Square square3 = new Square(4, 4);

      // Two squares with the same coordinates should have the same hash code
      assertEquals(square1.hashCode(), square2.hashCode(), "Squares with the same coordinates should have the same hash code");

      // Different coordinates should produce different hash codes
      assertNotEquals(square1.hashCode(), square3.hashCode(), "Squares with different coordinates should have different hash codes");
   }

   // Test 4: Testing toString method
   @Test
   public void testToString() {
      Square square = new Square(7, 8);
      assertEquals("{7, 8}", square.toString(), "toString should return the correct string representation");
   }
}
