package com.unima.risk6.game.json;

import com.google.gson.GsonBuilder;
import com.unima.risk6.game.json.jsonObjects.CountryJsonObject;
import java.io.File;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Implementation of parser to wrapper class for json file
 *
 * @author astoyano
 */
public class JsonParser {


  /**
   * Parse a file with gson google library to java object
   *
   * @param jsonFile
   * @return An array of wrapper class CountryJsonObject
   */
  public static CountryJsonObject[] parseJsonFile(File jsonFile) {
    Gson gson = new GsonBuilder().create();
    try {
      CountryJsonObject[] countryJsonObjects = gson.fromJson(new FileReader(jsonFile),
          CountryJsonObject[].class);
      return countryJsonObjects;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
