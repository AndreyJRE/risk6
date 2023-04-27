package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An intermediate class representing a player which only makes greedy moves. Used for Medium- and
 * HardBot since they do some things in the same way.
 *
 * @author eameri
 */
abstract class GreedyBot extends Player implements AiBot {

  private final List<Continent> continentsCopy;

  /**
   * Retrieves a copy of the list of continents.
   *
   * @return a copy of the list of continents.
   */
  public List<Continent> getContinentsCopy() {
    return continentsCopy;
  }

  /**
   * Adds all items of a Set of continents to the continentsCopy list.
   *
   * @param continents the set of continents.
   */
  public void setContinentsCopy(Set<Continent> continents) {
    this.continentsCopy.addAll(continents);
  }

  public GreedyBot(String username) {
    super(username);
    this.continentsCopy = new ArrayList<>();
  }

  public GreedyBot() {
    super();
    this.continentsCopy = new ArrayList<>();
  }

  public abstract List<Reinforce> createAllReinforcements();

  public abstract List<CountryPair> createAllAttacks();

  @Override
  public Fortify moveAfterAttack(CountryPair winPair) {
    int troopsAffordable = Integer.MIN_VALUE;
    for (Country adj : winPair.getOutgoing().getAdjacentCountries()) {
      if (!adj.getPlayer().equals(winPair.getOutgoing().getPlayer())) {
        int diff = this.calculateTroopWeakness(winPair.getOutgoing(), adj);
        if (diff > troopsAffordable) {
          troopsAffordable = diff;
        }
      }
    }
    // we can spare troops
    if (troopsAffordable < 0) {
      troopsAffordable *= -1;
      return winPair.createFortify(-troopsAffordable);
    } else { // else try to make it equal
      int outgoingTroops = winPair.getOutgoing().getTroops();
      int incomingTroops = winPair.getIncoming().getTroops();
      return winPair.createFortify((outgoingTroops - incomingTroops) / 2);
    }
  }

  /**
   * Calculates how many additional troops an adjacent country has in comparison to the current
   * country.
   *
   * @param country The country being tested.
   * @param adj     One adjacent country.
   * @return The amount of troops country needs to have the same amount as adj.
   */
  protected int calculateTroopWeakness(Country country, Country adj) {
    return adj.getTroops() - country.getTroops();
  }

  public abstract Fortify createFortify();

  /**
   * A method for a bot to claim a single country during the CLAIM PHASE Game State. The MediumBot
   * attempts to claim countries based off of a ranking of continents.
   */
  @Override
  public Reinforce claimCountry() {
    Map<ContinentName, Continent> continentMap = new HashMap<>();
    this.continentsCopy.forEach(cont -> continentMap.put(cont.getContinentName(), cont));
    List<Continent> priorityList = Arrays.asList(continentMap.get(ContinentName.AUSTRALIA),
        continentMap.get(ContinentName.SOUTH_AMERICA),
        continentMap.get(ContinentName.NORTH_AMERICA), continentMap.get(ContinentName.AFRICA),
        continentMap.get(ContinentName.EUROPE), continentMap.get(ContinentName.ASIA));
    Continent targetContinent = findBestClaim(priorityList);
    Country targetCountry = findUnclaimedCountry(targetContinent);
    return new Reinforce(targetCountry, 1);
  }

  /**
   * Finds an unclaimed country within a given continent.
   *
   * @param continent The continent in which to search for an unclaimed country.
   * @return The unclaimed country if found, otherwise null.
   */
  private Country findUnclaimedCountry(Continent continent) {
    for (Country country : continent.getCountries()) {
      if (!country.hasPlayer()) {
        return country;
      }
    }
    return null; // method only called on continents which have a free country
  }

  /**
   * Finds the best continent to claim a country in based on the priority list and the owned
   * percentage of countries within each continent.
   *
   * @param priorityList a list of continents sorted by priority.
   * @return the best continent to claim a country in.
   */
  private Continent findBestClaim(List<Continent> priorityList) {
    Continent targetContinent = null;
    double maxOwnedPercentage = -1;
    for (Continent continent : priorityList) {
      double ownedPercentage = calculateClaimPhasePower(continent);
      if (ownedPercentage > maxOwnedPercentage && hasFreeCountry(continent)) {
        targetContinent = continent;
        maxOwnedPercentage = ownedPercentage;
      }
    }
    return targetContinent;
  }

  /**
   * Checks if a continent has any unclaimed countries.
   *
   * @param continent the continent to check for unclaimed countries.
   * @return true if the continent has an unclaimed country, otherwise false.
   */
  private boolean hasFreeCountry(Continent continent) {
    for (Country country : continent.getCountries()) {
      if (!country.hasPlayer()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Calculates the percentage of countries owned by the bot within a given continent.
   *
   * @param continent the continent to calculate the owned percentage for.
   * @return the percentage of countries owned by the bot within the continent.
   */
  private double calculateClaimPhasePower(Continent continent) {
    double totalSize = continent.getCountries().size();
    int owned = 0;
    for (Country country : continent.getCountries()) {
      if (this.equals(country.getPlayer())) {
        owned++;
      }
    }
    return owned / totalSize;
  }
}
