package com.unima.risk6.game.ai.models;

import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;

/**
 * A class containing static methods and attributes relating to probabilities and the calculations
 * thereof in the given Game State. Can be used for both the AI's calculations and as
 * decision-making help for the human players.
 * @author eameri
 */
public class Probabilities {
  private int[][] winProbability = new int[20][20]; // initialized here so we can work with it for now

  /**
   * Decide if a specific (bordering) country should be reinforced, depending on the situation
   * in the surrounding continents
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
   * @param player The player which is being tested
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
    return countriesOwned/continent.getCountries().size();
 }


}
