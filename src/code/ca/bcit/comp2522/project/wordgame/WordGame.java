package ca.bcit.comp2522.project.wordgame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * The {@code WordGame} class implements the logic for a word guessing game where players
 * try to guess the country or capital city based on given clues. The game provides multiple
 * types of questions based on the country and capital city, tracks the player's score,
 * and stores the highest scores in a file.
 * <p>
 * The game gives players the option to guess either the capital city of a country,
 * the name of the country from its capital city, or guess the country based on a fact.
 * The scoring system awards points based on correct answers in the first and second attempts.
 * </p>
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class WordGame
{
   public static final int OPTION_A      = 1;
   public static final int OPTION_B      = 2;
   public static final int OPTION_C      = 3;
   public static final int REPORT_CYCLE  = 10;
   public static final int DATE_TIME_STR = 2;
   public static final String SCORE_FILE = "score.txt";

   /**
    * Main method to start and control the flow of the Word Game. It runs the game, tracks scores,
    * checks for new high scores, and writes the score history to a file.
    *
    * @param args Command line arguments (not used in this implementation).
    */
   public static void main(final String[] args)
   {
      final World world;
      final Map<String, Country> worldMap;
      final List<Country> countryList;
      final List<Score> scoreHistory;
      final Optional<Score> prevMaxScore;
      String[] dateTimeStr;
      final Score score;
      final Scanner scanner;
      final boolean isNewMax;
      final int date;
      final int time;

      String continuePlaying;
      int numGamesPlayed;
      int numCorrectFirstGuess;
      int numCorrectSecondGuess;
      int numIncorrectGuessTwoAttempts;
      String userAnswer;


      world = new World();
      worldMap = world.getWorldMap();
      countryList = world.getCountryList();
      scanner = new Scanner(System.in);
      dateTimeStr = new String[DATE_TIME_STR];
      numGamesPlayed = 0;
      numCorrectFirstGuess = 0;
      numCorrectSecondGuess = 0;
      numIncorrectGuessTwoAttempts = 0;

      System.out.println("Welcome to Word Game!");
      System.out.println("Here are the rules: ");
      System.out.println("a) I will give you the name of a capital city," +
              " and ask you to guess the name of the country.");
      System.out.println("b) I will give you the name of a country, " +
              "and ask you to guess the name of the capital city.");
      System.out.println("c) I will give one of the three facts about a country, " +
              "and ask you to guess the name of the country.");
      System.out.println("Here is how your scores are determined:");
      System.out.println("If your first guess is correct, you get 2 points. " +
              "On second guess, 1 point. No point for incorrect guesses.");
      System.out.println("Let's begin!");

      do
      {
         boolean isCorrect;
         int round;

         round = 0;

         numGamesPlayed++;

         while(round < REPORT_CYCLE)
         {
            final Random rand;
            final int option;
            final Country country;

            rand = new Random();
            option = rand.nextInt(1, 4);
            country = playGame(countryList, option);

            System.out.println("Enter your answer:");
            userAnswer = scanner.nextLine();
            isCorrect  = checkAnswer(country, userAnswer, option);

            if(isCorrect)
            {
               System.out.println("Correct! You earned 2 points.");
               numCorrectFirstGuess++;
            } else
            {
               System.out.println("Incorrect! You have one more guess...");
               userAnswer = scanner.nextLine();

               isCorrect = checkAnswer(country, userAnswer, option);

               if(isCorrect)
               {
                  System.out.println("Correct! You earned 1 point.");
                  numCorrectSecondGuess++;
               } else
               {
                  System.out.println("The correct answer is " + answerForIncorrect(country, option));
                  numIncorrectGuessTwoAttempts++;
               }
            }

            round++;
         }

         System.out.println(numGamesPlayed + " word game played");
         System.out.println(numCorrectFirstGuess + " correct answer on the first attempt");
         System.out.println(numCorrectSecondGuess + " correct answer on the second attempt");
         System.out.println(numIncorrectGuessTwoAttempts + " incorrect answers on two attempts each");

         System.out.println("Would you like to continue playing Word Game? [Yes/No]");
         continuePlaying = scanner.nextLine();

         while(!continuePlaying.equalsIgnoreCase("yes") &&
               !continuePlaying.equalsIgnoreCase("no"))
         {
            System.out.println("Please provide a 'Yes' or 'No'. " +
                    "Would you like to continue playing Word Game?");
            continuePlaying = scanner.nextLine();
         }

         if(continuePlaying.equalsIgnoreCase("no"))
         {
            System.out.println("Thank you for playing. Bye!");
            break;
         }
      } while(continuePlaying.equalsIgnoreCase("yes"));

      score = new Score(LocalDateTime.now(), numGamesPlayed, numCorrectFirstGuess, numCorrectSecondGuess, numIncorrectGuessTwoAttempts);

      isNewMax = checkMaxScore(score);
      prevMaxScore = getMaxScoreHistory();
      date = 0;
      time = 1;

      if(prevMaxScore.isPresent())
      {
         final double maxScore;

         maxScore = prevMaxScore.get().getScore();
         dateTimeStr = parseDateTime(prevMaxScore.get().getDateTimePlayed());

         if(isNewMax)
         {
            final StringBuilder sb;


            sb = new StringBuilder();

            sb.append("CONGRATULATIONS! You are the new high score with an average of ");
            sb.append(score.getScore()).append("; the previous score was ");
            sb.append(maxScore).append(" points per game on ").append(dateTimeStr[date]);
            sb.append(" at ").append(dateTimeStr[time]);

            System.out.println(sb.toString());
         } else
         {
            final StringBuilder sb;

            sb = new StringBuilder();

            sb.append("You did not beat the high score of ").append(maxScore);
            sb.append(" points per game from ").append(dateTimeStr[date]);
            sb.append(" at ").append(dateTimeStr[time]);

            System.out.println(sb.toString());
         }
      }

      try
      {
         Score.appendScoreToFile(score, SCORE_FILE);
      } catch(final IOException e)
      {
         System.out.println("Error appending score to file " + e.getMessage());
      }

      scanner.close();
   }

   /*
    * Plays a round of the game by selecting a random country and asking the player a question.
    *
    * @param countryList A list of available countries to choose from.
    * @param option The type of question to ask (capital, country name, or fact).
    * @return The randomly selected country for this round.
    */
   private static Country playGame(final List<Country> countryList, final int option)
   {
      final Random random;
      final int countryIndex;
      final int size;
      final Country country;
      final String countryName;
      final String capitalCity;
      final String fact;
      final int randomFact;

      random = new Random();
      size = countryList.size();
      countryIndex = random.nextInt(size);
      country = countryList.get(countryIndex);
      countryName = country.getName();
      randomFact = random.nextInt(country.getFacts().length);
      capitalCity = country.getCapitalCityName();
      fact = country.getFacts()[randomFact];

      if(countryList != null)
      {
         switch(option)
         {
            case OPTION_A -> System.out.println("Which country has this capital city? " + capitalCity);
            case OPTION_B -> System.out.println("What is the name of this country's capital city? " +
                    countryName);
            case OPTION_C ->
            {
               System.out.println("Here is a fact about this country: " + fact);
               System.out.println("What country is this?");
            }
            default -> System.out.println("Invalid option!");
         }
      }

      return country;
   }

   /*
    * Checks the player's answer against the correct answer for the given country and question type.
    *
    * @param country The country to compare against.
    * @param answer The player's answer.
    * @param option The type of question asked (capital, country name, or fact).
    * @return {@code true} if the answer is correct, {@code false} otherwise.
    */
   private static boolean checkAnswer(final Country country, final String answer, final int option)
   {
      if(country != null)
      {
         switch(option)
         {
            case OPTION_A, OPTION_C ->
            {
               return answer.trim().equalsIgnoreCase(country.getName());
            }
            case OPTION_B ->
            {
               return answer.trim().equalsIgnoreCase(country.getCapitalCityName());
            }
            default -> System.out.println("Invalid option!");
         }
      }

      return false;
   }

   /*
    * Returns the correct answer for an incorrect guess.
    *
    * @param country The country being questioned.
    * @param option The type of question asked.
    * @return The correct answer based on the question type.
    */
   private static String answerForIncorrect(final Country country, final int option)
   {
      String result;

      result = null;

      if(country != null)
      {
         switch(option)
         {
            case OPTION_A, OPTION_C -> result = country.getName();
            case OPTION_B -> result = country.getCapitalCityName();
            default -> System.out.println("Invalid option!");
         }
      }

      return result;
   }

   /*
    * Reads the score history from the score file and returns a list of scores.
    *
    * @param content A list of strings representing the contents of the score file.
    * @return A list of {@code Score} objects read from the file.
    * @throws IOException If there is an error reading the file.
    */
   private static List<Score> listScoreInFile(final List<String> content)
                                             throws IOException
   {
      final List<Score> scoreList;
      final List<String> fileContent;
      final Path filePath;

      scoreList = new ArrayList<>();
      fileContent = new ArrayList<>();

      filePath = Score.createOutputFile(SCORE_FILE);

      if(Files.notExists(filePath))
      {
         System.out.println("File does not exist " + SCORE_FILE);
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

      Score.addScoreToList(scoreList, fileContent);
      return scoreList;
   }

   /*
    * Checks whether the current score is greater than the highest score recorded in the file.
    *
    * @param currentScore The score to check against the high score.
    * @return {@code true} if the current score is a new high score, {@code false} otherwise.
    */
   private static boolean checkMaxScore(final Score currentScore)
   {
      List<Score> scoreList;
      final Optional<Score> maxScore;
      double max;

      max = 0.0;
      scoreList = new ArrayList<>();

      try
      {
         scoreList = Score.readScoresFromFile(SCORE_FILE);
      } catch(final IOException e)
      {
         System.out.println("Error reading scores from file " + e);
      }

      maxScore = scoreList.stream().max(Comparator.comparing(Score::getScore));

      if(currentScore == null)
      {
         System.out.println("Cannot compare due to invalid current score, " + currentScore);
      }

      if(maxScore.isPresent())
      {
         max = maxScore.get().getScore();
      }

      return max < currentScore.getScore();
   }

   /*
    * Retrieves the highest score history stored in the score file.
    *
    * @return An {@code Optional} containing the highest score, or {@code empty} if no score exists.
    */
   private static Optional<Score> getMaxScoreHistory()
   {
      List<Score> scoreList;
      final Optional<Score> max;

      scoreList = new ArrayList<>();

      try
      {
         scoreList = Score.readScoresFromFile(SCORE_FILE);
      } catch(final IOException e)
      {
         System.out.println("Error reading scores from file " + e);
      }

      max = scoreList.stream().max(Comparator.comparing(Score::getScore));

      return max;
   }

   /*
    * Parses a date-time string and splits it into date and time components.
    *
    * @param dateTime The date-time string to parse.
    * @return An array of strings containing the date and time components.
    */
   private static String[] parseDateTime(final String dateTime)
   {
      final String[] result;

      result = dateTime.split(" ");

      return result;
   }

}
