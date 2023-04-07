package com.unima.risk6.game.ai.models;

import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.json.JsonParser;
import java.io.File;
import java.util.Arrays;

/**
 * A class containing static methods and attributes relating to probabilities and the calculations
 * thereof in the given Game State. Can be used for both the AI's calculations and as
 * decision-making help for the human players.
 *
 * @author eameri
 */
public class Probabilities {

  private static Integer[][] winProbability;
  private static String[] borderCountries;

  public static void initProbabilities(File file) { // put in big config file later maybe
    winProbability = JsonParser.parseJsonFile(file, Integer[][].class);
    borderCountries = new String[] {"ALASKA", "BRAZIL", "CENTRAL_AMERICA", "GREENLAND",
        "ICELAND", "INDONESIA", "KAMCHATKA", "MIDDLE_EAST", "NORTH_AFRICA", "SIAM",
        "SOUTHERN_EUROPE", "VENEZUELA", "WESTERN_EUROPE"};
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
   * Calculate the percentage of Troops owned by a specific player in a continent
   * @param player    The player which is being tested
   * @param continent The continent to be tested
   * @return The percentage of troops in the continent which belong to the player
   * @author eameri
   */
  public static double relativeTroopContinentPower(Player player, Continent continent) {
    double troopsOwned = 0.0;
    double troopsTotal = 0.0;
    for (Country country : continent.getCountries()) {
      if (player.equals(country.getPlayer())) {
        troopsOwned += country.getTroops();
      }
      troopsTotal += country.getTroops();
    }
    return troopsOwned / troopsTotal;
  }


  public static int getWinProbability(int attackerTotal, int defenderTotal) {
    int attackerIndex = Math.min(attackerTotal - 1, 19);
    int defenderIndex = Math.min(defenderTotal - 1, 19);
    return winProbability[attackerIndex][defenderIndex];
  }

  public boolean isBorderCountry(Country country) {
    return Arrays.stream(borderCountries).anyMatch(country.getCountryName()::equals);
  }
}
