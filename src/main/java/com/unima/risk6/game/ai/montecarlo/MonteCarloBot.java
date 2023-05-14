package com.unima.risk6.game.ai.montecarlo;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.GreedyBot;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A bot used for internal simulations as a replacement for the Hard Bot and for human players. For
 * each of the 3 big phases, it collects a list of moves it deems optimal, and randomly decides
 * which ones should be executed.
 *
 * @author eameri
 */
public class MonteCarloBot extends GreedyBot implements AiBot {

  private static final Random RNG = new Random();
  private double attackProbability = 1.;


  /**
   * Constructs a new MonteCarloBot as a copy of a player.
   *
   * @param toCopy the player which is to be copied.
   */
  public MonteCarloBot(Player toCopy) {
    super(toCopy);
    playerController.setPlayer(this);
  }

  /**
   * Constructs a default MonteCarloBot.
   *
   * @param username the username of the bot.
   */
  public MonteCarloBot(String username) {
    super(username);
    playerController.setPlayer(this);
  }

  /**
   * Creates a map of reinforce moves for every country owned based off of the troop numbers in
   * adjacent countries with the number of troops to add slightly randomized.
   *
   * @return A map of countries to an amount of troops which would be good for a reinforce.
   */
  public Map<Country, Integer> getReinforceMoves() { //recreate defendable -> aggressive
    Map<Country, Integer> diffMap = new HashMap<>();
    for (Country reinforcable : this.getCountries()) {
      for (Country adj : reinforcable.getAdjacentCountries()) {
        if (!this.equals(adj.getPlayer())) {
          this.getCountryPairDiff(diffMap, new CountryPair(reinforcable, adj));
        }
      }
    }
    return diffMap;
  }

  /**
   * Goes through all possible attack moves and returns only those which are likely to win.
   *
   * @return A list of attack moves with a high probability of victory.
   */
  public List<CountryPair> getAttackMoves() {
    List<CountryPair> attackPairs = new ArrayList<>();
    for (Continent continent : this.getAllContinents()) {
      attackPairs.addAll(this.playerController.getAllValidCountryPairs(continent));
    }
    return attackPairs.stream().filter(pair -> pair.getWinningProbability() > 65).toList();
  }

  /**
   * Gets a list of possible fortify moves using the greedy algorithm of the GreedyBot, but
   * collecting at most 5 different options instead of only returning the best one.
   *
   * @return A list of algorithmically optimal fortify moves.
   */
  public List<Fortify> getFortifyMoves() {
    List<Fortify> fortifyList = new LinkedList<>();
    this.sortContinentsByHighestRelativePower();
    Map<Country, Integer> allOwnedCountryDiffs = new HashMap<>();
    List<Country> countriesByHighestDiff = getCountriesByHighestDiff(allOwnedCountryDiffs);
    for (Country country : countriesByHighestDiff) {
      Country bestAdj = this.findBestAdj(allOwnedCountryDiffs, country);
      if (bestAdj != null) {
        int diff = allOwnedCountryDiffs.get(bestAdj);
        int surrounding = diff < 0 ? -diff / 2 : new Random().nextInt(0, 2);
        fortifyList.add(new Fortify(bestAdj, country,
            Math.min((bestAdj.getTroops() - country.getTroops()) / 2, surrounding)));
      }
    }
    return fortifyList.subList(0, Math.min(5, fortifyList.size()));
  }

  /**
   * Sorts the object variable list of continents in descending order based off of the bots power.
   */
  public void sortContinentsByHighestRelativePower() {
    this.allContinents.sort(Comparator.comparing(
            (Continent continent) -> Probabilities.relativeTroopContinentPower(this, continent))
        .reversed());
  }

  /**
   * Returns a random country object from a set of countries.
   *
   * @param countrySet the non-empty set of countries.
   * @return a random country from the set.
   */
  private Country pickRandomCountryFromSet(Set<Country> countrySet) {
    int position = RNG.nextInt(countrySet.size());
    int counter = 0;
    for (Country country : countrySet) {
      if (position == counter) {
        return country;
      }
      counter++;
    }
    return null; // will never happen, method is only called when countries are available
  }

  @Override
  public List<Reinforce> createAllReinforcements() {
    List<Reinforce> answer = new ArrayList<>();
    int reinforceTroopsCopy = this.getDeployableTroops();
    Map<Country, Integer> allPossibilities = this.getReinforceMoves();
    Map<Country, Integer> allPossibilitiesCopy = new HashMap<>(allPossibilities);
    while (reinforceTroopsCopy > 0 && !allPossibilitiesCopy.isEmpty()) {
      // create reinforcements here with troopnumber check
      Country choice = this.pickRandomCountryFromSet(allPossibilitiesCopy.keySet());
      int troops = Math.min(reinforceTroopsCopy, Math.abs(allPossibilitiesCopy.get(choice)));
      Reinforce chosen = new Reinforce(choice, troops);
      answer.add(chosen);
      allPossibilitiesCopy.remove(choice);
      reinforceTroopsCopy -= chosen.getToAdd();
    }

    while (reinforceTroopsCopy > 0) {
      Country randomCountry = this.pickRandomCountryFromSet(allPossibilities.keySet());
      int toReinforce = reinforceTroopsCopy > 1 ? RNG.nextInt(1, reinforceTroopsCopy) : 1;
      Reinforce extra = new Reinforce(randomCountry, toReinforce);
      answer.add(extra);
      reinforceTroopsCopy -= extra.getToAdd();
    }

    return answer;
  }

  /**
   * Creates a list of all attacks the bot will perform as CountryPair objects which include both
   * countries involved in each attack.
   *
   * @return A list of CountryPair objects representing all attacks.
   */
  @Override
  public CountryPair createAttack() {
    return !this.getAttackMoves().isEmpty() ? this.getAttackMoves().get(0) : null;
  }

  @Override
  public Fortify createFortify() {
    List<Fortify> fortifies = this.getFortifyMoves();
    return !fortifies.isEmpty() ? fortifies.get(RNG.nextInt(fortifies.size())) : null;
  }


  @Override
  public boolean attackAgain() {
    this.attackProbability *= 0.95;
    return !this.getAttackMoves().isEmpty() && RNG.nextDouble() < this.attackProbability;
  }

}
