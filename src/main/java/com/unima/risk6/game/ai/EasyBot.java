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

/***
 * @author eameri
 */
public class EasyBot extends Player implements AiBot {

  private final Random rng;

  public EasyBot() {
    rng = new Random();
  }

  @Override
  public void makeMove(GameState gameState) { // later: separate based off of round
    if (this.numberOfCountries() == 0) { // unable to make a move if bot is out of the game.
      return;
    }
    // need a new method: skipPhase() to signal that you are not making a move
    this.createAllReinforcements();
    this.createAllAttacks();
    this.createFortify(); // maybe add possibility of not fortifying?
  }

  public void createAllAttacks() {
    Map<Country, List<Country>> decisions = this.getAllAttackableCountryPairs();
    double attackProbability = 0.8;
    while (rng.nextDouble() < attackProbability) {
      Country[] toAttack = this.getRandomCountryPair(decisions);
      Attack moveToMake = new Attack(toAttack[0], toAttack[1], 0);
      do {
        this.sendAttack(moveToMake);
        // later calculate attackingTroops and possible changes needed
        // if attacking again
      } while (moveToMake.battleWon());
      attackProbability *= 0.6;
    }
  }

  public void createFortify() {
    // decide if fortify should even be made
    Map<Country, List<Country>> decisions = this.getAllValidFortifies();
    Country[] toFortify = this.getRandomCountryPair(decisions);
    int troopsToMove = rng.nextInt(1, toFortify[0].getTroops());
    this.sendFortify(new Fortify(toFortify[0], toFortify[1], troopsToMove));
  }

  public void createAllReinforcements() {
    List<Reinforce> reinforcements = new ArrayList<>();
    int troopsAvailable = 999; // temp number until we get a variable for available troops
    while (troopsAvailable > 0) { // implement territory card rules (forced reinforcements
      // first)
      int troopsSent = rng.nextInt(1, troopsAvailable);
      this.createReinforce(troopsSent);
      troopsAvailable -= troopsSent;
    }
  }

  public void createReinforce(int troops) {
    Country toReinforce = getRandomCountryFromSet(this.getCountries());
    this.sendReinforce(new Reinforce(toReinforce, troops));
  }

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

  public Country[] getRandomCountryPair(Map<Country, List<Country>> decision) {
    Country[] countryPair = new Country[2];
    List<Country> keyList = new ArrayList<>(decision.keySet());
    countryPair[0] = keyList.get(rng.nextInt(keyList.size()));
    List<Country> destinationCountries = decision.get(countryPair[0]);
    countryPair[1] = destinationCountries.get(rng.nextInt(destinationCountries.size()));

    return countryPair;
  }
}
