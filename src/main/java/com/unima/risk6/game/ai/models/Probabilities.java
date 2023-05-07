package com.unima.risk6.game.ai.models;


import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.json.JsonParser;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * A class containing static methods and attributes relating to probabilities and the calculations
 * thereof in the given Game State. Can be used for both the AI's calculations and as
 * decision-making help for the human players.
 *
 * @author eameri
 */
public class Probabilities {

  /**
   * Return the initialized probability array object, strictly for testing purposes
   *
   * @return the win probability array
   */
  public static Integer[][] getWinProbabilityArray() {
    return winProbability;
  }

  private static Integer[][] winProbability;

  /**
   * Initialize the Probabilities class by setting the classes static winProbability and
   * borderCountries attributes.
   *
   * @param fileReader An InputStreamReader pointing to the json file containing the 20x20
   *                   Win-Probability Matrix.
   */
  public static void init(InputStreamReader fileReader) {
    winProbability = JsonParser.parseJsonFile(fileReader, Integer[][].class);
  }

  /**
   * Calculate the percentage of Troops owned by a specific player in a continent.
   *
   * @param player    The player which is being tested.
   * @param continent The continent to be tested.
   * @return The percentage of troops in the continent which belong to the player.
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
    if (troopsTotal == 0) {
      return 0;
    } else {
      return troopsOwned / troopsTotal;
    }
  }


  /**
   * Gets the probability of a country winning a battle against another country based off of their
   * amount of troops ("Calculations are done in python, data wrangling in pandas", for more
   * information on the source of the data see: ...https://www.reddit.com/r/dataisbeautiful/
   * comments/vknu9r/oc_the_probability_of_winning_a_battle_as_an/
   *
   * @param attackerTotal The total amount of troops the attacking country has.
   * @param defenderTotal The total amount of troops the defending country has.
   * @return the probability of the attacker winning an entire battle, rounded and given as an
   * integer.
   */
  public static int getWinProbability(int attackerTotal, int defenderTotal) {
    // attacker always needs to have at least 1 troop
    int attackerAvailable = attackerTotal - 1;
    while (attackerAvailable > 20 && defenderTotal > 20) {
      attackerAvailable -= 20;
      defenderTotal -= 20;
    }
    int attackerIndex = Math.min(attackerAvailable - 1, 19);
    int defenderIndex = Math.min(defenderTotal - 1, 19);
    System.out.print(attackerIndex < 0 ? "Attacker" : "");

    return winProbability[attackerIndex][defenderIndex];
  }

  /**
   * Finds the strongest player in the game based on the ratio of troops they have compared to the
   * amount of all troops.
   *
   * @param gameState The current GameState object containing the game information.
   * @return The strongest player based on their troop control ratio.
   */
  public static Player findStrongestPlayer(GameState gameState) {
    Map<Player, Integer> playerTroopCount = new HashMap<>();
    double totalTroopCount = 0;
    for (Country country : gameState.getCountries()) {
      int countryCount = country.getTroops();
      totalTroopCount += countryCount;
      playerTroopCount.merge(country.getPlayer(), countryCount, Integer::sum);
    }
    Player strongest = null;
    double strongestPercent = 0.0;
    double currentPercent;
    // total troop count can't be 0 when this method is called
    for (Player player : playerTroopCount.keySet()) {
      if ((currentPercent = playerTroopCount.get(player) / totalTroopCount) > strongestPercent) {
        strongestPercent = currentPercent;
        strongest = player;
      }
    }
    return strongest;
  }
}
