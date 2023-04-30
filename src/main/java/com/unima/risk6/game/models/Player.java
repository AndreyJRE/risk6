package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.GamePhase;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Player {

  private Hand hand;
  private final Set<Country> countries;
  private final Set<Continent> continents;
  private final String user;
  private int deployableTroops;
  private int initialTroops;
  private final Statistic statistic;
  private GamePhase currentPhase;
  private boolean hasConquered;

  public Player() {
    this.hand = new Hand();
    countries = new HashSet<>();
    continents = new HashSet<>();
    this.user = null;
    this.statistic = new Statistic();
    this.currentPhase = GamePhase.ORDER_PHASE;
  }

  public Player(String user) {
    this.hand = new Hand();
    this.countries = new HashSet<>();
    this.continents = new HashSet<>();
    this.user = user;
    this.statistic = new Statistic();
    this.currentPhase = GamePhase.ORDER_PHASE;
  }

  public Player(Player toCopy) {
    // only to be used by AI!
    this.hand = toCopy.getHand();
    this.countries = toCopy.getCountries();
    this.continents = toCopy.getContinents();
    this.user = toCopy.getUser();
    this.deployableTroops = toCopy.getDeployableTroops();
    this.initialTroops = toCopy.getInitialTroops();
    this.statistic = null;
    this.currentPhase = toCopy.getCurrentPhase();
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player player)) {
      return false;
    }
    return Objects.equals(getUser(), player.getUser());
  }

  public void setHand(Hand hand) {
    this.hand = hand;
  }

  public boolean isHasConquered() {
    return hasConquered;
  }

  public void setHasConquered(boolean hasConquered) {
    this.hasConquered = hasConquered;
  }
}

