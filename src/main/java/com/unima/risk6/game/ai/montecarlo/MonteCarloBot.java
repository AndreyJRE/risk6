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

public class MonteCarloBot extends GreedyBot implements AiBot {

  private static final Random RNG = new Random();
  private double attackProbability = 1.;


  public MonteCarloBot(Player toCopy) {
    super(toCopy);
    playerController.setPlayer(this);
  }

  public MonteCarloBot(String username) {
    super(username);
    playerController.setPlayer(this);
  }

  public Map<Country, Integer> getReinforceMoves() { //recreate defendable -> aggressive
    Map<Country, Integer> diffMap = new HashMap<>();
    for (Country reinforcable : this.getCountries()) {
      for (Country adj : reinforcable.getAdjacentCountries()) {
        if (!this.equals(adj.getPlayer())) {
          this.getCountryPairDiff(diffMap, new CountryPair(reinforcable, adj));
        }
      }
    }
    diffMap.replaceAll((country, diff) -> diff + RNG.nextInt(0, 3));
    return diffMap;
  }

  public List<CountryPair> getAttackMoves() {
    List<CountryPair> attackPairs = new ArrayList<>();
    for (Continent continent : this.getContinentsCopy()) {
      attackPairs.addAll(this.playerController.getAllValidCountryPairs(continent));
    }
    // TODO: shuffle moves or let it play by best chances?
    return attackPairs.stream().filter(pair -> pair.getWinningProbability() > 70).toList();
  }

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
        break;
      }
    }
    return fortifyList.subList(0, Math.min(5, fortifyList.size()));
  }

  public void sortContinentsByHighestRelativePower() {
    this.continentsCopy.sort(Comparator.comparing(
            (Continent continent) -> Probabilities.relativeTroopContinentPower(this, continent))
        .reversed());
  }


  /**
   * Creates a list of all Reinforce moves the bot will perform.
   *
   * @return A list of Reinforce objects representing all reinforcement moves to be performed.
   */
  @Override
  public List<Reinforce> createAllReinforcements() {
    List<Reinforce> answer = new ArrayList<>();
    int reinforceTroopsCopy = this.getDeployableTroops();
    Map<Country, Integer> allPossibilities = this.getReinforceMoves();
    Map<Country, Integer> allPossibilitiesCopy = new HashMap<>(allPossibilities);
    while (reinforceTroopsCopy > 0 && allPossibilitiesCopy.size() > 0) {
      // create reinforcements here with troopnumber check
      Country choice = this.pickRandomCountryFromSet(allPossibilitiesCopy.keySet());
      int troopsTopLimit = Math.min(reinforceTroopsCopy, allPossibilitiesCopy.get(choice));
      int troopsAdjusted = Math.max(0, troopsTopLimit);
      Reinforce chosen = new Reinforce(choice, troopsAdjusted);
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

  /**
   * Creates a list of all attacks the bot will perform as CountryPair objects which include both
   * countries involved in each attack.
   *
   * @return A list of CountryPair objects representing all attacks.
   */
  @Override
  public CountryPair createAttack() {
    return this.getAttackMoves().size() > 0 ? this.getAttackMoves().get(0) : null;
  }

  @Override
  public Fortify createFortify() {
    List<Fortify> fortifies = this.getFortifyMoves();
    return fortifies.size() > 0 ? fortifies.get(RNG.nextInt(fortifies.size())) : null;
  }


  @Override
  public boolean attackAgain() {
    this.attackProbability *= 0.9;
    return this.getAttackMoves().size() > 0 && RNG.nextDouble() < this.attackProbability;
  }

}
