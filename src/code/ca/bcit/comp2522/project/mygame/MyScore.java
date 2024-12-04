package ca.bcit.comp2522.project.mygame;

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
 * @version 1.1
 */
public class MyScore
{
   private final String        dateTimePlayed;
   private final int           gameRoundNum;
   private final int           highestScore;
   private final int           highestWinStreak;
   private final int           winStreakNum;

   /**
    * Constructs a Score object with the provided game data.
    *
    * @param dateTimePlayed   the date and time when the game was played.
    * @param gameRoundNum     the number of rounds played in the game.
    * @param highestScore     the highest score achieved during the game.
    * @param highestWinStreak the longest winning streak during the game.
    * @param winStreakNum     the current win streak number.
    * @throws IllegalArgumentException if the provided dateTimePlayed is null.
    */
   public MyScore(final LocalDateTime dateTimePlayed,
                final int gameRoundNum,
                final int highestScore,
                final int highestWinStreak,
                final int winStreakNum)
   {
      validateDateTime(dateTimePlayed);

      final DateTimeFormatter formatter;
      final String            formattedDateTime;

      formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      formattedDateTime = dateTimePlayed.format(formatter);

      this.dateTimePlayed = formattedDateTime;
      this.gameRoundNum = gameRoundNum;
      this.highestScore = highestScore;
      this.highestWinStreak = highestWinStreak;
      this.winStreakNum = winStreakNum;
   }

   /**
    * Returns the date and time when the game was played as a formatted string.
    *
    * @return a string representing the date and time when the game was played.
    */
   public String getDateTimePlayed()
   {
      return dateTimePlayed;
   }

   /**
    * Returns the total number of game rounds played.
    *
    * @return the number of game rounds played.
    */
   public int getNumGamesPlayed()
   {
      return gameRoundNum;
   }

   /**
    * Returns the highest score achieved during the game.
    *
    * @return the highest score achieved.
    */
   public int getNumCorrectFirstAttempt()
   {
      return highestScore;
   }

   /**
    * Returns the highest win streak during the game.
    *
    * @return the longest winning streak.
    */
   public int getNumCorrectSecondAttempt()
   {
      return highestWinStreak;
   }

   /**
    * Returns the number of wins achieved after two attempts.
    *
    * @return the number of wins after two attempts.
    */
   public int getNumIncorrectTwoAttempts()
   {
      return winStreakNum;
   }

   /**
    * Calculates the total score by adding twice the highest score and the highest winning streak.
    *
    * @return the total score, calculated as (highestScore * 2 + highestWinStreak).
    */
   public int getTotalScore()
   {
      return highestScore * 2 + highestWinStreak;
   }

   /**
    * Calculates the average score by dividing the total score by the number of rounds played.
    *
    * @return the average score per round.
    */
   public int getScore()
   {
      return (highestScore * 2 + highestWinStreak) / gameRoundNum;
   }

   /**
    * Appends a {@code Score} object to a file.
    *
    * @param score the score to append
    * @param fileName the name of the file to append to
    * @throws IOException if an I/O error occurs while writing to the file
    */
   public static void appendScoreToFile(final MyScore score,
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
   public static List<MyScore> readScoresFromFile(final String fileName) throws IOException
   {
      final List<MyScore> scoreList;
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
   public static void  addScoreToList(final List<MyScore> scoreList,
                                       final List<String> fileContent)
   {
      final int NEXT_SCORE = 6;
      int j = 1;
      if(scoreList != null && fileContent != null)
      {
         final int size;
         LocalDateTime dateTimePlayed;
         int gameRoundNum;
         int numCorrectFirstGuess;
         int numCorrectSecondGuess;
         int winStreakNum;

         dateTimePlayed = null;
         gameRoundNum = 0;
         numCorrectFirstGuess = 0;
         numCorrectSecondGuess = 0;
         winStreakNum = 0;

         size = fileContent.size();

         for(int i = 0; i < size; i++)
         {
            final MyScore score;
            final String dateTimeStr;
            final String dataStr;

            dataStr = fileContent.get(i);


            if(dataStr.contains("Date and Time:"))
            {
               dateTimeStr = dateTimeParser(dataStr);
               dateTimePlayed = convertStrToLocalDateTime(dateTimeStr);
               j++;
            }
            else if(dataStr.contains("Rounds Played:"))
            {
               j++;
               gameRoundNum = parseDigitFromString(dataStr);
            }
            else if(dataStr.contains("Highest Score:"))
            {
               j++;
               numCorrectFirstGuess = parseDigitFromString(dataStr);
            }
            else if(dataStr.contains("Highest Win Streaks:"))
            {
               j++;
               numCorrectSecondGuess = parseDigitFromString(dataStr);
            }
            else if(dataStr.contains("Number of Win Streaks:"))
            {
               j++;
               winStreakNum = parseDigitFromString(dataStr);
            }
            else {
               throw new IllegalArgumentException("Invalid string format!");
            }

            if(j % NEXT_SCORE == 0)
            {
               score = new MyScore(dateTimePlayed,
                       gameRoundNum,
                       numCorrectFirstGuess,
                       numCorrectSecondGuess,
                       winStreakNum);

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
      sb.append("Rounds Played: ").append(gameRoundNum).append("\n");
      sb.append("Highest Score: ").append(highestScore).append("\n");
      sb.append("Highest Win Streaks: ").append(highestWinStreak).append("\n");
      sb.append("Number of Win Streaks: ").append(winStreakNum).append("\n");

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
         }
         catch(final IOException e)
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

}
