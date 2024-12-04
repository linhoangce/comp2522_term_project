package ca.bcit.comp2522.project.wordgame;

/**
 * The {@code Country} class represents a country with its name, capital city,
 * and a set of facts associated with the country. It provides methods to retrieve
 * the name of the country, the capital city, and a list of facts about the country.
 * <p>
 *    This class ensures that the name and capital city are validated to ensure
 *    they are not empty or blank. The facts are stored in a fixed-length array.
 * </p>
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class Country
{
   public static final int DEFAULT_LENGTH = 3;

   private final String name;
   private final String capitalCityName;
   private final String[] facts;

   /**
    * Constructs a new {@code Country} object with the specified name, capital city,
    * and three facts associated with the country.
    * <p>
    *    Both the country name and capital city name are validated to ensure they are not null or blank.
    *    The facts array is initialized with the provided facts.
    * </p>
    *
    * @param name The name of the country.
    * @param capitalCityName The name of the capital city of the country.
    * @param fact1 A fact about the country.
    * @param fact2 A second fact about the country.
    * @param fact3 A third fact about the country.
    * @throws IllegalArgumentException If the name or capital city name is null or blank.
    */
   public Country(final String name,
                  final String capitalCityName,
                  final String fact1,
                  final String fact2,
                  final String fact3)
   {
      validateName(name);
      validateName(capitalCityName);

      this.name = name;
      this.capitalCityName = capitalCityName;
      facts = new String[DEFAULT_LENGTH];

      facts[0] = fact1;    // TEMPORARY
      facts[1] = fact2;
      facts[2] = fact3;
   }

   /**
    * Returns the name of the country.
    *
    * @return The name of the country.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Returns the name of the capital city of the country.
    *
    * @return The name of the capital city.
    */
   public String getCapitalCityName()
   {
      return capitalCityName;
   }

   /**
    * Returns the array of facts associated with the country.
    *
    * @return An array of facts about the country.
    */
   public String[] getFacts()
   {
      return facts;
   }

   /**
    * Returns a string representation of the country, including the name, capital city,
    * and the facts associated with the country.
    *
    * @return A string representation of the country.
    */
   public String toString()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append(name).append(System.lineSeparator());
      sb.append(capitalCityName).append(System.lineSeparator());

      for(final String fact : facts)
      {
         sb.append(fact).append(System.lineSeparator());
      }

      return sb.toString();
   }

   /*
    * Validates that the provided name is neither null nor blank.
    *
    * @param name The name to validate.
    * @throws IllegalArgumentException If the name is null or blank.
    */
   private static void validateName(final String name)
   {
      if(name == null || name.isBlank())
      {
         throw new IllegalArgumentException("Name must not be empty!");
      }
   }
}
