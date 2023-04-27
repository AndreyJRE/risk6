package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.logic.Attack;
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
 * The easy difficulty bot, which makes all of it's moves randomly
 *
 * @author eameri
 */
public class EasyBot extends Player implements AiBot {

  private final Random rng;
  private final PlayerController playerController;
  private int reinforceTroopsCopy;

  public EasyBot(String username) {
    super(username);
    rng = new Random();
    playerController = new PlayerController();
    playerController.setPlayer(this);
  }

  public EasyBot() {
    super();
    rng = new Random();
    playerController = new PlayerController();
    playerController.setPlayer(this);
  }

  /**
   * A method for a bot to make moves for all 3 phases of the game
   */
  public MoveTriplet makeMove() { // later: separate based off of round
    if (this.playerController.getNumberOfCountries() == 0) { // unable to make a move
      // if bot is out of the game.
      return null;
    }
    List<Reinforce> allReinforcements = this.createAllReinforcements();
    List<CountryPair> allAttacks = this.createAllAttacks();
    Fortify fortify = this.createFortify();

    return new MoveTriplet(allReinforcements, allAttacks, fortify);
  }

  /**
   * A method for a bot to claim a single country during the CLAIM PHASE Game State. The EasyBot
   * picks its country to claim randomly.
   */
  @Override
  public Reinforce claimCountry() {
    List<Country> unclaimed = this.getCountries().stream().filter(country -> !country.hasPlayer())
        .toList();
    return new Reinforce(unclaimed.get(rng.nextInt(unclaimed.size())), 1);
  }

  /**
   * Randomly picks countries to attack. The bot will continue to attack the same country until it
   * loses a fight
   *
   * @return
   * @author eameri
   */
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
//      while (attackAgain(attack)) {
//        attack = createAndSendAttack(toAttack);
//        allAttacks.add(attack);
//      }
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

  /**
   * Creates and sends an Attack object to the server.
   *
   * @param toAttack A pair consisting of the two countries attacking and defending
   * @return the attack object which was sent
   * @author eameri
   */
  private Attack createAndSendAttack(CountryPair toAttack) {
    int availableTroops = toAttack.getOutgoing().getTroops();
    int attackingTroops = rng.nextInt(1, Math.min(4, availableTroops)); // exclusive bound
    return toAttack.createAttack(attackingTroops);
  }

  /**
   * Randomly picks two countries for the fortify move with a random amount of troops. Low
   * possibility of choosing not to fortify.
   *
   * @return
   * @author eameri
   */
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
   * be added
   *
   * @author eameri
   */
  public List<Reinforce> createAllReinforcements() {
    List<Reinforce> reinforcements = new ArrayList<>();
    this.reinforceTroopsCopy = this.getDeployableTroops();
    while (reinforceTroopsCopy > 0) {
      int troopsSent = rng.nextInt(1, this.getDeployableTroops());
      Reinforce toAdd = this.createReinforce(troopsSent);
      reinforcements.add(toAdd);
      reinforceTroopsCopy -= troopsSent;
    }
    return reinforcements;
  }

  /**
   * Creates a Reinforce object and sends it to the server
   *
   * @param troops The amount of troops to send to a country
   * @author eameri
   */
  private Reinforce createReinforce(int troops) {
    Country toReinforce = getRandomCountryFromSet(this.getCountries());
    return new Reinforce(toReinforce, troops);
  }

  /**
   * Randomly pick a country from a set of countries
   *
   * @param countrySet a set of countries
   * @return A randomly chosen country
   * @author eameri
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
   * @param decision A mapping of one country to all countries to which a move is allowed
   * @return An array of 2 Countries (0. from, 1. to)
   * @author eameri
   */
  private CountryPair getRandomCountryPair(List<CountryPair> decision) {
    return decision.get(rng.nextInt(decision.size()));
  }

  /**
   * Lets the bot decide if it should attack the same country again
   *
   * @param attack The attack object to be evaluated
   * @return The roll was won, there are still troops available and the country hasn't been taken
   * over yet
   * @author eameri
   */
  private boolean attackAgain(Attack attack) {
    int troopsUsed = Math.min(2, attack.getTroopNumber());
    int troopsLost = attack.getAttackerLosses();
    int troopsAvailable = attack.getAttackingCountry().getTroops(); // contains number after attack
    return troopsLost < troopsUsed && troopsAvailable > 1 && this.equals(
        attack.getAttackingCountry().getPlayer()) && !this.equals(
        attack.getDefendingCountry().getPlayer());
  }

}
