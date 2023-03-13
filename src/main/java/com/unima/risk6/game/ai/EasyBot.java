package com.unima.risk6.game.ai;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.GameState;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The Easy difficulty bot
 *
 * @author eameri
 */
public class EasyBot extends Player implements AiBot {

  private final Random rng;

  public EasyBot() {
    rng = new Random();
  }

  /**
   * makes a series of moves depending on which phase the game is in
   *
   * @param gameState the current state of the game
   * @author eameri
   */
  @Override
  public void makeMove(GameState gameState) { // later: separate based off of round
    if (this.numberOfCountries() == 0) { // unable to make a move if bot is out of the game.
      return;
    }
    this.createAllReinforcements();
    this.createAllAttacks();
    this.nextPhase();
    this.createFortify();
    this.nextPhase();
  }

  /**
   * Randomly picks countries to attack. The bot will continue to attack the same country until it
   * loses a fight
   *
   * @author eameri
   */
  public void createAllAttacks() {
    Map<Country, List<Country>> decisions = this.getAllAttackableCountryPairs();
    double attackProbability = 0.8;
    while (rng.nextDouble() < attackProbability) {
      Country[] toAttack = this.getRandomCountryPair(decisions);
      Attack attack = createAttack(toAttack);
      while (attackAgain(attack)) {
        attack = createAttack(toAttack);
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
  public Attack createAttack(Country[] toAttack) {
    int availableTroops = toAttack[0].getTroops();
    int attackingTroops = rng.nextInt(1, Math.min(4, availableTroops));
    Attack moveToMake = new Attack(toAttack[0], toAttack[1], attackingTroops);
    this.sendAttack(moveToMake);
    return moveToMake;
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
    Map<Country, List<Country>> decisions = this.getAllValidFortifies();
    Country[] toFortify = this.getRandomCountryPair(decisions);
    int troopsToMove = rng.nextInt(1, toFortify[0].getTroops());
    this.sendFortify(new Fortify(toFortify[0], toFortify[1], troopsToMove));
  }

  /**
   * Picks a random amount of troops to send to a random country until there are no more troops to
   * be added
   *
   * @author eameri
   */
  public void createAllReinforcements() {
    while (this.getReinforcementTroops() > 0) {
      int troopsSent = rng.nextInt(1, this.getReinforcementTroops());
      this.createReinforce(troopsSent);
      this.setReinforcementTroops(this.getReinforcementTroops() - troopsSent);
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
    this.sendReinforce(new Reinforce(toReinforce, troops));
  }

  /**
   * Randomly pick a country from a set of countries
   *
   * @param countrySet a set of countries
   * @return A randomly chosen country
   * @author eameri
   */
  public Country getRandomCountryFromSet(Set<Country> countrySet) {
    int stopIndex = rng.nextInt(this.numberOfCountries());
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
  public Country[] getRandomCountryPair(Map<Country, List<Country>> decision) {
    Country[] countryPair = new Country[2];
    List<Country> keyList = new ArrayList<>(decision.keySet());
    countryPair[0] = keyList.get(rng.nextInt(keyList.size()));
    List<Country> destinationCountries = decision.get(countryPair[0]);
    countryPair[1] = destinationCountries.get(rng.nextInt(destinationCountries.size()));

    return countryPair;
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
    int troopsUsed = attack.getTroopNumber();
    int troopsLost = attack.getaLosses();
    int troopsAvailable = attack.getToAttack().getTroops(); // contains number after attack
    return troopsLost < troopsUsed && troopsAvailable > 1 && !attack.battleWon();
  }
}
