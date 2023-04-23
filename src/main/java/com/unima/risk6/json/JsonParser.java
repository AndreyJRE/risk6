package com.unima.risk6.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.InputStreamReader;

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
   */
  public static <T> T parseJsonFile(InputStreamReader jsonFile, Class<T> clazz) {
    Gson gson = new GsonBuilder().create();
    return gson.fromJson(jsonFile,
        clazz);
  }

}
