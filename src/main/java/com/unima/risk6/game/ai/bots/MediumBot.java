package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.logic.GameState;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The medium difficulty bot, which makes only the best move in each round without analyzing
 * possible future states of the game (greedy).
 *
 * @author eameri
 */
public class MediumBot extends Player implements AiBot {

  private final PlayerController playerController;
  private final List<Continent> continentsCopy;


  public MediumBot(GameState gameState) {
    playerController = new PlayerController(this, gameState);
    continentsCopy = new ArrayList<>();
    continentsCopy.addAll(gameState.getContinents());
  }

  /**
   * A method for a bot to make moves for all 3 phases of the game
   */
  @Override
  public void makeMove() {
    if (this.numberOfCountries() == 0) { // unable to make a move if bot is out of the game.
      return;
    }
    this.createAllReinforcements();
    this.createAllAttacks();
//    this.nextPhase(); // use player-controller later?
    this.createFortify();
//    this.nextPhase();
  }

  /**
   * A method for a bot to claim a single country during the CLAIM PHASE Game State. The MediumBot
   * attempts to claim countries based off of a ranking of continents.
   */
  @Override
  public void claimCountry() {
    Map<ContinentName, Continent> continentMap = new HashMap<>();
    this.playerController.getGameState().getContinents()
        .forEach(cont -> continentMap.put(cont.getContinentName(), cont));

    for (Country australia : continentMap.get(ContinentName.AUSTRALIA).getCountries()) {
      if (!australia.hasPlayer()) {
        this.playerController.sendReinforce(australia, 1);
        return;
      }
    }
    for (Country southAmerica : continentMap.get(ContinentName.SOUTH_AMERICA).getCountries()) {
      if (!southAmerica.hasPlayer()) {
        this.playerController.sendReinforce(southAmerica, 1);
        return;
      }
    }
    for (Country northAmerica : continentMap.get(ContinentName.NORTH_AMERICA).getCountries()) {
      if (!northAmerica.hasPlayer()) {
        this.playerController.sendReinforce(northAmerica, 1);
        return;
      }
    }
    for (Country africa : continentMap.get(ContinentName.AFRICA).getCountries()) {
      if (!africa.hasPlayer()) {
        this.playerController.sendReinforce(africa, 1);
        return;
      }
    }
    for (Country europe : continentMap.get(ContinentName.EUROPE).getCountries()) {
      if (!europe.hasPlayer()) {
        this.playerController.sendReinforce(europe, 1);
        return;
      }
    }
    for (Country asia : continentMap.get(ContinentName.ASIA).getCountries()) {
      if (!asia.hasPlayer()) {
        this.playerController.sendReinforce(asia, 1);
        return;
      }
    }
  }

  /**
   * Reinforces based off of weighted continent importance, focusing on preventing an easy loss of a
   * country and on securing borders of continents which are 100% controlled
   *
   * @author eameri
   */
  private void createAllReinforcements() {
    sortContinentsByHighestRelativePower();
    // reinforce defensively
    for (Continent continent : this.continentsCopy) {
      // if country owned, only reinforce border
      if (this.getDeployableTroops() > 0) {
        makeContinentDefendable(continent);
      } else {
        break;
      }
    }

    for (Continent continent : this.continentsCopy) {
      if (this.getDeployableTroops() > 0
          && Probabilities.relativeTroopContinentPower(this, continent) != 1.0) {
        aggressiveReinforce(continent);
      } else {
        break;
      }
    }

  }

  /**
   * Aggressive reinforcement - finds the strongest country on the continent and reinforces it as
   * much as possible. This would make the country be the best choice for an attack move in the next
   * phase
   *
   * @param continent The continent on which the reinforce move is being made
   */
  private void aggressiveReinforce(Continent continent) {
    Map<Country, Integer> ownedCountryDiffs = getCountryTroopDiffsByContinent(continent);
    List<Country> countriesByLowestReinforce = new ArrayList<>(ownedCountryDiffs.keySet());
    sortCountriesByLowestDiffs(ownedCountryDiffs, countriesByLowestReinforce);
    // now the country earliest in list is the one with the highest chance of winning.
    // order works, need to invert values to make them positive
    ownedCountryDiffs.replaceAll((country, diff) -> -diff);
    reinforceSortedCountryList(ownedCountryDiffs, countriesByLowestReinforce);
  }

  /**
   * Sorts the local list of continents in a descending manner based off of how much total control/
   * presence the bot has over a continent
   */
  private void sortContinentsByHighestRelativePower() {
    this.continentsCopy.sort((continent1, continent2) -> {
      double power1 = Probabilities.relativeTroopContinentPower(this, continent1);
      double power2 = Probabilities.relativeTroopContinentPower(this, continent2);
      return (int) ((power2 - power1) * 100);
    });
  }

  /**
   * Makes the countries of a continent more likely to survive an attack by attempting to reinforce
   * each country by an amount necessary to even out the number of troops of the current country
   * with that of the most powerful adjacent enemy country
   *
   * @param continent The continent to be reinforced
   */
  private void makeContinentDefendable(Continent continent) {
    Map<Country, Integer> ownedCountryDiffs = getCountryTroopDiffsByContinent(continent);
    List<Country> countriesByLowestReinforce = new ArrayList<>(ownedCountryDiffs.keySet());
    sortCountriesByLowestDiffs(ownedCountryDiffs, countriesByLowestReinforce);
    reinforceSortedCountryList(ownedCountryDiffs, countriesByLowestReinforce);
  }

  /**
   * Reinforces countries in the given order by the highest of either the amount of troops that can
   * be deployed or the amount of troops needed to even out the country in comparison to its
   * neighbours
   *
   * @param ownedCountryDiffs A mapping of each country to the amount of troops it needs to be
   *                          reinforced with
   * @param sortedCountryList A sorted list of countries, in the order that the reinforce should be
   *                          performed
   */
  private void reinforceSortedCountryList(Map<Country, Integer> ownedCountryDiffs,
      List<Country> sortedCountryList) {
    for (Country country : sortedCountryList) {
      if (this.getDeployableTroops() > 0 && ownedCountryDiffs.get(country) > 0) {
        int amountDeployed = Math.min(this.getDeployableTroops(), ownedCountryDiffs.get(country));
        this.playerController.sendReinforce(country, amountDeployed);
        this.playerController.changeDeployableTroops(-amountDeployed);
      } else {
        break;
      }
    }
  }

  /**
   * Sorts a list of countries in an ascending manner based off of the amount of troops defined by
   * the mapping given to the method
   *
   * @param ownedCountryDiffs A mapping of countries to the amount of troops needed to even out the
   *                          troop number with that of the strongest neighbouring enemy
   * @param listToSort        The list of countries which is to be sorted
   */
  private static void sortCountriesByLowestDiffs(Map<Country, Integer> ownedCountryDiffs,
      List<Country> listToSort) {
    listToSort.sort((country1, country2) -> {
      int country1Count = ownedCountryDiffs.get(country1);
      int country2Count = ownedCountryDiffs.get(country2);
      return country1Count - country2Count;
    });
  }

  /**
   * Creates a mapping of countries from a continent to the amount of additional troops needed in
   * order to balance out their strength with that of their strongest enemy country
   *
   * @param continent The continent whose countries are being tested
   * @return A map of countries to the additionally necessary amount of troops. Countries without an
   * enemy adjacent country are not included in this map
   */
  private Map<Country, Integer> getCountryTroopDiffsByContinent(Continent continent) {
    Map<Country, Integer> ownedCountryDiffs = new HashMap<>();
    for (Country country : continent.getCountries()) {
      if (this.equals(country.getPlayer())) {
        ownedCountryDiffs.put(country, Integer.MIN_VALUE);
        for (Country adj : country.getAdjacentCountries()) {
          if (!this.equals(adj.getPlayer())) {
            ownedCountryDiffs.put(country,
                Math.max(calculateTroopWeakness(country, adj), ownedCountryDiffs.get(country)));
          }
        }
        if (ownedCountryDiffs.get(country) == Integer.MIN_VALUE) {
          ownedCountryDiffs.remove(country);
        }
      }
    }
    return ownedCountryDiffs;
  }

  /**
   * Calculates how many additional troops an adjacent country has in comparison to the current
   * country
   *
   * @param country The country being tested
   * @param adj     One adjacent country
   * @return The amount of troops country needs to have the same amount as adj
   */
  private int calculateTroopWeakness(Country country, Country adj) {
    return adj.getTroops() - country.getTroops();
  }

  /**
   * In order of strongest continents, makes the strongest attack moves which have high chances of
   * victory
   */
  private void createAllAttacks() {
    sortContinentsByHighestRelativePower();
    for (Continent continent : continentsCopy) {
      makeBestAttackInContinent(continent);
    }
  }

  /**
   * Grabs all possible attack moves in the current continent, and performs only those which have a
   * high probability of victory until one side loses.
   *
   * @param continent The continent in which attacks will be performed.
   */
  private void makeBestAttackInContinent(Continent continent) {
    Map<Country, List<Country>> allPossibleAttacks = new HashMap<>();
    for (Country country : continent.getCountries()) {
      if (this.equals(country.getPlayer())) {
        List<Country> attacksFromHere = this.playerController.getValidAttackFromCountry(country);
        if (attacksFromHere.size() > 0) {
          allPossibleAttacks.put(country, attacksFromHere);
        }
      }
    }
    List<List<Country>> bestAttacks = rateAttacks(allPossibleAttacks);

    for (List<Country> attackPair : bestAttacks) {
      Country attacker = attackPair.get(0);
      Country defender = attackPair.get(1);
      if (this.getWinningProbability(attacker, defender) > 70) {
        // how to check if country defeated?
        // temp solution: attack isn't done until owner of either attacker or defender country
        // changes
        while (this.equals(attacker.getPlayer()) && !this.equals(defender.getPlayer())) {
          this.playerController.sendAttack(attacker, defender,
              Math.min(3, attacker.getTroops() - 1));
        }
      } else {
        break;
      }
    }
  }

  /**
   * Rates a list of possible Attacks by win probability
   *
   * @param allAttacks A map of all possible attacks from one or more source countries to their
   *                   adjacent countries
   * @return A descending sorted list of pairs of countries (attacking, defending), sorted by
   * probability of the attacking country winning the battle
   */
  private List<List<Country>> rateAttacks(Map<Country, List<Country>> allAttacks) {
    List<List<Country>> highestWinCountryPairs = extractAttackPairs(allAttacks);
    this.sortAttacksByProbability(highestWinCountryPairs);
    return highestWinCountryPairs;
  }

  /**
   * From a map of attacks built as "Attacking Country" : "List of attackable countries", extract
   * all pairs of (attacking, defending) countries.
   *
   * @param allAttacks The map containing all attack moves to be extracted
   * @return A list of pairs of countries built as (attacking, defending)
   */
  private List<List<Country>> extractAttackPairs(Map<Country, List<Country>> allAttacks) {
    List<List<Country>> extracted = new ArrayList<>();
    for (Country attacker : allAttacks.keySet()) {
      for (Country defender : allAttacks.get(attacker)) {
        List<Country> attackPair = new ArrayList<>();
        attackPair.add(attacker);
        attackPair.add(defender);
        extracted.add(attackPair);
      }
    }
    return extracted;
  }

  /**
   * Sorts a list of pairs of countries descending by the probability of the attacking country
   * (first entry) winning a battle against the defending country (second entry)
   *
   * @param unsortedPairs The general unsorted list of possible attack moves
   */
  private void sortAttacksByProbability(List<List<Country>> unsortedPairs) {
    unsortedPairs.sort((pair1, pair2) -> {
      int probPair1 = getWinningProbability(pair1.get(0), pair1.get(1));
      int probPair2 = getWinningProbability(pair2.get(0), pair2.get(1));
      return probPair2 - probPair1;
    });
  }

  /**
   * From the weakest countries, only fortifies the weakest one which would also result in the
   * lowest loss of strength in surrounding territories
   */
  private void createFortify() {
    sortContinentsByHighestRelativePower();
    Map<Country, Integer> allOwnedCountryDiffs = new HashMap<>();
    for (Continent continent : continentsCopy) {
      allOwnedCountryDiffs.putAll(getCountryTroopDiffsByContinent(continent));
    }
    List<Country> countriesByHighestDiff = new ArrayList<>(allOwnedCountryDiffs.keySet());
    countriesByHighestDiff.sort((country1, country2) -> {
      int country1Count = allOwnedCountryDiffs.get(country1);
      int country2Count = allOwnedCountryDiffs.get(country2);
      return country2Count - country1Count;
    });

    for (Country country : countriesByHighestDiff) {
      Country bestAdj = null;
      for (Country adj : country.getAdjacentCountries()) {
        if (this.equals(adj.getPlayer()) && adj.getTroops() >= country.getTroops()
            && allOwnedCountryDiffs.get(adj) < 0) {
          if (bestAdj == null || adj.getTroops() > bestAdj.getTroops()
              || allOwnedCountryDiffs.get(adj) < allOwnedCountryDiffs.get(bestAdj)) {
            bestAdj = adj;
          }
        }
      }
      if (bestAdj != null) {
        this.playerController.sendFortify(bestAdj, country,
            Math.min((bestAdj.getTroops() + country.getTroops()) / 2,
                -allOwnedCountryDiffs.get(bestAdj)));
        break;
      }
    }
  }


  /**
   * Gets the probability of a country winning an entire battle against another country
   *
   * @param attacker The country initiating the battle
   * @param defender The country which is to defend in the battle
   * @return The probability of the attacking country winning the entire battle
   */
  private int getWinningProbability(Country attacker, Country defender) {
    int attackerCount = attacker.getTroops();
    int defenderCount = defender.getTroops();
    return Probabilities.getWinProbability(attackerCount, defenderCount);
  }
}
