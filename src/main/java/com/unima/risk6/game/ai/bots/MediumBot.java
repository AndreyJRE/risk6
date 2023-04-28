package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The medium difficulty bot, which makes only the best move in each round without analyzing
 * possible future states of the game (greedy).
 *
 * @author eameri
 */
public class MediumBot extends GreedyBot implements AiBot {

  private final PlayerController playerController;
  private int reinforceTroopsCopy;

  public MediumBot(String username) {
    super(username);
    playerController = new PlayerController();
    playerController.setPlayer(this);
  }

  public MediumBot() {
    super();
    playerController = new PlayerController();
    playerController.setPlayer(this);
  }

  @Override
  public List<Reinforce> createAllReinforcements() {
    List<Reinforce> allReinforcements = new ArrayList<>();
    this.reinforceTroopsCopy = this.getDeployableTroops();
    sortContinentsByHighestRelativePower();
    // reinforce defensively
    for (Continent continent : this.getContinentsCopy()) {
      if (this.reinforceTroopsCopy > 0) {
        allReinforcements.addAll(makeContinentDefendable(continent));
      } else {
        break;
      }
    }

    for (Continent continent : this.getContinentsCopy()) {
      if (this.reinforceTroopsCopy > 0
          && Probabilities.relativeTroopContinentPower(this, continent) != 1.0) {
        allReinforcements.addAll(aggressiveReinforce(continent));
      } else {
        break;
      }
    }

    return allReinforcements;
  }

  /**
   * Aggressive reinforcement - finds the strongest country on the continent and reinforces it as
   * much as possible. This would make the country be the best choice for an attack move in the next
   * phase
   *
   * @param continent The continent on which the reinforce move is being made
   */
  private List<Reinforce> aggressiveReinforce(Continent continent) {
    Map<Country, Integer> ownedCountryDiffs = getCountryTroopDiffsByContinent(continent);
    List<Country> countriesByLowestReinforce = new ArrayList<>(ownedCountryDiffs.keySet());
    sortCountriesByLowestDiffs(ownedCountryDiffs, countriesByLowestReinforce);
    // now the country earliest in list is the one with the highest chance of winning.
    // order works, need to invert values to make them positive
    ownedCountryDiffs.replaceAll((country, diff) -> -diff);
    return reinforceSortedCountryList(ownedCountryDiffs, countriesByLowestReinforce);
  }

  /**
   * Sorts the local list of continents in a descending manner based off of how much total control/
   * presence the bot has over a continent
   */
  private void sortContinentsByHighestRelativePower() {
    this.updateContinentsCopy(this.getContinents());
    this.getContinentsCopy().sort(Comparator.comparing(
            (Continent continent) -> Probabilities.relativeTroopContinentPower(this, continent))
        .reversed());
  }

  /**
   * Makes the countries of a continent more likely to survive an attack by attempting to reinforce
   * each country by an amount necessary to even out the number of troops of the current country
   * with that of the most powerful adjacent enemy country
   *
   * @param continent The continent to be reinforced
   */
  private List<Reinforce> makeContinentDefendable(Continent continent) {
    Map<Country, Integer> ownedCountryDiffs = getCountryTroopDiffsByContinent(continent);
    List<Country> countriesByLowestReinforce = new ArrayList<>(ownedCountryDiffs.keySet());
    sortCountriesByLowestDiffs(ownedCountryDiffs, countriesByLowestReinforce);
    return reinforceSortedCountryList(ownedCountryDiffs, countriesByLowestReinforce);
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
  private List<Reinforce> reinforceSortedCountryList(Map<Country, Integer> ownedCountryDiffs,
      List<Country> sortedCountryList) {
    List<Reinforce> listReinforce = new ArrayList<>();
    for (Country country : sortedCountryList) {
      if (this.reinforceTroopsCopy > 0 && ownedCountryDiffs.get(country) > 0) {
        int amountDeployed = Math.min(this.getDeployableTroops(), ownedCountryDiffs.get(country));
        listReinforce.add(new Reinforce(country, amountDeployed));
        this.reinforceTroopsCopy -= amountDeployed;
      } else {
        break;
      }
    }
    return listReinforce;
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
    listToSort.sort(Comparator.comparing(ownedCountryDiffs::get));
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
    List<CountryPair> diffInfo = this.playerController.getAllAttackableCountryPairs(continent);
    for (CountryPair countryPair : diffInfo) {
      this.getCountryPairDiff(ownedCountryDiffs, countryPair);
    }
    return ownedCountryDiffs;
  }

  private void getCountryPairDiff(Map<Country, Integer> ownedCountryDiffs,
      CountryPair countryPair) {
    if (ownedCountryDiffs.get(countryPair.getOutgoing()) == null) {
      ownedCountryDiffs.put(countryPair.getOutgoing(),
          calculateTroopWeakness(countryPair.getOutgoing(), countryPair.getIncoming()));
    } else {
      ownedCountryDiffs.put(countryPair.getOutgoing(),
          Math.max(calculateTroopWeakness(countryPair.getOutgoing(), countryPair.getIncoming()),
              ownedCountryDiffs.get(countryPair.getOutgoing())));
    }
  }

  @Override
  public List<CountryPair> createAllAttacks() {
    List<CountryPair> allAttacks = new ArrayList<>();
    sortContinentsByHighestRelativePower();
    for (Continent continent : this.getContinentsCopy()) {
      allAttacks.addAll(makeBestAttackInContinent(continent));
    }
    return allAttacks;
  }

  /**
   * Grabs all possible attack moves in the current continent, and performs only those which have a
   * high probability of victory until one side loses.
   *
   * @param continent The continent in which attacks will be performed.
   */
  private List<CountryPair> makeBestAttackInContinent(Continent continent) {
    List<CountryPair> attacksToReturn = new ArrayList<>();
    List<CountryPair> allPossibleAttacks = this.playerController.getAllAttackableCountryPairs(
        continent);
    sortAttacksByProbability(allPossibleAttacks);
    for (CountryPair attackPair : allPossibleAttacks) {
      if (attackPair.getWinningProbability() > 70) {
        attacksToReturn.add(attackPair);
      } else {
        break;
      }
    }
    return attacksToReturn;
  }

  /**
   * Sorts a list of pairs of countries descending by the probability of the attacking country
   * (first entry) winning a battle against the defending country (second entry)
   *
   * @param unsortedPairs The general unsorted list of possible attack moves
   */
  private void sortAttacksByProbability(List<CountryPair> unsortedPairs) {
    unsortedPairs.sort(Comparator.comparing(CountryPair::getWinningProbability).reversed());

  }

  /**
   * From the weakest countries, only fortifies the weakest one which would also result in the
   * lowest loss of strength in surrounding territories.
   *
   * @return The strongest fortify move.
   */
  public Fortify createFortify() {
    Fortify fortify = null;
    sortContinentsByHighestRelativePower();
    Map<Country, Integer> allOwnedCountryDiffs = new HashMap<>();
    for (Continent continent : this.getContinentsCopy()) {
      allOwnedCountryDiffs.putAll(getCountryTroopDiffsByContinent(continent));
    }
    List<Country> countriesByHighestDiff = new ArrayList<>(allOwnedCountryDiffs.keySet());
    countriesByHighestDiff.sort(
        Comparator.comparing((Country country) -> allOwnedCountryDiffs.get(country)).reversed());

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
        // TODO: Math.max?
        fortify = new Fortify(bestAdj, country,
            Math.min((bestAdj.getTroops() - country.getTroops()) / 2,
                -allOwnedCountryDiffs.get(bestAdj)));
        break;
      }
    }
    return fortify;
  }
}
