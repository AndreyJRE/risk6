package com.unima.risk6.game.ai.models;

import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.json.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * A class containing static methods and attributes relating to probabilities and the calculations
 * thereof in the given Game State. Can be used for both the AI's calculations and as
 * decision-making help for the human players.
 *
 * @author eameri
 */
public class Probabilities {

  private static Integer[][] winProbability;

  public static void initProbabilities(String probabilityFilePath) { // put in big config file later
    // maybe
    InputStream inputStream = Probabilities.class.getResourceAsStream(probabilityFilePath);
    try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
      winProbability = JsonParser.parseJsonFile(reader, Integer[][].class);
    } catch (IOException e) {
      throw new RuntimeException();
    }

  }

  /**
   * Decide if a specific (bordering) country should be reinforced, depending on the situation in
   * the surrounding continents
   *
   * @param country The country to be reinforced
   * @return If it is worth it to reinforce this country or not
   */
  public static boolean shouldReinforceBorder(Country country) {
    //
    return false;
  }

  /**
   * Calculate the percentage of countries in a continent owned by a specific player
   *
   * @param player    The player which is being tested
   * @param continent The continent to be tested
   * @return The percentage of countries of a continent which belong to the player
   * @author eameri
   */
  public static double relativePower(Player player, Continent continent) {
    double countriesOwned = 0.0;
    for (Country country : continent.getCountries()) {
      if (player.equals(country.getPlayer())) {
        countriesOwned++;
      }
    }
    return countriesOwned / continent.getCountries().size();
  }


  public static int getWinProbability(int attackerTotal, int defenderTotal) {
    int attackerIndex = Math.min(attackerTotal - 1, 19);
    int defenderIndex = Math.min(defenderTotal - 1, 19);
    return winProbability[attackerIndex][defenderIndex];
  }
}
