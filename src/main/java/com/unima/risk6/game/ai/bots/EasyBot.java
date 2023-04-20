package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.MovePair;
import com.unima.risk6.game.logic.Attack;
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
  private Set<Country> countries;

  public EasyBot() {
    rng = new Random();
    playerController = new PlayerController();
    playerController.setPlayer(this);
  }

  /**
   * A method for a bot to make moves for all 3 phases of the game
   */
  @Override
  public void makeMove() { // later: separate based off of round
    if (this.playerController.getNumberOfCountries()
        == 0) { // unable to make a move if bot is out of the game.
      return;
    }
    this.createAllReinforcements();
    this.createAllAttacks();
    //this.nextPhase();
    this.createFortify();
    //this.nextPhase();

  }

  /**
   * A method for a bot to claim a single country during the CLAIM PHASE Game State. The EasyBot
   * picks its country to claim randomly.
   */
  @Override
  public void claimCountry() {
    List<Country> unclaimed = this.countries.stream().filter(country -> !country.hasPlayer())
        .toList();
    this.playerController.sendReinforce(unclaimed.get(rng.nextInt(unclaimed.size())), 1);
  }

  /**
   * Randomly picks countries to attack. The bot will continue to attack the same country until it
   * loses a fight
   *
   * @author eameri
   */
  public void createAllAttacks() {

    List<MovePair> decisions = new ArrayList<>();
    for (Continent continent : this.getContinents()) {
      decisions.addAll(this.playerController.getAllAttackableCountryPairs(continent));
    }
    double attackProbability = 0.8;
    while (rng.nextDouble() < attackProbability) {
      MovePair toAttack = this.getRandomCountryPair(decisions);
      Attack attack = createAndSendAttack(toAttack);
      while (attackAgain(attack)) {
        attack = createAndSendAttack(toAttack);
      }
      attackProbability *= 0.6;
    }
  }

  /**
   * Creates and sends an Attack object to the server.
   *
   * @param toAttack A pair consisting of the two countries attacking and defending
   * @return the attack object which was sent
   * @author eameri
   */
  public Attack createAndSendAttack(MovePair toAttack) {
    int availableTroops = toAttack.getOutgoing().getTroops();
    int attackingTroops = rng.nextInt(1, Math.min(4, availableTroops)); // exclusive bound
    return this.playerController.sendAttack(toAttack.getOutgoing(), toAttack.getIncoming(),
        attackingTroops);
  }

  /**
   * Randomly picks two countries for the fortify move with a random amount of troops. Low
   * possibility of choosing not to fortify.
   *
   * @author eameri
   */
  public void createFortify() {
    if (rng.nextDouble() < 0.25) {
      return;
    }
    List<MovePair> decisions = this.playerController.getAllValidFortifies();
    MovePair toFortify = this.getRandomCountryPair(decisions);
    int troopsToMove = rng.nextInt(1, toFortify.getOutgoing().getTroops());
    this.playerController.sendFortify(toFortify.getOutgoing(), toFortify.getIncoming(),
        troopsToMove);
  }

  /**
   * Picks a random amount of troops to send to a random country until there are no more troops to
   * be added
   *
   * @author eameri
   */
  public void createAllReinforcements() {
    while (this.getDeployableTroops() > 0) {
      int troopsSent = rng.nextInt(1, this.getDeployableTroops());
      this.createReinforce(troopsSent);
      this.playerController.changeDeployableTroops(-troopsSent);
    }
  }

  /**
   * Creates a Reinforce object and sends it to the server
   *
   * @param troops The amount of troops to send to a country
   * @author eameri
   */
  public void createReinforce(int troops) {
    Country toReinforce = getRandomCountryFromSet(this.getCountries());
    this.playerController.sendReinforce(toReinforce, troops);
  }

  /**
   * Randomly pick a country from a set of countries
   *
   * @param countrySet a set of countries
   * @return A randomly chosen country
   * @author eameri
   */
  public Country getRandomCountryFromSet(Set<Country> countrySet) {
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
  public MovePair getRandomCountryPair(List<MovePair> decision) {
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
  public boolean attackAgain(Attack attack) {
    int troopsUsed = Math.min(2, attack.getTroopNumber());
    int troopsLost = attack.getAttackerLosses();
    int troopsAvailable = attack.getAttackingCountry().getTroops(); // contains number after attack
    return troopsLost < troopsUsed && troopsAvailable > 1 && this.equals(
        attack.getAttackingCountry().getPlayer()) && !this.equals(
        attack.getDefendingCountry().getPlayer());
  }

  public void setCountries(Set<Country> countries) {
    this.countries = countries;
  }
}
