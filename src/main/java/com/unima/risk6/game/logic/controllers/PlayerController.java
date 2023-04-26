package com.unima.risk6.game.logic.controllers;

import static com.unima.risk6.game.models.enums.GamePhase.NOT_ACTIVE;

import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.List;

public class PlayerController {

  private Player player;
  private final HandController handController;


  public PlayerController() {
    this.handController = new HandController();

  }

  public Reinforce sendReinforce(Country reinforcedCountry, int troopNumber) {
    return new Reinforce(reinforcedCountry, troopNumber);
  }

  public Attack sendAttack(Country attackingCountry, Country defendingCountry, int troopNumber) {

    return new Attack(attackingCountry, defendingCountry, troopNumber);
  }

  public Fortify sendFortify(Country outgoing, Country incoming, int troopNumber) {
    return new Fortify(outgoing, incoming, troopNumber);
  }

  public void changeDeployableTroops(int diff) {
    player.setDeployableTroops(player.getDeployableTroops() + diff);
  }


  public void sendHandIn() {
    if (handController.isExchangeable()) {
      HandIn handIn = new HandIn(handController.getHand().getSelectedCards());
      //removes Cards that were selected and can be exchanged
      handController.exchangeCards();
      //TODO Send HandIn Move

    }

  }

  //TODO have to implement which Continent is fully Occupied by Player


  public List<CountryPair> getValidFortifiesFromCountry(Country country) {
    List<CountryPair> fortifiable = new ArrayList<>();
    country.getAdjacentCountries().forEach((adj) -> {
      if (adj.getPlayer().equals(player)) {
        fortifiable.add(new CountryPair(country, adj));
      }
    });
    return fortifiable;
  }

  public List<CountryPair> getValidAttacksFromCountry(Country country) {
    List<CountryPair> attackable = new ArrayList<>();
    if (country.getTroops() >= 2) {
      for (Country adjacentCountry : country.getAdjacentCountries()) {
        if (!this.player.equals(adjacentCountry.getPlayer())) {
          attackable.add(new CountryPair(country, adjacentCountry));
        }
      }
    }
    return attackable;
  }


  public List<CountryPair> getAllValidFortifies() {
    List<CountryPair> countriesFortifiable = new ArrayList<>();
    for (Country country : this.player.getCountries()) {
      List<CountryPair> fortifiable = getValidFortifiesFromCountry(country);
      if (fortifiable.size() > 0) {
        countriesFortifiable.addAll(fortifiable);
      }
    }
    return countriesFortifiable;
  }

  public List<CountryPair> getAllAttackableCountryPairs(Continent continent) {
    List<CountryPair> countriesAttackable = new ArrayList<>();
    for (Country country : continent.getCountries()) {
      List<CountryPair> attackable = getValidAttacksFromCountry(country);
      if (attackable.size() > 0) {
        countriesAttackable.addAll(attackable);
      }
    }
    return countriesAttackable;
  }

  public void removeCountry(Country countryToRemove) {
    player.getCountries().remove(countryToRemove);
  }

  public void addCountry(Country countryToAdd) {
    player.getCountries().add(countryToAdd);
    countryToAdd.setPlayer(player);
  }

  public void setPlayer(Player player) {
    this.player = player;
    this.handController.setHand(player.getHand());

  }

  public Player getPlayer() {
    return player;
  }

  public HandController getHandController() {
    return handController;
  }


  public int getNumberOfCountries() {
    return player.getCountries().size();
  }

  public boolean isActive() {
    return !player.getCurrentPhase().equals(NOT_ACTIVE);
  }
}
