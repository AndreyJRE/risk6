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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The easy difficulty bot which makes all of its moves randomly.
 *
 * @author eameri
 */
public class EasyBot extends Player implements AiBot {

  private static final Random RNG = new Random();
  private final PlayerController playerController;
  private GameState currentGameState;
  private double attackProbability;

  /**
   * Constructs an EasyBot with a specified username.
   *
   * @param username The username for the EasyBot.
   */
  public EasyBot(String username) {
    super(username);
    playerController = new PlayerController();
    playerController.setPlayer(this);
    this.attackProbability = 0.8;
  }

  /**
   * Constructs an EasyBot with a default username.
   */
  public EasyBot() {
    this("EasyBot #" + RNG.nextInt(1000));
  }

  /**
   * Claims a single random country for the bot during the Claim Phase game state.
   *
   * @return A Reinforce object representing the claimed country with one troop placed on it.
   */
  @Override
  public Reinforce claimCountry() {
    List<Country> unclaimed = this.currentGameState.getCountries().stream()
        .filter(country -> !country.hasPlayer()).toList();
    if (unclaimed.isEmpty()) {
      return new Reinforce(this.getRandomCountryFromSet(this.getCountries()), 1);
    } else {
      return new Reinforce(unclaimed.get(RNG.nextInt(unclaimed.size())), 1);
    }
  }

  @Override
  public CountryPair createAttack() {
    List<CountryPair> decisions = new ArrayList<>();
    for (Continent continent : this.currentGameState.getContinents()) {
      decisions.addAll(this.playerController.getAllValidCountryPairs(continent));
    }
    return this.getRandomCountryPair(decisions);
  }

  @Override
  public boolean attackAgain() {
    boolean answer = RNG.nextDouble() < this.attackProbability;
    this.attackProbability *= 0.6;
    return answer;
  }

  @Override
  public int getAttackTroops(Country attacker) {
    return RNG.nextInt(1, Math.min(4, attacker.getTroops()));
  }

  @Override
  public Fortify moveAfterAttack(CountryPair winPair) {
    // the automatic move will have already been made
    int maxAvailable = winPair.getOutgoing().getTroops();
    // nextInt automatically chooses any number while always leaving at least one troop behind
    return winPair.createFortify(RNG.nextInt(maxAvailable));
  }


  @Override
  public Fortify createFortify() {
    this.attackProbability = 0.8;
    if (RNG.nextDouble() < 0.25) {
      return null;
    }
    List<CountryPair> decisions = this.playerController.getAllValidFortifies();
    CountryPair toFortify = this.getRandomCountryPair(decisions);
    if (toFortify != null) {
      int troopsToMove = RNG.nextInt(1, toFortify.getOutgoing().getTroops());
      return toFortify.createFortify(troopsToMove);
    }
    // return end phase
    return null;
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
      int troopsSent;
      troopsSent = reinforceTroopsCopy == 1 ? 1 : RNG.nextInt(1, reinforceTroopsCopy);
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
   * Randomly picks a country from a set of countries.
   *
   * @param countrySet A set of countries.
   * @return A randomly chosen country from the set.
   */
  private Country getRandomCountryFromSet(Set<Country> countrySet) {
    int stopIndex = RNG.nextInt(this.playerController.getNumberOfCountries());
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
    return !decision.isEmpty() ? decision.get(RNG.nextInt(decision.size())) : null;
  }

  /**
   * Sets the values relevant to the bots decision-making by copying them from the current game
   * state.
   *
   * @param gameState the current state of the game.
   */
  @Override
  public void setGameState(GameState gameState) {
    this.currentGameState = gameState;
  }


}
