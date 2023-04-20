package com.unima.risk6.game.logic.controllers;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.CLAIM_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.NOT_ACTIVE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.GameStateObserver;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

//TODO mach das kompatibel zu controller
public class GameController implements GameStateObserver {

  private GameState gameState;

  private boolean hasConquered;
  private HashMap<Player, Integer> initialTroops;
  private final Queue<Player> players;

  public GameController(GameState gameState) {
    this.gameState = gameState;
    this.players = gameState.getActivePlayers();
    GameConfiguration.addObserver(this);
  }

  public void nextPlayer() {
    if (hasConquered) {
      drawCard();
    }
    Player lastPlayer = players.poll();
    Player nextPlayer = players.peek();
    gameState.setCurrentPlayer(nextPlayer);
    nextPhase();
    players.add(lastPlayer);
    hasConquered = false;
  }

  public void removeLostPlayer(Player loser) {
    players.remove(loser);
    if (!loser.getHand().getCards().isEmpty()) {
      //the Cards of the Players who lost get transferred to the Player who conquered them
      takeOverCardFromLostPlayer(loser);
    }
  }

  public void takeOverCardFromLostPlayer(Player lostPlayer) {
    gameState.getCurrentPlayer().getHand()
        .getCards()
        .addAll(lostPlayer.getHand().getCards());
  }
  //TODO Server must process it in another class with player controller
 /* public void processAttack(Attack attack) {

    Country attackingCountry = attack.getAttackingCountry();
    Country defendingCountry = attack.getDefendingCountry();
    Player attacker = attackingCountry.getPlayer();
    Player defender = defendingCountry.getPlayer();

    attack.calculateLosses();
    addLastMove(attack);
    attackingCountry.changeTroops(-attack.getAttackerLosses());
    defendingCountry.changeTroops(-attack.getDefenderLosses());
    if (defendingCountry.getTroops() == 0) {
      hasConquered=true;
      defendingCountry.setPlayer(attacker);
      attacker.addCountry(defendingCountry);
      defender.removeCountry(defendingCountry);

      //Forced Fortify after attack and takeover
      Fortify forcedFortify = new Fortify(attackingCountry, defendingCountry,
          attack.getTroopNumber());
      addLastMove(forcedFortify);
      processFortify(forcedFortify);

    }
    if (defender.getNumberOfCountries() == 0) {
      removeLostPlayer(defender);
    }

  } */

  public Queue<Player> setPlayerOrder(HashMap<Player, Integer> diceRolls) {

    Set<Entry<Player, Integer>> entrySet = diceRolls.entrySet();
    Queue<Player> order = new ConcurrentLinkedQueue<>();
    for (int i = 6; i >= 1; i--) {

      for (Entry<Player, Integer> entry : entrySet) {
        if (entry.getValue().equals(i)) {
          order.add(entry.getKey());
        }
      }
    }
    return order;


  }

  public void processFortify(Fortify fortify) {
    addLastMove(fortify);
    fortify.getIncoming().changeTroops(fortify.getTroopsToMove());
    fortify.getOutgoing().changeTroops(-fortify.getTroopsToMove());


  }

  public void processReinforce(Reinforce reinforce) {
    addLastMove(reinforce);
    if (gameState.getCurrentPlayer().getCurrentPhase().equals(CLAIM_PHASE)) {
      reinforce.getCountry().setPlayer(gameState.getCurrentPlayer());
      //TODO Player controller by server use
      //gameState.getCurrentPlayer().addCountry(reinforce.getCountry());
      reinforce.getCountry().setTroops(1);

    } else {
      reinforce.getCountry().changeTroops(reinforce.getToAdd());
    }
  }


  public void addLastMove(Move move) {
    this.gameState.getLastMoves().add(move);
  }

  @Override
  public void update(GameState gameState) {
    this.gameState = gameState;
  }

  //TODO Reinforcephase Automation in Process Reinforce IN SERVER
  public GamePhase nextPhase() {
    Player player = gameState.getCurrentPlayer();
    switch (player.getCurrentPhase()) {
      case REINFORCEMENT_PHASE -> {
        if (player.getDeployableTroops() == 0) {
          player.setCurrentPhase(ATTACK_PHASE);
          return ATTACK_PHASE;
        } else {
          return REINFORCEMENT_PHASE;
          //TODO exception or error which should be given to UI
        }
      }
      case ATTACK_PHASE -> player.setCurrentPhase(FORTIFY_PHASE);
      case FORTIFY_PHASE, CLAIM_PHASE -> {
        player.setCurrentPhase(NOT_ACTIVE);
        nextPlayer();
      }
      case NOT_ACTIVE -> {
        if (player.getInitialTroops() > 0) {
          player.setCurrentPhase(CLAIM_PHASE);
        } else {
          player.setCurrentPhase(REINFORCEMENT_PHASE);
        }
      }
      default -> {
      }
    }
    return player.getCurrentPhase();
  }

  public void drawCard() {
    Card drawnCard = gameState.getDeck().getDeckCards().remove(0);
    gameState.getCurrentPlayer().getHand().getCards().add(drawnCard);
  }


  public void calculateDeployableTroops() {
    Player currentPlayer = gameState.getCurrentPlayer();
    this.updateContinentsOfCurrentPlayer(gameState.getContinents());
    currentPlayer.setDeployableTroops(3);
    int n = currentPlayer.getCountries().size();
    if (n > 8) {
      n = n - 9;
      currentPlayer.setDeployableTroops(Math.floorDiv(n, 3));
    }
    currentPlayer.getContinents().forEach((x) -> currentPlayer.setDeployableTroops(
        currentPlayer.getDeployableTroops() + x.getBonusTroops()));
  }

  public void updateContinentsOfCurrentPlayer(Set<Continent> continents) {
    continents.forEach((n) -> {
      Set<Country> countries = gameState.getCurrentPlayer().getCountries();
      if (countries.containsAll(n.getCountries())) {
        gameState.getCurrentPlayer().getContinents().add(n);
      }
    });

  }

}