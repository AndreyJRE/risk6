package com.unima.risk6.game.ai.models;


import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;
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
  private static CountryName[] borderCountries;

  /**
   * Initialize the Probabilities class by setting the classes static winProbability and
   * borderCountries attributes
   * @param file The json file containing the 20x20 Win-Probability Matrix
   */
  public static void initProbabilities(File file) {
    winProbability = JsonParser.parseJsonFile(file, Integer[][].class);
    borderCountries = new CountryName[] {CountryName.ALASKA, CountryName.BRAZIL,
        CountryName.CENTRAL_AMERICA, CountryName.GREENLAND, CountryName.ICELAND,
        CountryName.INDONESIA, CountryName.KAMCHATKA, CountryName.MIDDLE_EAST,
        CountryName.NORTH_AFRICA, CountryName.SIAM, CountryName.SOUTHERN_EUROPE,
        CountryName.VENEZUELA, CountryName.WESTERN_EUROPE};
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


  /**
   * Gets the probability of a country winning a battle against another country based off of their
   * amount of troops ("Calculations are done in python, data wrangling in pandas", for more
   * information on the source of the data see: ...)
   * @param attackerTotal The total amount of troops the attacking country has
   * @param defenderTotal The total amount of troops the defending country has
   * @return the probability of the attacker winning an entire battle (rounded, given as an integer)
   */
  public static int getWinProbability(int attackerTotal, int defenderTotal) {
    int attackerIndex = Math.min(attackerTotal - 1, 19);
    int defenderIndex = Math.min(defenderTotal - 1, 19);
    return winProbability[attackerIndex][defenderIndex];
  }

  /**
   * Checks if the given country is a bordering country of a continent
   * @param country The country to be checked
   * @return A boolean value expressing if the country can be attacked from another continent
   */
  public static boolean isBorderCountry(Country country) {
    return Arrays.asList(borderCountries).contains(country.getCountryName());
  }
}
