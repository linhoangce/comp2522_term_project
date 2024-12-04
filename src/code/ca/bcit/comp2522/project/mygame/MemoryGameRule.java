package ca.bcit.comp2522.project.mygame;

import java.util.List;

/**
 * The {@code MemoryGameRule} interface defines the contract for checking the validity of a user's pattern submission
 * against the generated pattern in a memory game. Implementing classes should define the specific rules for verifying
 * the user's selections in the context of the game.
 *
 * <p>
 * This interface is intended to be used by different game levels or rule sets to enforce various pattern-checking behaviors.
 * </p>
 *
 * @author Linh Hoang
 */
interface MemoryGameRule
{
   /**
    * Checks whether the user's pattern submission matches the generated pattern.
    *
    * @param userSelections a list of {@link Square} objects representing the user's selected pattern
    * @param generatedPattern a list of {@link Square} objects representing the correct generated pattern
    * @return {@code true} if the user's selections match the generated pattern, {@code false} otherwise
    * @throws IllegalPatternSubmissionException if the submitted pattern is invalid or violates the game rules
    */
   boolean checkPattern(final List<Square> userSelections,
                        final List<Square> generatedPattern) throws IllegalPatternSubmissionException;
}
