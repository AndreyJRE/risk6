package com.unima.risk6.game.ai.montecarlo;

import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.MoveTriplet;
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

public class MonteCarloBot extends Player {

  private final PlayerController playerController;
  private List<Continent> continentsCopy;

  public MonteCarloBot(Player toCopy) {
    super(toCopy);
    playerController = new PlayerController();
    playerController.setPlayer(this);
    this.continentsCopy = new ArrayList<>();
  }

  public List<MoveTriplet> getLegalMoves() {
    List<Reinforce> reinforceMoves = this.getReinforceMoves();
    List<CountryPair> attackMoves = this.getAttackMoves();
    List<Fortify> fortifyMoves = this.getFortifyMoves();

    List<MoveTriplet> legalMoves = new ArrayList<>();
    // for (List<Reinforce> reinforce : reinforceMoves) {
    // for (List<> attack : attackMoves) {
    // for (Fortify fortify : fortifyMoves) {
    // legalMoves.add(new MoveTriplet(reinforce, attack, fortify));
    // }
    // }
    // }

    return legalMoves;
  }

  // TODO: DO
  public List<Reinforce> getReinforceMoves() {
    List<Reinforce> reinforcements = new ArrayList<>();
    int troopsAvailable = this.getDeployableTroops();
    Map<Country, Integer> diffMap = new HashMap<>();
    for (Country reinforcable : this.getCountries()) {
      for (Country adj : reinforcable.getAdjacentCountries()) {
        if (!this.equals(adj.getPlayer())) {
          this.getCountryPairDiff(diffMap, new CountryPair(reinforcable, adj));
        }
      }
    }

    List<Country> reinforceList = diffMap.keySet().stream().filter(c -> diffMap.get(c) > 0)
        .sorted(Comparator.comparing(diffMap::get).reversed()).toList();
    reinforcements.addAll(
        diffMap.entrySet().stream().filter(entry -> reinforceList.contains(entry.getKey()))
            .map(entry -> new Reinforce(entry.getKey(), entry.getValue())).toList());
    // reinforce if diff > 0

    return reinforcements;
  }

  // TODO: List<List>
  public List<CountryPair> getAttackMoves() {
    List<CountryPair> attackPairs = new ArrayList<>();
    for (Continent continent : this.getContinents()) {
      attackPairs.addAll(this.playerController.getAllValidCountryPairs(continent));
    }
    // TODO: sort by best for continent?
    return attackPairs.stream().filter(pair -> pair.getWinningProbability() > 70).toList();
  }

  public List<Fortify> getFortifyMoves() {
    List<Fortify> fortifyList = new LinkedList<>();
    this.sortContinentsByHighestRelativePower();
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
        int diff = allOwnedCountryDiffs.get(bestAdj);
        int surrounding = diff < 0 ? -diff / 2 : new Random().nextInt(0, 2);
        fortifyList.add(new Fortify(bestAdj, country,
            Math.min((bestAdj.getTroops() - country.getTroops()) / 2, surrounding)));
        break;
      }
    }
    return fortifyList.subList(0, Math.min(5, fortifyList.size()));
  }

  private Map<Country, Integer> getCountryTroopDiffsByContinent(Continent continent) {
    Map<Country, Integer> ownedCountryDiffs = new HashMap<>();
    List<CountryPair> diffInfo = this.playerController.getAllValidCountryPairs(continent);
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

  private int calculateTroopWeakness(Country country, Country adj) {
    return adj.getTroops() - country.getTroops();
  }

  public void updateContinentsList() {
    this.continentsCopy = new ArrayList<>();
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

}
