package com.unima.risk6.game.configurations;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.models.Player;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class is used to configure and initialize the players in the game. It contains methods for
 * initializing the human players and the AI bots.
 *
 * @author astoyano
 */

public class PlayersConfiguration {

  private final List<String> users;

  private final List<AiBot> bots;

  private final Queue<Player> players;

  /**
   * This is the constructor for the PlayersConfiguration class. It takes in a list of usernames for
   * the human players and a list of AI bots. It initializes the queue of players.
   *
   * @param users A list of usernames for the human players.
   * @param bots  A list of AI bots.
   */
  public PlayersConfiguration(List<String> users, List<AiBot> bots) {
    this.users = users;
    this.bots = bots;
    players = new ConcurrentLinkedDeque<>();
  }

  /**
   * This method initializes the players and bots in the game. It calls the initPlayers() and
   * initBots() methods.
   */
  public void configure() {
    initPlayers();
    initBots();
  }

  /**
   * This method initializes the human players in the game. It loops through the list of usernames
   * and creates a new Player object for each username. The Player objects are added to the queue of
   * players.
   */
  private void initPlayers() {
    for (String user : users) {
      Player player = new Player(user);
      players.add(player);
    }
  }

  /**
   * This method initializes the AI bots in the game. It loops through the list of AI bots and casts
   * each AI bot as a Player object. The Player objects are added to the queue of players.
   */

  private void initBots() {
    for (AiBot aiBot : bots) {
      Player player = (Player) aiBot;
      players.add(player);

    }
  }

  public Queue<Player> getPlayers() {
    return players;
  }
}
