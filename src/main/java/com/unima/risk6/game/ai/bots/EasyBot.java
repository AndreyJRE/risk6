package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The easy difficulty bot which makes all of its moves randomly
 *
 * @author eameri
 */
public class EasyBot extends Player implements AiBot {

  private final Random rng;
  private final PlayerController playerController;

  /**
   * Constructs an EasyBot with a specified username.
   *
   * @param username The username for the EasyBot.
   */
  public EasyBot(String username) {
    super(username);
    rng = new Random();
    playerController = new PlayerController();
    playerController.setPlayer(this);
  }

  /**
   * Constructs an EasyBot with a default username.
   */
  public EasyBot() {
    super();
    rng = new Random();
    playerController = new PlayerController();
    playerController.setPlayer(this);
  }

  /**
   * Claims a single random country for the bot during the Claim Phase game state.
   *
   * @return A Reinforce object representing the claimed country with one troop placed on it.
   */
  @Override
  public Reinforce claimCountry() {
    List<Country> unclaimed = this.getCountries().stream().filter(country -> !country.hasPlayer())
        .toList();
    return new Reinforce(unclaimed.get(rng.nextInt(unclaimed.size())), 1);
  }

  @Override
  public List<CountryPair> createAllAttacks() {
    List<CountryPair> allAttacks = new ArrayList<>();

    List<CountryPair> decisions = new ArrayList<>();
    for (Continent continent : this.getContinents()) {
      decisions.addAll(this.playerController.getAllAttackableCountryPairs(continent));
    }
    double attackProbability = 0.8;
    while (rng.nextDouble() < attackProbability) {
      CountryPair toAttack = this.getRandomCountryPair(decisions);
      allAttacks.add(toAttack);
      attackProbability *= 0.6;
    }
    return allAttacks;
  }

  @Override
  public Fortify moveAfterAttack(CountryPair winPair) {
    // the automatic move will have already been made
    int maxAvailable = winPair.getOutgoing().getTroops();
    // nextInt automatically chooses any number while always leaving at least one troop behind
    return winPair.createFortify(rng.nextInt(maxAvailable));
  }


  @Override
  public Fortify createFortify() {
    if (rng.nextDouble() < 0.25) {
      return null;
    }
    List<CountryPair> decisions = this.playerController.getAllValidFortifies();
    CountryPair toFortify = this.getRandomCountryPair(decisions);
    int troopsToMove = rng.nextInt(1, toFortify.getOutgoing().getTroops());
    return toFortify.createFortify(troopsToMove);
  }

  /**
   * Picks a random amount of troops to send to a random country until there are no more troops to
   * be added.
   */
  @Override
  public List<Reinforce> createAllReinforcements() {
    List<Reinforce> reinforcements = new ArrayList<>();
    int reinforceTroopsCopy = this.getDeployableTroops();
    while (reinforceTroopsCopy > 0) {
      int troopsSent = rng.nextInt(1, this.getDeployableTroops());
      Reinforce toAdd = this.createRandomReinforce(troopsSent);
      reinforcements.add(toAdd);
      reinforceTroopsCopy -= troopsSent;
    }
    return reinforcements;
  }

  /**
   * Chooses a random country to reinforce.
   *
   * @param troops The amount of troops to send to a country.
   * @return The Reinforce move to be made.
   */
  private Reinforce createRandomReinforce(int troops) {
    Country toReinforce = getRandomCountryFromSet(this.getCountries());
    return new Reinforce(toReinforce, troops);
  }

  /**
   * Randomly picks a country from a set of countries
   *
   * @param countrySet A set of countries.
   * @return A randomly chosen country from the set.
   */
  private Country getRandomCountryFromSet(Set<Country> countrySet) {
    int stopIndex = rng.nextInt(this.playerController.getNumberOfCountries());
    int counter = 0;
    for (Country country : countrySet) {
      if (counter == stopIndex) {
        return country;
      }
      counter++;
    }
    return null; // will never happen, because we are guaranteed to always have at least one country
  }

  /**
   * Randomly selects a CountryPair from a list of CountryPair objects.
   *
   * @param decision A list of CountryPair objects.
   * @return A randomly chosen CountryPair from the list.
   */
  private CountryPair getRandomCountryPair(List<CountryPair> decision) {
    return decision.get(rng.nextInt(decision.size()));
  }
}
