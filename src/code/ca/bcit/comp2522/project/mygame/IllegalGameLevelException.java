package ca.bcit.comp2522.project.mygame;

/**
 * Exception thrown when an illegal or invalid game level is encountered.
 * This class extends {@link RuntimeException} and is used to signal that
 * a game level is not valid within the context of the memory game.
 * Typically used when trying to set or access a game level that is not supported
 * or is out of the allowed range.
 */
public class IllegalGameLevelException extends RuntimeException
{
   /**
    * Constructs a new IllegalGameLevelException with the specified detail message.
    *
    * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method
    */
   public IllegalGameLevelException(final String message)
   {
      super(message);
   }
}
