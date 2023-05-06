package com.unima.risk6.game.logic.controllers;

import static com.unima.risk6.game.models.enums.GamePhase.NOT_ACTIVE;

import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.client.GameClient;
import com.unima.risk6.network.message.StandardMessage;
import java.util.ArrayList;
import java.util.List;

public class PlayerController {

  private Player player;
  private final HandController handController;

  private final GameClient gameClient;

  public PlayerController() {
    this.handController = new HandController();
    this.gameClient = LobbyConfiguration.getGameClient();

  }

  public void sendReinforce(Country reinforcedCountry, int troopNumber) {
    Reinforce reinforce = new Reinforce(reinforcedCountry, troopNumber);
    gameClient.sendMessage(new StandardMessage<Reinforce>(reinforce));
  }

  public void sendAttack(Country attackingCountry, Country defendingCountry, int troopNumber) {
    Attack attack = new Attack(attackingCountry, defendingCountry, troopNumber);
    gameClient.sendMessage(new StandardMessage<Attack>(attack));
  }

  public void sendFortify(Country outgoing, Country incoming, int troopNumber) {
    Fortify fortify = new Fortify(outgoing, incoming, troopNumber);
    gameClient.sendMessage(new StandardMessage<Fortify>(fortify));
  }


  public void setEndPhase(GamePhase gamePhase) {
    EndPhase endPhase = new EndPhase(gamePhase);
    gameClient.sendMessage(new StandardMessage<EndPhase>(endPhase));
  }

  public void changeDeployableTroops(int diff) {
    player.setDeployableTroops(player.getDeployableTroops() + diff);
  }


  public void sendHandIn() {
    if (handController.isExchangeable()) {
      HandIn handIn = new HandIn(handController.getHand().getSelectedCards());
      //removes Cards that were selected and can be exchanged
      handController.exchangeCards();
      gameClient.sendMessage(new StandardMessage<HandIn>(handIn));
    }

  }


  public List<CountryPair> getValidFortifiesFromCountry(Country country) {
    List<CountryPair> fortifiable = new ArrayList<>();
    if (country.getTroops() > 1) {
      country.getAdjacentCountries().forEach((adj) -> {
        if (this.player.equals(adj.getPlayer())) {
          fortifiable.add(new CountryPair(country, adj));
        }
      });
    }
    return fortifiable;
  }

  public List<CountryPair> getValidCountryPairsFromCountry(Country country) {
    List<CountryPair> attackable = new ArrayList<>();
    if (country.getTroops() >= troopLimitPerPhase()) {
      for (Country adjacentCountry : country.getAdjacentCountries()) {
        if (!this.player.equals(adjacentCountry.getPlayer()) && adjacentCountry.hasPlayer()) {
          attackable.add(new CountryPair(country, adjacentCountry));
        }
      }
    }
    return attackable;
  }

  private int troopLimitPerPhase() {
    return this.player.getCurrentPhase() == GamePhase.REINFORCEMENT_PHASE
        || this.player.getCurrentPhase() == GamePhase.CLAIM_PHASE ? 1 : 2;
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

  public List<CountryPair> getAllValidCountryPairs(Continent continent) {
    List<CountryPair> countriesAttackable = new ArrayList<>();
    for (Country country : continent.getCountries()) {
      List<CountryPair> attackable = new ArrayList<>();
      if (this.player.equals(country.getPlayer())) {
        attackable = getValidCountryPairsFromCountry(country);
      }
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
