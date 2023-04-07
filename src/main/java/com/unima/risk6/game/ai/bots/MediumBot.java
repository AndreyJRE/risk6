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
   * Reinforces based off of weighted country and continent importance
   *
   * @author eameri
   */
  private void createAllReinforcements() {
    sortContinentsByRelativePower();
    // reinforce in a way to defend
    for (Continent continent : this.continentsCopy) {
      // if country owned, only reinforce border
      if (this.getDeployableTroops() > 0) {
        if (Probabilities.relativeTroopContinentPower(this, continent) == 1.0) {
          reinforceBorder(continent);
        } else {
          makeContinentDefendable(continent);
        }
      }
    }
    // if still able to reinforce, reinforce border countries
  }

  private void sortContinentsByRelativePower() {
    this.continentsCopy.sort((continent1, continent2) -> {
      double power1 = Probabilities.relativeTroopContinentPower(this, continent1);
      double power2 = Probabilities.relativeTroopContinentPower(this, continent2);
      return (int) ((power1 - power2) * 100);
    });
  }

  private void makeContinentDefendable(Continent continent) {
    Map<Country, Integer> ownedCountries = new HashMap<>();
    // calculate how much to reinforce each country
    for (Country country : continent.getCountries()) {
      if (this.equals(country.getPlayer())) {
        int diff = 0;
        ownedCountries.put(country, 0);
        for (Country adj : country.getAdjacentCountries()) {
          // if adj country has more troops and the difference is higher than the currently saved
          // value
          if (!this.equals(adj.getPlayer())
              && (diff = calculateTroopDiff(country, adj)) > ownedCountries.get(country)) {
            ownedCountries.put(country, diff);
          }
        }
      }
    }
    // sort by least to reinforce

    List<Country> countriesByLowestReinforce = new ArrayList<>(ownedCountries.keySet());
    countriesByLowestReinforce.sort((country1, country2) -> {
      int country1Count = ownedCountries.get(country1);
      int country2Count = ownedCountries.get(country2);
      return country2Count - country1Count;
    });

    // IMPORTANT: See how player-controller methods work and if changeDeployableTroops
    // changes the variable relevant to reinforce phase
    for (Country country : countriesByLowestReinforce) {
      if (this.getDeployableTroops() > 0) {
        int amountDeployed = Math.min(this.getDeployableTroops(), ownedCountries.get(country));
        this.playerController.sendReinforce(country, amountDeployed);
        this.playerController.changeDeployableTroops(-amountDeployed);
      }
    }
  }

  private void sortByLowestTroopDiff(List<Country> countryList) {
    
  }
  public void reinforceBorder(Continent continent) {
    
  }

  private int calculateTroopDiff(Country country, Country adj) {
    return 0;
  }

  private void createAllAttacks() {
    // decide which continents should be attacked/ defended
    // rate best win probability
    // attack those
  }

  /**
   * Rates a list of possible Attacks by win probability
   *
   * @param allAttacks A map of all possible attacks from one or more source countries to their
   *                   adjacent countries
   * @return A List of attacks with the highest win probability attacks as most likely
   */
  private List<List<Country>> rateAttacks(Map<Country, List<Country>> allAttacks) {
    List<List<Country>> highestWinCountryPairs = extractAttackPairs(allAttacks);
    this.sortAttacksByProbability(highestWinCountryPairs);
    return highestWinCountryPairs;
  }

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

  private void sortAttacksByProbability(List<List<Country>> unsortedPairs) {
    unsortedPairs.sort((pair1, pair2) -> {
      double probPair1 = getWinningProbability(pair1.get(0), pair1.get(1));
      double probPair2 = getWinningProbability(pair2.get(0), pair2.get(1));
      return (int) ((probPair1 - probPair2) * 100);
    });
  }

  private void createFortify() {
  }

  private double getWinningProbability(Country attacker, Country defender) {
    int attackerCount = attacker.getTroops();
    int defenderCount = defender.getTroops();
    return Probabilities.getWinProbability(attackerCount, defenderCount);
  }
}
