package com.unima.risk6.game.ai.montecarlo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.network.serialization.AttackTypeAdapter;
import com.unima.risk6.network.serialization.ContinentTypeAdapter;
import com.unima.risk6.network.serialization.CountryTypeAdapter;
import com.unima.risk6.network.serialization.EasyBotTypeAdapter;
import com.unima.risk6.network.serialization.GameStateTypeAdapter;
import com.unima.risk6.network.serialization.HandTypeAdapter;
import com.unima.risk6.network.serialization.PlayerTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MonteCarloTreeSearch {


  private static final int SIMULATION_COUNT = 200;
  private static final int SIMULATION_TIME_LIMIT = 500; // see how much time a human has?
  private static final Random RNG = new Random();
  private static final Gson gson = new GsonBuilder().registerTypeAdapter(GameState.class,
          new GameStateTypeAdapter()).registerTypeAdapter(Country.class, new CountryTypeAdapter())
      .registerTypeAdapter(Continent.class, new ContinentTypeAdapter())
      .registerTypeAdapter(Hand.class, new HandTypeAdapter())
      .registerTypeAdapter(Player.class, new PlayerTypeAdapter())
      .registerTypeAdapter(EasyBot.class, new EasyBotTypeAdapter())
      .registerTypeAdapter(Attack.class, new AttackTypeAdapter()).create();
  private final HardBot player;
  private int treeDepth;

  /**
   * Constructor for MonteCarloTreeSearch.
   *
   * @param player The HardBot player that will use this Monte Carlo Tree Search algorithm.
   */
  public MonteCarloTreeSearch(HardBot player) {
    this.player = player;
  }

  /**
   * Returns the best move for the given game state based on Monte Carlo Tree Search algorithm.
   *
   * @param game The current game state.
   * @return The best move for the current game state.
   */
  public MoveTriplet getBestMove(GameState game) {
    MonteCarloNode root = new MonteCarloNode(game, null);
    for (int i = 0; i < SIMULATION_COUNT; i++) {
      // does reassigning node in select mess up root?
      MonteCarloNode node = select(root);
      Player result = null;
      if (!node.getGameState().isGameOver()) {
        node = expand(node);
        result = simulate(node.getGameState());
      }
      backpropagate(node, result);
    }
    return chooseBestMove(root);
  }

  /**
   * Selects the most promising node from the tree based on UCT values.
   *
   * @param node The root node of the Monte Carlo Tree.
   * @return The most promising node.
   */
  private MonteCarloNode select(MonteCarloNode node) {
    // goes to specific depth?
    // keep traversing non-terminal states, finding a leaf node <-> not expanded fully
    while (node.isFullyExpanded() && !node.getGameState().isGameOver()) {
      node = node.getBestChild();
    }
    return node;
  }

  /**
   * Expands the given node by creating a child node with one turn's worth of moves.
   *
   * @param node The node in the Monte Carlo Tree whose child is to be created.
   * @return The created child node.
   */
  private MonteCarloNode expand(MonteCarloNode node) { // choose only best moves
    GameState oneTurn = this.copyGameState(node.getGameState());
    // apply move from bots perspective
    // move get player method to node?
    MonteCarloBot ourBot = new MonteCarloBot(this.getPlayerAtGameState(node.getGameState()));
    List<Reinforce> reinforcements = ourBot.getReinforceMoves().subList(0,
        Math.min(RNG.nextInt(ourBot.getReinforceMoves().size()),
            ourBot.getReinforceMoves().size()));
    // perform reinforcements, now the get attack move will be based off of the state after
    // randomly select reinforcements until no more troops deployable
    // ^ new method, keeps selecting, if troops still available verteil them randomly
    List<CountryPair> attacks = ourBot.getAttackMoves()
        .subList(0, Math.min(RNG.nextInt(4), ourBot.getAttackMoves().size()));
    // perform attacks, then we are ready for fortify
    Fortify fortify = ourBot.getFortifyMoves().get(RNG.nextInt(ourBot.getFortifyMoves().size()));
    // perform fortify
    MoveTriplet move = new MoveTriplet(reinforcements, attacks, fortify);
    int players = oneTurn.getActivePlayers().size();
    // play for all other bots
    for (int i = 0; i < players - 1; i++) {
      Player player = oneTurn.getCurrentPlayer();
      // perform moves
      // changing the queue should be handled by server move performer method
    }
    return new MonteCarloNode(oneTurn, move, node);
  }

  /**
   * Simulates the game from the given game state, with all players making moves akin to their skill
   * until a certain stop condition is met.
   *
   * @param game The GameState from which the simulation will begin.
   * @return The strongest player once the game has stopped being simulated.
   */
  private Player simulate(GameState game) { // here: mediumbots that keep playing
    // simulation gets stopped after time period
    GameState simulation = this.copyGameState(game);
    long endTime = System.currentTimeMillis() + SIMULATION_TIME_LIMIT;
    // if bot -> createMove, if hardbot or human -> random move
    while (System.currentTimeMillis() < endTime && !simulation.isGameOver()) {
      Player current = simulation.getCurrentPlayer();
      List<MoveTriplet> legalMoves = this.getSimulationMoves(current);
      MoveTriplet randomMove = legalMoves.get(RNG.nextInt(legalMoves.size()));
//      simulation.applyMove(randomMove);
    }

    return Probabilities.findStrongestPlayer(simulation);
  }

  /**
   * Returns a list of legal moves for the given player during the simulation.
   *
   * @param player The player whose legal moves are to be retrieved.
   * @return A list of legal moves for the given player.
   */
  private List<MoveTriplet> getSimulationMoves(Player player) {
    List<MoveTriplet> legalMoves = new ArrayList<>();
    if (this.isMonteCarlo(player)) {
      legalMoves = ((MonteCarloBot) player).getLegalMoves();
    } else {
      // legalMoves.add(((AiBot) player).makeMove());
    }
    return legalMoves;
  }

  /**
   * Checks if the given player is an instance of MonteCarloBot.
   *
   * @param player The player to be checked.
   * @return A boolean value indicating whether the player is an instance of MonteCarloBot.
   */
  private boolean isMonteCarlo(Player player) {
    return !(player instanceof EasyBot || player instanceof MediumBot);
  }

  /**
   * Updates the wins and visits of the nodes in the Monte Carlo Tree based on the simulation
   * result.
   *
   * @param node   The leaf node from which backpropagation will start.
   * @param result The strongest player after the simulation.
   */
  private void backpropagate(MonteCarloNode node, Player result) {
    while (node != null) {
      node.incrementVisits();
      if (this.player.equals(result)) {
        node.incrementWins();
      }
      node = node.getParent();
    }
  }

  /**
   * Chooses the best move from the children of the given node based on the maximum number of
   * visits.
   *
   * @param node The root node of the Monte Carlo Tree.
   * @return The best move to make for the current game state.
   */
  private MoveTriplet chooseBestMove(MonteCarloNode node) {
    MonteCarloNode bestChild = null;
    int maxVisits = Integer.MIN_VALUE;
    for (MonteCarloNode child : node.getChildren()) { // all children signify a move made by the
      // HardBot
      if (child.getVisits() > maxVisits) {
        maxVisits = child.getVisits();
        bestChild = child;
      }
    }
    return bestChild.getMove();
  }

  /**
   * Creates a deep copy of the given game state.
   *
   * @param gameState The game state to be copied.
   * @return A deep copy of the given game state.
   */
  public GameState copyGameState(GameState gameState) {
    GameState copy = gson.fromJson(gson.toJson(gameState), gameState.getClass());
    // swap players
    Map<String, Player> playerMap = new HashMap<>();
    for (Player player : copy.getActivePlayers()) {
      // players/ hardbots may make different choices, assume others do as they usually do
      if (!(player instanceof EasyBot || player instanceof MediumBot)) {
        player = new MonteCarloBot(player);
      }
      playerMap.put(player.getUser(), player);
    }
    // make changes in countries and current player
    for (Country country : copy.getCountries()) {
      country.setPlayer(playerMap.get(country.getPlayer().getUser()));
    }
    copy.setCurrentPlayer(playerMap.get(copy.getCurrentPlayer().getUser()));

    return copy;
  }

  /**
   * Retrieves the player object of the HardBot in the given game state.
   *
   * @param gameState The current game state.
   * @return The player object of the HardBot in the given game state, or null if the player is no
   * longer in the game.
   */
  private Player getPlayerAtGameState(GameState gameState) {
    for (Player player : gameState.getActivePlayers()) {
      if (player.equals(this.player)) {
        return player;
      }
    }
    return null; // signifies that player is no longer in game
  }

}
