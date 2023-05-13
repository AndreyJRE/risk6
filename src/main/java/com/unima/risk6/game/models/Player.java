package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.GamePhase;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a player in the game with its troops it can place, its username, its hand, its
 * statistic, the currentPhase the player is in and whether it has conquered something in its attack
 * phase. It also contains references to the countries and continents that are owned by the player.
 *
 * @author wphung
 */
public class Player {

  private Hand hand;
  private final Set<Country> countries;
  private final Set<Continent> continents;
  private final String user;
  private int deployableTroops;
  private int initialTroops;
  private Statistic statistic;
  private GamePhase currentPhase;
  private boolean hasConquered;

  /**
   * Constructs a new player without username.
   */
  public Player() {
    this.hand = new Hand();
    countries = new HashSet<>();
    continents = new HashSet<>();
    this.user = null;
    this.statistic = new Statistic();
    this.currentPhase = GamePhase.ORDER_PHASE;
  }

  /**
   * Constructs a PLayer object if given the username.
   *
   * @param user the name of the user that is represented by this player object
   */
  public Player(String user) {
    this.hand = new Hand();
    this.countries = new HashSet<>();
    this.continents = new HashSet<>();
    this.user = user;
    this.statistic = new Statistic();
    this.currentPhase = GamePhase.ORDER_PHASE;
  }

  /**
   * Constructs a PLayer object that is a copy of the given player.
   *
   * @param toCopy the player object to be copied
   */
  public Player(Player toCopy) {
    // only to be used by AI!
    this.hand = toCopy.getHand();
    this.countries = toCopy.getCountries();
    this.continents = toCopy.getContinents();
    this.user = toCopy.getUser();
    this.deployableTroops = toCopy.getDeployableTroops();
    this.initialTroops = toCopy.getInitialTroops();
    this.statistic = new Statistic();
    this.currentPhase = toCopy.getCurrentPhase();
    this.hasConquered = toCopy.getHasConquered();
  }

  public Set<Continent> getContinents() {
    return continents;
  }


  public Set<Country> getCountries() {
    return countries;
  }

  public Hand getHand() {
    return hand;
  }

  public void setHand(Hand hand) {
    this.hand = hand;
  }

  public int getInitialTroops() {
    return initialTroops;
  }

  public void setInitialTroops(int initialTroops) {
    this.initialTroops = initialTroops;
  }

  public int getDeployableTroops() {
    return deployableTroops;
  }

  public void setDeployableTroops(int deployableTroops) {
    this.deployableTroops = deployableTroops;
  }

  public String getUser() {
    return user;
  }

  public Statistic getStatistic() {
    return statistic;
  }

  public GamePhase getCurrentPhase() {
    return currentPhase;
  }

  public void setCurrentPhase(GamePhase currentPhase) {
    this.currentPhase = currentPhase;
  }

  @Override
  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append(this.getUser() + " deployableTroops: " + deployableTroops + " initial Troops: "
        + initialTroops + "\n" + this.currentPhase + "| ");
    countries.stream()
        .forEach(n -> s.append(n.getCountryName() + ": " + n.getTroops() + "| "));
    getHand().getCards().forEach(s::append);

    s.append("\n--------------");

    return s.toString();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player player)) {
      return false;
    }
    return Objects.equals(getUser(), player.getUser());
  }

  public boolean getHasConquered() {
    return hasConquered;
  }

  public void setHasConquered(boolean hasConquered) {
    this.hasConquered = hasConquered;
  }

  public void setStatistic(Statistic statistic) {
    this.statistic = statistic;
  }
}

