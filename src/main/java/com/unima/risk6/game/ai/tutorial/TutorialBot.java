package com.unima.risk6.game.ai.tutorial;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Country;
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
 * only performing deterministic moves
 *
 * @author eameri
 */
public class TutorialBot extends Player implements AiBot {

  private final Queue<Reinforce> deterministicClaims;
  private final Queue<Reinforce> deterministicReinforces;
  private final Queue<CountryPair> deterministicAttacks;
  private final Queue<Fortify> deterministicAfterAttacks;
  private final Queue<Fortify> deterministicFortifies;
  private final Map<CountryName, Country> countryMap;

  /**
   * Constructs a TutorialBot instance with a specified username.
   *
   * @param username the unique name of the bot in the game.
   */
  public TutorialBot(String username) {
    super(username);
    this.deterministicClaims = this.createDeterministicClaims();
    this.deterministicReinforces = this.createDeterministicReinforces();
    this.deterministicAttacks = this.createDeterministicAttacks();
    this.deterministicAfterAttacks = this.createDeterministicAfterAttacks();
    this.deterministicFortifies = this.createDeterministicFortifies();
    this.countryMap = this.initalizeMap();
  }

  /**
   * Creates a queue of deterministic after-attack moves for the bot during the tutorial.
   *
   * @return a queue of Fortify objects representing the bot's deterministic after-attack moves.
   */
  private Queue<Fortify> createDeterministicAfterAttacks() {
    Queue<Fortify> afterAttacks = new LinkedList<>();
    afterAttacks.add(new Fortify(this.countryMap.get(CountryName.BRAZIL),
        this.countryMap.get(CountryName.VENEZUELA), 1));
    return afterAttacks;
  }

  /**
   * Creates a queue of deterministic fortification moves for the bot during the tutorial.
   *
   * @return a queue of Fortify objects representing the bot's deterministic fortification moves.
   */
  private Queue<Fortify> createDeterministicFortifies() {
    Queue<Fortify> fortifies = new LinkedList<>();
    fortifies.add(
        new Fortify(this.countryMap.get(CountryName.PERU), this.countryMap.get(CountryName.BRAZIL),
            2));
    return fortifies;
  }

  /**
   * Creates a queue of deterministic attack moves for the bot during the tutorial.
   *
   * @return a queue of CountryPair objects representing the bot's deterministic attack moves.
   */
  private Queue<CountryPair> createDeterministicAttacks() {
    Queue<CountryPair> attacks = new LinkedList<>();
    attacks.add(new CountryPair(this.countryMap.get(CountryName.VENEZUELA),
        this.countryMap.get(CountryName.PERU)));
    return attacks;
  }

  /**
   * Creates a queue of deterministic reinforcement moves for the bot during the tutorial.
   *
   * @return a queue of Reinforce objects representing the bot's deterministic reinforcement moves.
   */
  private Queue<Reinforce> createDeterministicReinforces() {
    Queue<Reinforce> reinforces = new LinkedList<>();
    reinforces.add(new Reinforce(this.countryMap.get(CountryName.VENEZUELA), 3));
    return reinforces;
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
    List<Reinforce> nextReinforce = new LinkedList<>();
    nextReinforce.add(this.deterministicReinforces.poll());
    return nextReinforce;
  }

  @Override
  public List<CountryPair> createAllAttacks() {
    List<CountryPair> nextAttack = new LinkedList<>();
    nextAttack.add(this.deterministicAttacks.poll());
    return nextAttack;
  }

  @Override
  public Fortify moveAfterAttack(CountryPair winPair) {
    return this.deterministicAfterAttacks.poll();
  }

  @Override
  public Fortify createFortify() {
    return this.deterministicFortifies.poll();
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
   * @return a queue of Reinforce objects representing the bot's deterministic country claims.
   */
  private Queue<Reinforce> createDeterministicClaims() {
    Queue<Reinforce> claims = new LinkedList<>();
    claims.add(new Reinforce(this.countryMap.get(CountryName.BRAZIL), 1));
    return claims;
  }

  @Override
  public boolean attackAgain() { // we always know what will happen in the tutorial
    return false;
  }
}
