package com.unima.risk6.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Implementation of parser to wrapper class for json file
 *
 * @author astoyano
 */
public class JsonParser {

  /**
   * Parses a JSON file and maps it to an object of the specified class.
   *
   * @param <T>      the type of the object to be returned
   * @param jsonFile the JSON file to be parsed
   * @param clazz    the class of the object to be returned
   * @return an object of type T, created from the parsed JSON file
   * @throws RuntimeException if the specified JSON file is not found
   */
  public static <T> T parseJsonFile(File jsonFile, Class<T> clazz) {
    Gson gson = new GsonBuilder().create();
    try {
      T countryJsonObjects = gson.fromJson(new FileReader(jsonFile),
          clazz);
      return countryJsonObjects;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
