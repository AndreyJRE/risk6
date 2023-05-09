package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * An intermediate class representing a player which only makes greedy moves. Used for Medium- and
 * HardBot since they do some things in the same way.
 *
 * @author eameri
 */
public abstract class GreedyBot extends Player implements AiBot {

  protected static final Random RNG = new Random();
  protected List<Continent> allContinents;
  protected List<Country> allCountries;
  protected final PlayerController playerController;

  /**
   * Retrieves a copy of the list of continents.
   *
   * @return a copy of the list of continents.
   */
  protected List<Continent> getAllContinents() {
    return allContinents;
  }

  @Override
  public void setGameState(GameState gameState) {
    this.allContinents = new ArrayList<>();
    this.allContinents.addAll(gameState.getContinents());
    this.allCountries = new ArrayList<>();
    this.allCountries.addAll(gameState.getCountries());
  }

  /**
   * Constructs a new GreedyBot as a copy of a player.
   *
   * @param player the player which is to be copied.
   */
  public GreedyBot(Player player) {
    super(player);
    playerController = new PlayerController();
    playerController.setPlayer(this);
    this.allContinents = new ArrayList<>();
  }

  /**
   * Constructs a default GreedyBot.
   *
   * @param username the username of the bot.
   */
  public GreedyBot(String username) {
    super(username);
    playerController = new PlayerController();
    playerController.setPlayer(this);
    this.allContinents = new ArrayList<>();
  }

  public abstract List<Reinforce> createAllReinforcements();

  public abstract CountryPair createAttack();

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
    if (troopsAffordable == Integer.MIN_VALUE) {
      troopsAffordable = 1;
    }
    // we can spare troops
    if (troopsAffordable < 0) {
      troopsAffordable *= -1;
      int outgoingTroops = winPair.getOutgoing().getTroops();
      int incomingTroops = winPair.getIncoming().getTroops();
      int makeEqual = (outgoingTroops - incomingTroops) / 2;
      return winPair.createFortify(Math.min(troopsAffordable / 2, makeEqual));
    } else { // not worth it to fortify -> all surrounding enemy countries are stronger
      return null;
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
  public int calculateTroopWeakness(Country country, Country adj) {
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
    this.allContinents.forEach(cont -> continentMap.put(cont.getContinentName(), cont));
    long unclaimedCount = this.allCountries.stream().filter(c -> !c.hasPlayer()).count();
    if (unclaimedCount > 0) {
      List<Continent> priorityList = Arrays.asList(continentMap.get(ContinentName.AUSTRALIA),
          continentMap.get(ContinentName.SOUTH_AMERICA),
          continentMap.get(ContinentName.NORTH_AMERICA), continentMap.get(ContinentName.AFRICA),
          continentMap.get(ContinentName.EUROPE), continentMap.get(ContinentName.ASIA));
      Continent targetContinent = findBestClaim(priorityList);
      Country targetCountry = findUnclaimedCountry(targetContinent);
      return new Reinforce(targetCountry, 1);
    } else {
      List<Continent> priorityList = this.getAllContinents().stream()
          .sorted(Comparator.comparingDouble(this::calculateClaimPhasePower).reversed()).toList();
      Map<Country, Integer> allDiffs = new HashMap<>();
      this.getAllContinents()
          .forEach(continent -> allDiffs.putAll(this.getCountryTroopDiffsByContinent(continent)));
      Country choice = null;
      for (Continent continent : priorityList) {
        choice = allDiffs.keySet().stream().filter(
                country -> country.getContinent().getContinentName()
                    .equals(continent.getContinentName())).max(Comparator.comparingInt(allDiffs::get))
            .get();
      }
      return new Reinforce(choice, 1);
    }
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
    return continent.getCountries().stream().anyMatch(c -> !c.hasPlayer());
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

  public int getAttackTroops(Country attacker) {
    return Math.min(3, attacker.getTroops() - 1);
  }

  /**
   * Creates a mapping of countries from a continent to the amount of additional troops needed in
   * order to balance out their strength with that of their strongest enemy country.
   *
   * @param continent The continent whose countries are being tested.
   * @return A map of countries to the additionally necessary amount of troops (if the value exists)
   */
  protected Map<Country, Integer> getCountryTroopDiffsByContinent(Continent continent) {
    Map<Country, Integer> ownedCountryDiffs = new HashMap<>();
    List<CountryPair> diffInfo = this.playerController.getAllValidCountryPairs(continent);
    for (CountryPair countryPair : diffInfo) {
      this.getCountryPairDiff(ownedCountryDiffs, countryPair);
    }
    return ownedCountryDiffs;
  }


  protected void getCountryPairDiff(Map<Country, Integer> ownedCountryDiffs,
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

  protected Country findBestAdj(Map<Country, Integer> allOwnedCountryDiffs, Country country) {
    Country bestAdj = null;
    for (Country adj : country.getAdjacentCountries()) {
      if (this.equals(adj.getPlayer()) && adj.getTroops() >= country.getTroops()
          && allOwnedCountryDiffs.getOrDefault(adj, 0) < 0) {
        if (bestAdj == null || adj.getTroops() > bestAdj.getTroops()
            || allOwnedCountryDiffs.get(adj) < allOwnedCountryDiffs.get(bestAdj)) {
          bestAdj = adj;
        }
      }
    }
    return bestAdj;
  }

  protected List<Country> getCountriesByHighestDiff(Map<Country, Integer> allOwnedCountryDiffs) {
    for (Continent continent : this.getAllContinents()) {
      allOwnedCountryDiffs.putAll(getCountryTroopDiffsByContinent(continent));
    }
    List<Country> countriesByHighestDiff = new ArrayList<>(allOwnedCountryDiffs.keySet());
    countriesByHighestDiff.sort(
        Comparator.comparing((Country country) -> allOwnedCountryDiffs.get(country)).reversed());
    return countriesByHighestDiff;
  }
}
