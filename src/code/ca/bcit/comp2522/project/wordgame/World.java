package ca.bcit.comp2522.project.wordgame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The {@code World} class represents a collection of {@link Country} objects,
 * which are loaded from text files in a specified directory. Each file contains
 * information about countries, including their names, capitals, and facts.
 * The class provides methods to retrieve the list of countries and access them
 * via a map where the country name is the key.
 *
 * <p>This class is responsible for:
 * <ul>
 *   <li>Reading the content of files from the specified directory.</li>
 *   <li>Parsing the content to create {@link Country} objects.</li>
 *   <li>Storing the parsed countries in a {@link Map} for easy lookup.</li>
 * </ul>
 *
 * @author Linh Hoang
 * @version 1.0
 */
public class World
{
   final Map<String, Country> worldMap;

   /**
    * Constructs a {@code World} object by reading country data from files in
    * the "src/resources" directory and storing the countries in the.
    */
   public World()
   {
      final Path srcPath;
      final List<String> fileContent;
      final List<Country> countryList;

      worldMap = new HashMap<>();

      srcPath = Paths.get("src", "resources");
      fileContent = new ArrayList<>();

      try(final Stream<Path> filePath = Files.walk(srcPath))
      {
         filePath.filter(Files::isRegularFile)     // Filter only regular files, not directories
                    .forEach(path -> addFileContentToList(path, fileContent));
      } catch(final IOException e)
      {
         System.out.println("File not found! " + e.getMessage());
      }


      if(fileContent != null)
      {
         countryList = instantiateCountry(fileContent);
         putCountryToMap(countryList, worldMap);
      }
   }

   /**
    * Retrieves the map of countries where the key is the country name and the
    * value is the corresponding {@link Country} object.
    *
    * @return a map containing country names and their respective {@link Country} objects
    */
   public Map<String, Country> getWorldMap()
   {
      return worldMap;
   }

   /**
    * Retrieves a list of all countries contained in the world map.
    *
    * @return a list of all {@link Country} objects in the world map
    */
   public List<Country> getCountryList()
   {
      final List<Country> result;
      final Set<String> keySet;

      result = new ArrayList<>();
      keySet = worldMap.keySet();
      keySet.forEach(key -> result.add(worldMap.get((key))));

      return  result;
   }

   /**
    * Instantiates {@link Country} objects by parsing the provided file content.
    * Each country is initialized with its name, capital, and up to three facts.
    *
    * @param parsedFileContent a list of strings representing the content of the files
    * @return a list of {@link Country} objects
    */
   private static List<Country> instantiateCountry(final List<String> parsedFileContent)
   {
      final List<Country> countryList;
      final int size;
      final int skipToNextCountry;
      final int skipToFacts;
      final int firstSplit;
      final int secondSplit;
      int nextCountryIndex;

      countryList = new ArrayList<>();
      size = parsedFileContent.size();
      skipToNextCountry = 4;
      nextCountryIndex = 0;
      skipToFacts = 1;
      firstSplit = 0;
      secondSplit = 1;

      for(int i = 0; i < size; i += skipToNextCountry)
      {
         nextCountryIndex += skipToNextCountry;

         if(parsedFileContent.get(i) != null && !parsedFileContent.get(i).isBlank())
         {
            final Country country;
            final String[] splitByColon;
            String countryName;
            String capital;
            final String fact1;
            final String fact2;
            final String fact3;
            int factLine;

            countryName = "";
            capital = "";
            factLine = i + skipToFacts;

            if(parsedFileContent.get(i).contains(":"))
            {
               splitByColon = parsedFileContent.get(i).split(":");

               if(splitByColon.length == 2)
               {
                  countryName = splitByColon[firstSplit].trim();
                  capital = splitByColon[secondSplit].trim();
               }
            }

            if(factLine < size && parsedFileContent.get(factLine) != null &&
                                 !parsedFileContent.get(factLine).isBlank())
            {
               fact1 = parsedFileContent.get(factLine++).trim();
            } else
            {
               fact1 = "";
            }

            if(factLine < size && parsedFileContent.get(factLine) != null &&
                                       !parsedFileContent.get(factLine).isBlank())
            {
               fact2 = parsedFileContent.get(factLine++).trim();
            } else
            {
               fact2 = "";
            }

            if(factLine < size && parsedFileContent.get(factLine) != null &&
                                 !parsedFileContent.get(factLine).isBlank())
            {
               fact3 = parsedFileContent.get(factLine).trim();
            } else
            {
               fact3 = "";
            }

            country = new Country(countryName, capital, fact1, fact2, fact3);
            countryList.add(country);
         }
      }

      return countryList;
   }

   /**
    * Reads the content of a file and adds it to the provided list.
    *
    * @param path the path to the file to read
    * @param list the list to store the file content
    */
   private static void addFileContentToList(final Path path, final List<String> list)
   {
      try(final Stream<String> content = Files.lines(path))
      {
         content.filter(s -> s != null)
                 .filter(s -> !s.isBlank())
                 .forEach(list::add);
      } catch(final IOException e)
      {
         System.out.println("Error reading file " + path.getFileName() + ", " + e.getMessage());
      }
   }

   /**
    * Adds a list of countries to the specified map.
    *
    * @param countryList the list of countries to add
    * @param map the map to store the countries in
    */
   private static void putCountryToMap(final List<Country> countryList,
                                       final Map<String, Country> map)
   {
      if(countryList != null)
      {
         final Iterator<Country> it;

         it = countryList.iterator();

         while(it.hasNext())
         {
            final Country country;

            country = it.next();

            if(country != null)
            {
               map.put(country.getName(), country);
            }
         }
      }
   }

   /**
    * The main method, which creates an instance of the {@code World} class.
    *
    * @param args command-line arguments (not used)
    */
   public static void main(final String[] args)
   {
      final World w = new World();

   }
}
