package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
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

  private int reinforceTroopsCopy;
  private int lastAttackSize;

  /**
   * Constructs a new MediumBot as a copy of a player.
   *
   * @param player The player to be copied.
   */
  public MediumBot(Player player) {
    super(player);
    this.lastAttackSize = 0;
  }

  /**
   * Constructs a MediumBot with the specified username.
   *
   * @param username The username of the MediumBot.
   */
  public MediumBot(String username) {
    super(username);
    this.lastAttackSize = 0;
  }

  /**
   * Constructs a default MediumBot.
   */
  public MediumBot() {
    this("MediumBot #" + RNG.nextInt(1000));
  }

  @Override
  public List<Reinforce> createAllReinforcements() {
    List<Reinforce> allReinforcements = new ArrayList<>();
    this.reinforceTroopsCopy = this.getDeployableTroops();
    sortContinentsByHighestRelativePower();
    // reinforce defensively
    for (Continent continent : this.getAllContinents()) {
      if (this.reinforceTroopsCopy > 0) {
        allReinforcements.addAll(makeContinentDefendable(continent));
      } else {
        break;
      }
    }

    for (Continent continent : this.getAllContinents()) {
      if (this.reinforceTroopsCopy > 0) {
        allReinforcements.addAll(aggressiveReinforce(continent));
      } else {
        break;
      }
    }

    // use up all reinforcements
    if (this.reinforceTroopsCopy > 0 && allReinforcements.size() > 0) {
      Reinforce repeat = allReinforcements.get(0);
      Reinforce extra = new Reinforce(repeat.getCountry(), this.reinforceTroopsCopy);
      this.reinforceTroopsCopy = 0;
      allReinforcements.add(extra);
    }

    // if no proper reinforcement was found, make the strongest country stronger -> greedy
    if (this.reinforceTroopsCopy > 0) {
      Country greedyChoice = this.getCountries().stream() // will never return null
          .max(Comparator.comparing(Country::getTroops)).orElse(null);
      allReinforcements.add(new Reinforce(greedyChoice, this.reinforceTroopsCopy));
      this.reinforceTroopsCopy = 0;
    }

    return allReinforcements;
  }

  /**
   * Aggressive reinforcement - finds the strongest country on the continent in priority order and
   * reinforces it as much as possible. This would make the country be the best choice for an attack
   * move in the next phase.
   *
   * @param continent The continent on which the reinforce move is being made
   */
  private List<Reinforce> aggressiveReinforce(Continent continent) {
    Map<Country, Integer> ownedCountryDiffs = getCountryTroopDiffsByContinent(continent);
    List<Country> countriesByLowestReinforce = new ArrayList<>(ownedCountryDiffs.keySet());
    sortCountriesByLowestDiffs(ownedCountryDiffs, countriesByLowestReinforce);
    // now the country earliest in list is the one with the highest chance of winning.
    // order works, need to invert values to make them positive
    // there shouldn't be any diffs less than 0, since they would have been cleared in step before
    ownedCountryDiffs.replaceAll((country, diff) -> -diff);
    ownedCountryDiffs.replaceAll((country, diff) -> Math.max(diff, 2));
    return reinforceSortedCountryList(ownedCountryDiffs, countriesByLowestReinforce);
  }

  /**
   * Sorts the local list of continents in a descending manner based off of how much total control/
   * presence the bot has over a continent.
   */
  public void sortContinentsByHighestRelativePower() {
    this.getAllContinents().sort(Comparator.comparing(
            (Continent continent) -> Probabilities.relativeTroopContinentPower(this, continent))
        .reversed());
  }

  /**
   * Makes the countries of a continent more likely to survive an attack by attempting to reinforce
   * each country by an amount necessary to even out the number of troops of the current country
   * with that of the most powerful adjacent enemy country.
   *
   * @param continent The continent to be reinforced
   */
  private List<Reinforce> makeContinentDefendable(Continent continent) {
    Map<Country, Integer> ownedCountryDiffs = getCountryTroopDiffsByContinent(continent);
    List<Country> countriesByLowestReinforce = new ArrayList<>(ownedCountryDiffs.keySet());
    sortCountriesByLowestDiffs(ownedCountryDiffs, countriesByLowestReinforce);
    ownedCountryDiffs.replaceAll((entry, diff) -> diff + RNG.nextInt(1, 3));
    return reinforceSortedCountryList(ownedCountryDiffs, countriesByLowestReinforce);
  }

  /**
   * Reinforces countries in the given order by the highest of either the amount of troops that can
   * be deployed or the amount of troops needed to even out the country in comparison to its
   * neighbours.
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
      // TODO: remove check for diff > 0 and just fix maps earlier?
      if (this.reinforceTroopsCopy > 0 && ownedCountryDiffs.get(country) > 0) {
        int amountDeployed = Math.min(this.reinforceTroopsCopy, ownedCountryDiffs.get(country));
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
   * the mapping given to the method.
   *
   * @param ownedCountryDiffs A mapping of countries to the amount of troops needed to even out the
   *                          troop number with that of the strongest neighbouring enemy
   * @param listToSort        The list of countries which is to be sorted
   */
  private static void sortCountriesByLowestDiffs(Map<Country, Integer> ownedCountryDiffs,
      List<Country> listToSort) {
    listToSort.sort(Comparator.comparing(ownedCountryDiffs::get));
  }


  @Override
  public CountryPair createAttack() {
    List<CountryPair> allAttacks = new ArrayList<>();
    sortContinentsByHighestRelativePower();
    for (Continent continent : this.getAllContinents()) {
      allAttacks.addAll(makeBestAttackInContinent(continent));
    }
    this.lastAttackSize = allAttacks.size();
    return allAttacks.size() > 0 ? allAttacks.get(0) : null;
  }

  @Override
  public boolean attackAgain() {
    return this.lastAttackSize > 0;
  }

  /**
   * Grabs all possible attack moves in the current continent, and performs only those which have a
   * high probability of victory until one side loses.
   *
   * @param continent The continent in which attacks will be performed.
   */
  private List<CountryPair> makeBestAttackInContinent(Continent continent) {
    List<CountryPair> attacksToReturn = new ArrayList<>();
    List<CountryPair> allPossibleAttacks = this.playerController.getAllValidCountryPairs(continent);
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
   * (first entry) winning a battle against the defending country (second entry).
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
    List<Country> countriesByHighestDiff = getCountriesByHighestDiff(allOwnedCountryDiffs);

    for (Country country : countriesByHighestDiff) {
      Country bestAdj = findBestAdj(allOwnedCountryDiffs, country);
      if (bestAdj != null) {
        fortify = new Fortify(bestAdj, country,
            Math.min((bestAdj.getTroops() - country.getTroops()) / 2,
                -allOwnedCountryDiffs.get(bestAdj) / 2));
        break;
      }
    }
    return fortify;
  }


}
