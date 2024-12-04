package ca.bcit.comp2522.project.wordgame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The {@code Score} class represents a record of a game score, capturing statistics
 * about the number of games played, correct and incorrect attempts, and the date and time
 * the game was played. It provides methods to calculate the total score, the average score
 * per game, and to persist and retrieve scores from a file.
 * This class provides functionality to:
 * <ul>
 *   <li>Store game statistics, including the number of games played and the number of correct
 *       and incorrect attempts for each game.</li>
 *   <li>Calculate the total score and average score based on the number of correct attempts.</li>
 *   <li>Save the score to a file and read scores from an existing file.</li>
 *   <li>Format and display the score information in a human-readable format.</li>
 * </ul>
 *
 * <p>Scores are written to a file with a formatted structure and can be read back into
 * the program as {@code Score} objects.</p>
 *
 * <p><strong>Note:</strong> The file operations assume that the file exists, and
 * errors related to file creation or reading are handled accordingly.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * LocalDateTime dateTime = LocalDateTime.now();
 * Score score = new Score(dateTime, 10, 8, 2, 0);
 * System.out.println(score.toString());
 * Score.appendScoreToFile(score, "scores.txt");
 * </pre>
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class Score
{
   private final String        dateTimePlayed;
   private final int           numGamesPlayed;
   private final int           numCorrectFirstAttempt;
   private final int           numCorrectSecondAttempt;
   private final int           numIncorrectTwoAttempts;

   /**
    * Constructs a new {@code Score} object.
    *
    * @param dateTimePlayed the date and time the game was played
    * @param numGamesPlayed the number of games played
    * @param numCorrectFirstAttempt the number of correct answers on the first attempt
    * @param numCorrectSecondAttempt the number of correct answers on the second attempt
    * @param numIncorrectTwoAttempts the number of incorrect answers after two attempts
    * @throws IllegalArgumentException if any of the input parameters are invalid
    */
   public Score(final LocalDateTime dateTimePlayed,
                final int numGamesPlayed,
                final int numCorrectFirstAttempt,
                final int numCorrectSecondAttempt,
                final int numIncorrectTwoAttempts)
   {
      validateDateTime(dateTimePlayed);
      validateStats(numGamesPlayed);
      validateStats(numCorrectFirstAttempt);
      validateStats(numCorrectSecondAttempt);
      validateStats(numIncorrectTwoAttempts);

      final DateTimeFormatter formatter;
      final String            formattedDateTime;

      formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      formattedDateTime = dateTimePlayed.format(formatter);

      this.dateTimePlayed = formattedDateTime;
      this.numGamesPlayed = numGamesPlayed;
      this.numCorrectFirstAttempt = numCorrectFirstAttempt;
      this.numCorrectSecondAttempt = numCorrectSecondAttempt;
      this.numIncorrectTwoAttempts = numIncorrectTwoAttempts;
   }

   /**
    * Gets the date and time when the game was played.
    *
    * @return the date and time the game was played
    */
   public String getDateTimePlayed()
   {
      return dateTimePlayed;
   }

   /**
    * Gets the number of games played.
    *
    * @return the number of games played
    */
   public int getNumGamesPlayed()
   {
      return numGamesPlayed;
   }

   /**
    * Gets the number of correct answers on the first attempt.
    *
    * @return the number of correct answers on the first attempt
    */
   public int getNumCorrectFirstAttempt()
   {
      return numCorrectFirstAttempt;
   }

   /**
    * Gets the number of correct answers on the second attempt.
    *
    * @return the number of correct answers on the second attempt
    */
   public int getNumCorrectSecondAttempt()
   {
      return numCorrectSecondAttempt;
   }

   /**
    * Gets the number of incorrect answers after two attempts.
    *
    * @return the number of incorrect answers after two attempts
    */
   public int getNumIncorrectTwoAttempts()
   {
      return numIncorrectTwoAttempts;
   }

   /**
    * Calculates the total score based on the number of correct answers on both attempts.
    *
    * @return the total score
    */
   public int getTotalScore()
   {
      return numCorrectFirstAttempt * 2 + numCorrectSecondAttempt;
   }

   /**
    * Calculates the average score per game.
    *
    * @return the average score per game
    */
   public int getScore()
   {
      return (numCorrectFirstAttempt * 2 + numCorrectSecondAttempt) / numGamesPlayed;
   }

   /**
    * Appends a {@code Score} object to a file.
    *
    * @param score the score to append
    * @param fileName the name of the file to append to
    * @throws IOException if an I/O error occurs while writing to the file
    */
   public static void appendScoreToFile(final Score score,
                                        final String fileName) throws IOException
   {
      final Path filePath;

      filePath = createOutputFile(fileName);

      if(Files.notExists(filePath))
      {
         throw new IOException("File does not exist " + filePath.getFileName());
      }

      if(score != null)
      {
         try
         {
            Files.writeString(filePath, score.toString() + System.lineSeparator(), StandardOpenOption.APPEND);
         } catch(final IOException e)
         {
            System.out.println("Error writing score to file, " + e.getMessage());
         }
      }
   }

   /**
    * Reads scores from a file and returns a list of {@code Score} objects.
    *
    * @param fileName the name of the file to read from
    * @return a list of {@code Score} objects
    * @throws IOException if an I/O error occurs while reading from the file
    */
   public static List<Score> readScoresFromFile(final String fileName) throws IOException
   {
      final List<Score> scoreList;
      final List<String> fileContent;
      final Path filePath;

      scoreList = new ArrayList<>();
      fileContent = new ArrayList<>();
      filePath = createOutputFile(fileName);

      if(Files.notExists(filePath))
      {
         System.out.println("File does not exist " + fileName);
         return scoreList;
      }

      try(final Stream<String> lines = Files.lines(filePath))
      {
         final List<String> parsedFileContent;

         parsedFileContent = lines.filter(line -> line != null)
                                    .filter(line -> !line.isBlank())
                                    .toList();

         if(parsedFileContent != null)
         {
            fileContent.addAll(parsedFileContent);
         }
      } catch(final IOException e)
      {
         System.out.println("Error reading from file " + filePath.getFileName() + ", " + e.getMessage());
      }

      addScoreToList(scoreList, fileContent);
      return scoreList;

   }

   /**
    * Adds parsed scores from a file content list to the score list.
    *
    * @param scoreList the list to which scores are added
    * @param fileContent the list of file content containing score data
    * @throws IllegalArgumentException if string format does not match
    */
   public static void  addScoreToList(final List<Score> scoreList,
                                       final List<String> fileContent)
   {
      final int NEXT_SCORE = 6;
      int j = 1;
      if(scoreList != null && fileContent != null)
      {
         final int size;
         LocalDateTime dateTimePlayed;
         int numGamesPlayed;
         int numCorrectFirstGuess;
         int numCorrectSecondGuess;
         int numIncorrectTwoAttempts;

         dateTimePlayed = null;
         numGamesPlayed = 0;
         numCorrectFirstGuess = 0;
         numCorrectSecondGuess = 0;
         numIncorrectTwoAttempts = 0;

         size = fileContent.size();

         for(int i = 0; i < size; i++)
         {
            final Score score;
            final String dateTimeStr;
            final String dataStr;

            dataStr = fileContent.get(i);


            if(dataStr.contains("Date and Time:"))
            {
               dateTimeStr = dateTimeParser(dataStr);
               dateTimePlayed = convertStrToLocalDateTime(dateTimeStr);
               j++;
            } else if(dataStr.contains("Games Played:"))
            {
               j++;
               numGamesPlayed = parseDigitFromString(dataStr);
            } else if(dataStr.contains("Correct First Attempts:"))
            {
               j++;
               numCorrectFirstGuess = parseDigitFromString(dataStr);
            } else if(dataStr.contains("Correct Second Attempts:"))
            {
               j++;
               numCorrectSecondGuess = parseDigitFromString(dataStr);
            } else if(dataStr.contains("Incorrect Attempts:"))
            {
               j++;
               numIncorrectTwoAttempts = parseDigitFromString(dataStr);
            } else if(dataStr.contains("Score:"))
            {
               j++;
            } else
            {
               throw new IllegalArgumentException("Invalid string format!");
            }

            if(j % NEXT_SCORE == 0)
            {
               score = new Score(dateTimePlayed,
                       numGamesPlayed,
                       numCorrectFirstGuess,
                       numCorrectSecondGuess,
                       numIncorrectTwoAttempts);

               scoreList.add(score);
            }
         }
      }
   }

   /**
    * Parses the first sequence of digits from the given string.
    *
    * @param str the string to parse
    * @return the first integer found in the string, or -1 if no digits are found
    */
   private static int parseDigitFromString(final String str)
   {
      final Pattern pattern;
      final Matcher matcher;
      int result;

      pattern = Pattern.compile("\\d+");
      matcher = pattern.matcher(str);
      result = - 1;

      if(matcher.find())
      {
         result = Integer.parseInt(matcher.group());
      }

      return result;
   }

   /**
    * Parses a date-time string and formats it into the pattern "yyyy-MM-dd HH:mm:ss".
    *
    * @param dateTimeStr the date-time string to parse
    * @return the formatted date-time string, or an empty string if no match is found
    */
   public static String dateTimeParser(final String dateTimeStr)
   {
      final Pattern pattern;
      final Matcher matcher;
      String dateTimeResult;


      pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
      matcher = pattern.matcher(dateTimeStr);
      dateTimeResult = "";

      if(matcher.find())
      {
         final String dateTimeMatch;
         final LocalDateTime dateTime;
         final DateTimeFormatter formatter;

         dateTimeMatch = matcher.group();
         formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
         dateTime = LocalDateTime.parse(dateTimeMatch, formatter);
         dateTimeResult = dateTime.format(formatter);
      }

      return dateTimeResult;
   }

   /**
    * Converts a date-time string into a LocalDateTime object.
    *
    * @param dateTimeStr the date-time string to convert
    * @return a LocalDateTime object representing the parsed date-time
    */
   private static LocalDateTime convertStrToLocalDateTime(final String dateTimeStr)
   {
      final LocalDateTime result;
      final DateTimeFormatter formatter;

      formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      result = LocalDateTime.parse(dateTimeStr, formatter);

      return result;
   }

   /**
    * Returns a string representation of the current object, including the date and time, game statistics, and score.
    *
    * @return a string representation of the object
    */
   @Override
   public String toString()
   {
      final StringBuilder sb;
      sb = new StringBuilder();

      sb.append("Date and Time: ").append(dateTimePlayed).append('\n');
      sb.append("Games Played: ").append(numGamesPlayed).append("\n");
      sb.append("Correct First Attempts: ").append(numCorrectFirstAttempt).append("\n");
      sb.append("Correct Second Attempts: ").append(numCorrectSecondAttempt).append("\n");
      sb.append("Incorrect Attempts: ").append(numIncorrectTwoAttempts).append("\n");
      sb.append("Score: ").append(getTotalScore()).append(" points").append("\n");

      return sb.toString();
   }

   /**
    * Creates an output file in the "src/output" directory with the specified file name.
    * If the directory does not exist, it is created.
    *
    * @param fileName the name of the file to create
    * @return the path to the created file
    * @throws IOException if an I/O error occurs while creating the file or directory
    */
   public static Path createOutputFile(final String fileName) throws IOException
   {
      final Path dirPath;
      final Path filePath;

      dirPath = Paths.get("src", "output");

      if(Files.notExists(dirPath))
      {
         try
         {
            Files.createDirectories(dirPath);
         } catch(final IOException e)
         {
            System.out.println("Error creating path " + dirPath.toString());
         }
      }

      filePath = dirPath.resolve(fileName);

      if(Files.exists(filePath))
      {
         System.out.println("Files already exist!");
      } else
      {
         try
         {
            Files.createFile(filePath);
            System.out.println("File created. " + filePath);
         } catch (final IOException e)
         {
            System.out.println("Error creating file. " + e.getMessage());
         }
      }

      return filePath;
   }

   /*
    * Validates the date and time when the game was played.
    *
    * @param dateTimePlayed the date and time to validate
    * @throws IllegalArgumentException if the date and time are invalid
    */
   private static void validateDateTime(final LocalDateTime dateTimePlayed)
   {
      if(dateTimePlayed == null)
      {
         throw new IllegalArgumentException("Date time must not be null!");
      }
   }


   /*
    * Validates the statistics provided for the number of games or attempts.
    *
    * @param stat the statistic value to validate
    * @throws IllegalArgumentException if the statistic value is invalid
    */
   private static void validateStats(final int stat)
   {
      final int MIN = 0;

      if(stat < MIN)
      {
         throw new IllegalArgumentException("Statistics must not be lower than " + MIN);
      }
   }
}
