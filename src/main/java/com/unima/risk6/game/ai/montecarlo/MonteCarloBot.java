package com.unima.risk6.game.ai.montecarlo;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.GreedyBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
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

public class MonteCarloBot extends GreedyBot implements AiBot {

  private final PlayerController playerController;
  private List<Continent> continentsCopy;

  private static final Random RNG = new Random();
  private double attackProbability = 1.;


  public MonteCarloBot(Player toCopy) {
    super(toCopy);
    playerController = new PlayerController();
    playerController.setPlayer(this);
  }

  public List<Reinforce> getReinforceMoves() {
    int troopsAvailable = this.getDeployableTroops();
    Map<Country, Integer> diffMap = new HashMap<>();
    for (Country reinforcable : this.getCountries()) {
      for (Country adj : reinforcable.getAdjacentCountries()) {
        if (!this.equals(adj.getPlayer())) {
          this.getCountryPairDiff(diffMap, new CountryPair(reinforcable, adj));
        }
      }
    }
    diffMap.replaceAll((country, diff) -> Math.min(diff + RNG.nextInt(0, 3), troopsAvailable));

    List<Country> reinforceList = diffMap.keySet().stream()
        .filter(c -> diffMap.get(c) > 0 && diffMap.get(c) <= troopsAvailable)
        .sorted(Comparator.comparing(diffMap::get).reversed()).toList();
    // reinforce if diff > 0

    return new ArrayList<>(
        diffMap.entrySet().stream().filter(entry -> reinforceList.contains(entry.getKey()))
            .map(entry -> new Reinforce(entry.getKey(), entry.getValue())).toList());
  }

  public List<CountryPair> getAttackMoves() {
    List<CountryPair> attackPairs = new ArrayList<>();
    for (Continent continent : this.getContinents()) {
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


  public void updateContinentsList() {
    this.continentsCopy = new ArrayList<>();
    //TODO: continents must be gotten from a gamestate
    this.continentsCopy.addAll(this.getContinents());
  }

  public List<Continent> getContinentsCopy() {
    return continentsCopy;
  }

  public void sortContinentsByHighestRelativePower() {
    this.updateContinentsList();
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
    List<Reinforce> allPossibilities = this.getReinforceMoves();
    List<Reinforce> allPossibilitiesCopy = new ArrayList<>(allPossibilities);
    int troopsAvailable = this.getDeployableTroops();
    while (troopsAvailable > 0 && allPossibilitiesCopy.size() > 0) {
      Reinforce chosen = allPossibilitiesCopy.get(RNG.nextInt(allPossibilitiesCopy.size()));
      answer.add(chosen);
      allPossibilitiesCopy.remove(chosen);
      troopsAvailable -= chosen.getToAdd();
    }

    while (troopsAvailable > 0) {
      Country randomCountry = allPossibilities.get(RNG.nextInt(allPossibilities.size()))
          .getCountry();
      int toReinforce = troopsAvailable > 1 ? RNG.nextInt(1, troopsAvailable) : 1;
      Reinforce extra = new Reinforce(randomCountry, toReinforce);
      answer.add(extra);
      troopsAvailable -= extra.getToAdd();
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
    return RNG.nextDouble() < this.attackProbability;
  }

}
