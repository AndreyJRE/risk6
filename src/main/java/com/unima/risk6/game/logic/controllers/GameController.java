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
  private Queue<Player> players;
  private Set<Country> countries;
  private HashMap<Player, Integer> initialTroops;

  public GameController(GameState gameState) {
    this.gameState = gameState;
    players = gameState.getActivePlayers();
    countries = gameState.getCountries();
    GameConfiguration.addObserver(this);
  }

  public void nextPhase() {
    //TODO Mach so, dass das geht.

  }

  public void nextPlayer() {
    /*


    Player lastPlayer = players.poll();
    lastPlayer.nextPhase();
    Player nextPlayer = players.peek();
    gameState.setCurrentPlayer(nextPlayer);
    gameState.setCurrentPhase(nextPlayer.nextPhase());
    players.add(lastPlayer);
    }
     */

  }

  public void removeLostPlayer(Player loser) {
    Queue<Player> players = gameState.getActivePlayers();

    players.remove(loser);

    if (!loser.getHand().getCards().isEmpty()) {
      //the Cards of the Players who lost get transferred to the Player who conquered them
      takeOverCardFromLostPlayer(loser);
    }
  }

  public void processAttack(Attack attack) {

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

  }

  public void processFortify(Fortify fortify) {
    addLastMove(fortify);
    fortify.getIncoming().changeTroops(fortify.getTroopsToMove());
    fortify.getOutgoing().changeTroops(-fortify.getTroopsToMove());


  }

  public void processReinforce(Reinforce reinforce) {
    addLastMove(reinforce);
    if (gameState.getCurrentPlayer().getCurrentPhase().equals(CLAIMPHASE)) {
      reinforce.getCountry().setPlayer(gameState.getCurrentPlayer());
      gameState.getCurrentPlayer().addCountry(reinforce.getCountry());
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
/*
  public GamePhase nextPhase() {

    switch (player.getCurrentPhase()) {
      case REINFORCEMENTPHASE:
        if (player.getDeployableTroops() == 0) {
          player.setPhase(ATTACKPHASE);
          return ATTACKPHASE;
        } else {
          return REINFORCEMENTPHASE;
          //TODO exception or error which should be given to UI
        }

      case ATTACKPHASE:
        player.setPhase(FORTIFYPHASE);
        break;
      case FORTIFYPHASE, CLAIMPHASE:
        player.setPhase(NOTACTIVE);
        break;
      case NOTACTIVE:
        if (player.getInitialTroops() > 0) {
          player.setPhase(CLAIMPHASE);
        } else {
          player.setPhase(REINFORCEMENTPHASE);
        }
        break;

      default:
        break;
    }
    return player.getCurrentPhase();
  }



 */
}