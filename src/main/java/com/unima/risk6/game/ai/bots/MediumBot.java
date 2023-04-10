package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.logic.GameState;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * The medium difficulty bot, which makes only the best move in each round without analyzing
 * possible future states of the game (greedy).
 *
 * @author eameri
 */
public class MediumBot extends Player implements AiBot {

  private final PlayerController playerController;
  private final List<Continent> continentsCopy;
  private final Random rng;
  private PriorityQueue<Country> weightedCountries;


  public MediumBot(GameState gameState) {
    rng = new Random();
    playerController = new PlayerController(this, gameState);
    continentsCopy = new ArrayList<>();
    continentsCopy.addAll(gameState.getContinents());
  }

  @Override
  public void makeMove(GameState gameState) {
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
        // this would happen anyway!
//        if (Probabilities.relativeTroopContinentPower(this, continent) == 1.0) {
//          reinforceContinentBorder(continent);
//        } else {
        makeContinentDefendable(continent);
//        }
      }
    }

    for (Continent continent : this.continentsCopy) {
      if (this.getDeployableTroops() > 0
          && Probabilities.relativeTroopContinentPower(this, continent) != 1.0) {
        aggressiveReinforce(continent);
      }
    }

  }

  private void aggressiveReinforce(Continent continent) {
    // find country with highest diff, reinforce it even more
    Map<Country, Integer> ownedCountryDiffs = getCountryTroopDiffsByContinent(continent);
    List<Country> countriesByLowestReinforce = new ArrayList<>(ownedCountryDiffs.keySet());
    sortCountriesByLowestDiffs(ownedCountryDiffs, countriesByLowestReinforce);
    // now the country earliest in list is the one with the highest chance of winning
    // order works, need to invert values to make them positive
    ownedCountryDiffs.replaceAll((country, diff) -> -diff);
    reinforceSortedCountryList(ownedCountryDiffs, countriesByLowestReinforce);
  }

  /**
   * Sorts the local list of continents in a descending manner based off of how much total control
   * the bot has over a continent
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
    // calculate how much to reinforce each country
    Map<Country, Integer> ownedCountryDiffs = getCountryTroopDiffsByContinent(continent);
    List<Country> countriesByLowestReinforce = new ArrayList<>(ownedCountryDiffs.keySet());
    sortCountriesByLowestDiffs(ownedCountryDiffs, countriesByLowestReinforce);
    reinforceSortedCountryList(ownedCountryDiffs, countriesByLowestReinforce);
  }

  private void reinforceSortedCountryList(Map<Country, Integer> ownedCountryDiffs,
      List<Country> countriesByLowestReinforce) {
    for (Country country : countriesByLowestReinforce) {
      if (this.getDeployableTroops() > 0 && ownedCountryDiffs.get(country) > 0) {
        int amountDeployed = Math.min(this.getDeployableTroops(), ownedCountryDiffs.get(country));
        this.playerController.sendReinforce(country, amountDeployed);
        this.playerController.changeDeployableTroops(-amountDeployed);
      } else {
        break;
      }
    }
  }

  private static void sortCountriesByLowestDiffs(Map<Country, Integer> ownedCountryDiffs,
      List<Country> countriesByLowestReinforce) {
    countriesByLowestReinforce.sort((country1, country2) -> {
      int country1Count = ownedCountryDiffs.get(country1);
      int country2Count = ownedCountryDiffs.get(country2);
      return country1Count - country2Count;
    });
  }

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
   * Reinforces only the borders of a continent, by matching the amount of troops on the bordering
   * countries with that of the strongest adjacent country. Only used by the bot when it has full
   * control over a continent
   *
   * @param continent The continent whose border is to be reinforced
   */
//  public void reinforceContinentBorder(Continent continent) {
//    for (Country country : continent.getCountries()) {
//      if (Probabilities.isBorderCountry(country)) {
//        int diff = 0;
//        for (Country adj : country.getAdjacentCountries()) {
//          if (!this.equals(adj.getPlayer())) {
//            diff = Math.max(calculateTroopWeakness(country, adj), diff);
//          }
//        }
//        if (diff > 0) {
//          this.playerController.sendReinforce(country, diff);
//        }
//      }
//    }
//  }

  /**
   * Calculates how many troops the country is lacking compared to an adjacent country
   *
   * @param country The country being tested
   * @param adj     One adjacent country
   * @return The amount of troops country needs to have the same amount as adj
   */
  private int calculateTroopWeakness(Country country, Country adj) {
    return adj.getTroops() - country.getTroops();
  }

  private void createAllAttacks() {
    // for strongest continents
    // rate all possible attacks
    // make strongest attack move
    sortContinentsByHighestRelativePower();
    for (Continent continent : continentsCopy) {
      makeBestAttackInContinent(continent);
    }
  }

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
        while (this.equals(attacker.getContinent()) && !this.equals(defender.getPlayer())) {
          this.playerController.sendAttack(attacker, defender,
              Math.min(3, attacker.getTroops() - 1));
        }
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
   * @param allAttacks The map containing all attack moves to be extract
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
   * Sorts a List of Pairs of Countries descending by the probability of the attacking country
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

  private void createFortify() {
    // find the weakest country (in terms of diff) and attempt to balance out both the new country
    // and the fortifier country
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

    // find good neighbour
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
        this.playerController.sendFortify(bestAdj, country, 0);
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
