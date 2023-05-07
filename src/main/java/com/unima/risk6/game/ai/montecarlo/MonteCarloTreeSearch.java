package com.unima.risk6.game.ai.montecarlo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.HandController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.message.Message;
import com.unima.risk6.network.message.StandardMessage;
import com.unima.risk6.network.serialization.AttackTypeAdapter;
import com.unima.risk6.network.serialization.CardTypeAdapter;
import com.unima.risk6.network.serialization.ContinentTypeAdapter;
import com.unima.risk6.network.serialization.CountryTypeAdapter;
import com.unima.risk6.network.serialization.Deserializer;
import com.unima.risk6.network.serialization.EasyBotTypeAdapter;
import com.unima.risk6.network.serialization.EndPhaseTypeAdapter;
import com.unima.risk6.network.serialization.FortifyTypeAdapter;
import com.unima.risk6.network.serialization.GameStateTypeAdapter;
import com.unima.risk6.network.serialization.HandTypeAdapter;
import com.unima.risk6.network.serialization.HardBotTypeAdapter;
import com.unima.risk6.network.serialization.MediumBotTypeAdapter;
import com.unima.risk6.network.serialization.MonteCarloBotTypeAdapter;
import com.unima.risk6.network.serialization.PlayerTypeAdapter;
import com.unima.risk6.network.serialization.ReinforceTypeAdapter;
import com.unima.risk6.network.server.MoveProcessor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MonteCarloTreeSearch {


  private static final int SIMULATION_COUNT = 40;
  private static final int SIMULATION_TIME_LIMIT = 500; // see how much time a human has?
  private static final Gson gson = new GsonBuilder().registerTypeAdapter(GameState.class,
          new GameStateTypeAdapter()).registerTypeAdapter(Country.class, new CountryTypeAdapter())
      .registerTypeAdapter(Continent.class, new ContinentTypeAdapter())
      .registerTypeAdapter(Hand.class, new HandTypeAdapter())
      .registerTypeAdapter(Player.class, new PlayerTypeAdapter())
      .registerTypeAdapter(EasyBot.class, new EasyBotTypeAdapter())
      .registerTypeAdapter(MediumBot.class, new MediumBotTypeAdapter())
      .registerTypeAdapter(HardBot.class, new HardBotTypeAdapter())
      .registerTypeAdapter(MonteCarloBot.class, new MonteCarloBotTypeAdapter())
      .registerTypeAdapter(Attack.class, new AttackTypeAdapter())
      .registerTypeAdapter(Reinforce.class, new ReinforceTypeAdapter())
      .registerTypeAdapter(Fortify.class, new FortifyTypeAdapter())
      .registerTypeAdapter(Card.class, new CardTypeAdapter())
      .registerTypeAdapter(EndPhase.class, new EndPhaseTypeAdapter()).create();
  private final HardBot player;

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
      System.out.println(i);
      // does reassigning node in select mess up root?
      MonteCarloNode node = select(root);
      Player result = null;
      if (!node.getGameState().isGameOver() && node.getGameState().getActivePlayers()
          .contains(this.player)) {
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
    while (node.isFullyExpanded() && !node.getGameState().isGameOver() && node.getGameState()
        .getActivePlayers().contains(this.player)) {
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
    GameController simulationController = new GameController(oneTurn);
    PlayerController playerController = new PlayerController();
    playerController.setPlayer(simulationController.getCurrentPlayer());
    DeckController deckController = new DeckController(oneTurn.getDeck());
    MoveProcessor moveProcessor = new MoveProcessor(playerController, simulationController,
        deckController);
    // apply move from bots perspective
    // move get player method to node?
//    AiBot ourBot = new MonteCarloBot(this.getPlayerAtGameState(node.getGameState()));
    // the current player is our bot?
    MoveTriplet move = this.playTurn(simulationController, playerController, moveProcessor);
    int players = oneTurn.getActivePlayers().size();
    // play for all other bots
    for (int i = 0; i < players - 1; i++) {
      this.playTurn(simulationController, playerController, moveProcessor);
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
  private Player simulate(GameState game) {
    GameState simulation = this.copyGameState(game);
    long endTime = System.currentTimeMillis() + SIMULATION_TIME_LIMIT;
    GameController simulationController = new GameController(simulation);
    PlayerController playerController = new PlayerController();
    playerController.setPlayer(simulationController.getCurrentPlayer());
    DeckController deckController = new DeckController(simulation.getDeck());
    MoveProcessor moveProcessor = new MoveProcessor(playerController, simulationController,
        deckController);
    while (System.currentTimeMillis() < endTime && !simulation.isGameOver()) {
      this.playTurn(simulationController, playerController, moveProcessor);
    }
    return Probabilities.findStrongestPlayer(simulation);
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
    GameState empty = GameConfiguration.configureGame(new ArrayList<>(), new ArrayList<>());
    Message copy = Deserializer.deserialize(gson.toJson(new StandardMessage<>(gameState)), empty);
    GameState deepCopy = (GameState) copy.getContent();
    int queueSize = deepCopy.getActivePlayers().size();
    for (int i = 0; i < queueSize; i++) {
      Player toSwap = deepCopy.getActivePlayers().poll();
      AiBot replacement;
      if (!(toSwap instanceof EasyBot || toSwap instanceof MediumBot
          || toSwap instanceof MonteCarloBot)) {
        replacement = new MonteCarloBot(toSwap);
        for (Country c : toSwap.getCountries()) {
          c.setPlayer((Player) replacement);
        }
      } else {
        replacement = (AiBot) toSwap;
      }
      if (replacement instanceof MonteCarloBot) {
        ((MonteCarloBot) replacement).setContinentsCopy(deepCopy.getContinents());
        // find cleaner solution for following
      } else if (replacement instanceof EasyBot) {
        ((EasyBot) replacement).setCurrentGameState(deepCopy);
      }
      deepCopy.getActivePlayers().add((Player) replacement);

    }
    deepCopy.setCurrentPlayer(deepCopy.getActivePlayers().peek());
    return (GameState) copy.getContent();
  }

  public MoveTriplet playTurn(GameController simulationController,
      PlayerController playerController, MoveProcessor moveProcessor) {
    AiBot current = (AiBot) simulationController.getCurrentPlayer();
    boolean tooMuch = !simulationController.getGameState().getCountries().stream()
        .filter(c -> c.getTroops() <= 0).toList().isEmpty();
    if (tooMuch) {
      System.out.println("BEFORE ALL");
      simulationController.getGameState().getCountries().stream().filter(c -> c.getTroops() <= 0)
          .forEach(System.out::println);
    }
    HandController handController = playerController.getHandController();
    // TODO: add hand in to moveTriplet
    if (handController.holdsExchangeable()) {
      handController.selectExchangeableCards();
//      HandIn handIn = new HandIn(handController.getHand().getSelectedCards());
//      moveProcessor.processHandIn(handIn);
    }
    List<Reinforce> allReinforcements = current.createAllReinforcements();
    for (Reinforce reinforce : allReinforcements) {
      moveProcessor.processReinforce(reinforce);
    }
    moveProcessor.processEndPhase(new EndPhase(GamePhase.REINFORCEMENT_PHASE));
    tooMuch = !simulationController.getGameState().getCountries().stream()
        .filter(c -> c.getTroops() <= 0).toList().isEmpty();
    if (tooMuch) {
      System.out.println("REINFORCED");
      simulationController.getGameState().getCountries().stream().filter(c -> c.getTroops() <= 0)
          .forEach(System.out::println);
    }
    Queue<CountryPair> allAttacks = new LinkedList<>();
    do {
      CountryPair attacks = current.createAttack();
      // while not (one country has lost)
      if (attacks == null) {
        break;
      }
      Attack toProcess = attacks.createAttack(current.getAttackTroops(attacks.getOutgoing()));
      allAttacks.add(attacks);
      moveProcessor.processAttack(toProcess);
      if (simulationController.getGameState().isGameOver()) {
        return null;
      }
      if (toProcess.getHasConquered()) {
        moveProcessor.processFortify(current.moveAfterAttack(attacks));
      }
    } while (current.attackAgain());
    moveProcessor.processEndPhase(new EndPhase(GamePhase.ATTACK_PHASE));

    Fortify fortify = current.createFortify();
    if (fortify == null) {
      moveProcessor.processEndPhase(new EndPhase(GamePhase.FORTIFY_PHASE));
    } else {
      moveProcessor.processFortify(fortify);
    }
    return new MoveTriplet(allReinforcements, allAttacks, fortify);
  }
}
