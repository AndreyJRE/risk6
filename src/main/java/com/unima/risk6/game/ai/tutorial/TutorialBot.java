package com.unima.risk6.game.ai.tutorial;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The TutorialBot class represents an AI bot specifically designed for the game tutorial mode by
 * only performing deterministic moves.
 *
 * @author eameri
 */
public class TutorialBot extends Player implements AiBot {

  private Queue<Reinforce> deterministicClaims;
  private Map<CountryName, Country> countryMap;

  /**
   * Constructs a TutorialBot instance with a specified username.
   *
   * @param username the unique name of the bot in the game.
   */
  public TutorialBot(String username) {
    super(username);
    this.countryMap = this.initalizeMap();
    this.deterministicClaims = this.createDeterministicClaims();
  }

  /**
   * Constructs a TutorialBot instance with a default username.
   */
  public TutorialBot() {
    this("Johnny Test");
  }

  /**
   * Initializes the map for the TutorialBot by creating a mapping between CountryName and Country
   * objects.
   *
   * @return A mapping of CountryNames to their countries.
   */
  private Map<CountryName, Country> initalizeMap() {
    return GameConfiguration.configureGame(new LinkedList<>(), new LinkedList<>()).getCountries()
        .stream().collect(Collectors.toMap(Country::getCountryName, Function.identity()));
  }


  @Override
  public List<Reinforce> createAllReinforcements() {
    return List.of(
        new Reinforce(this.countryMap.get(CountryName.BRAZIL), this.getDeployableTroops()));
  }

  @Override
  public CountryPair createAttack() {
    return new CountryPair(this.countryMap.get(CountryName.BRAZIL),
        this.countryMap.get(CountryName.VENEZUELA));
  }

  @Override
  public Fortify moveAfterAttack(CountryPair winPair) {
    return new Fortify(this.countryMap.get(CountryName.BRAZIL),
        this.countryMap.get(CountryName.VENEZUELA), 1);
  }

  @Override
  public Fortify createFortify() {
    Country outgoing = this.countryMap.get(CountryName.BRAZIL);
    Country incoming = this.countryMap.get(CountryName.VENEZUELA);
    return new Fortify(outgoing, incoming, (outgoing.getTroops() - incoming.getTroops()) / 2);
  }

  /**
   * A method for a bot to claim a single country during the CLAIM PHASE Game State.
   */
  @Override
  public Reinforce claimCountry() {
    return this.deterministicClaims.poll();
  }

  /**
   * Creates a queue of deterministic country claims for the bot during the tutorial.
   *
   * @return a queue of Reinforce objects representing the bots deterministic country claims.
   */
  private Queue<Reinforce> createDeterministicClaims() {
    Queue<Reinforce> claims = new LinkedList<>();
    claims.add(new Reinforce(this.countryMap.get(CountryName.INDONESIA), 1));
    for (int i = 0; i < 8; i++) {
      claims.add(new Reinforce(this.countryMap.get(CountryName.BRAZIL), 1));
    }
    return claims;
  }

  @Override
  public boolean attackAgain() { // we always know what will happen in the tutorial
    return false;
  }

  @Override
  public int getAttackTroops(Country attacker) {
    return 3;
  }

  @Override
  public void setGameState(GameState gameState) {
    this.countryMap = gameState.getCountries().stream()
        .collect(Collectors.toMap(Country::getCountryName, Function.identity()));
  }

  public Queue<Reinforce> getDeterministicClaims() {
    return deterministicClaims;
  }

  public void setDeterministicClaims(Queue<Reinforce> deterministicClaims) {
    this.deterministicClaims = deterministicClaims;
  }
}
