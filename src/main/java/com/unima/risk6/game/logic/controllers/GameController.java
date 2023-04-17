package com.unima.risk6.game.logic.controllers;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACKPHASE;
import static com.unima.risk6.game.models.enums.GamePhase.CLAIMPHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFYPHASE;
import static com.unima.risk6.game.models.enums.GamePhase.NOTACTIVE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENTPHASE;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.GameStateObserver;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.HashMap;
import java.util.Queue;

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
    Player lastPlayer = players.poll();
    Player nextPlayer = players.peek();
    gameState.setCurrentPlayer(nextPlayer);
    nextPhase();
    players.add(lastPlayer);
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

  public void processFortify(Fortify fortify) {
    addLastMove(fortify);
    fortify.getIncoming().changeTroops(fortify.getTroopsToMove());
    fortify.getOutgoing().changeTroops(-fortify.getTroopsToMove());


  }

  public void processReinforce(Reinforce reinforce) {
    addLastMove(reinforce);
    if (gameState.getCurrentPlayer().getCurrentPhase().equals(CLAIMPHASE)) {
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

  //TODO Reinforcephase Automation in Process Reinforce
  public GamePhase nextPhase() {
    Player player = gameState.getCurrentPlayer();
    switch (player.getCurrentPhase()) {
      case REINFORCEMENTPHASE -> {
        if (player.getDeployableTroops() == 0) {
          player.setCurrentPhase(ATTACKPHASE);
          return ATTACKPHASE;
        } else {
          return REINFORCEMENTPHASE;
          //TODO exception or error which should be given to UI
        }
      }
      case ATTACKPHASE -> player.setCurrentPhase(FORTIFYPHASE);
      case FORTIFYPHASE, CLAIMPHASE -> {
        player.setCurrentPhase(NOTACTIVE);
        nextPlayer();
      }
      case NOTACTIVE -> {
        if (player.getInitialTroops() > 0) {
          player.setCurrentPhase(CLAIMPHASE);
        } else {
          player.setCurrentPhase(REINFORCEMENTPHASE);
        }
      }
      default -> {
      }
    }
    return player.getCurrentPhase();
  }
}