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
import com.unima.risk6.game.logic.HandIn;
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

/**
 * An implementation of the MCTS Algorithm to be used by the Hard Bot for decision-making. The four
 * main steps of selection, expansion, simulation, and backpropagation are implemented, allowing the
 * Hard Bot to consider future states of the game in order to make an informed decision on its next
 * move.
 *
 * @author eameri
 */
public class MonteCarloTreeSearch {

  private static int simulationCount = 150; // best choice is more, but shorter simulations
  private static final int SIMULATION_TIME_LIMIT = 100;
  private static final double STRENGTH_WEIGHT = 0.5;
  private static final double COUNTRY_WEIGHT = 0.2;
  private static final double CONTINENT_WEIGHT = 0.3;

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
    if (game.getActivePlayers().size() + game.getLostPlayers().size() > 3) {
      simulationCount = simulationCount / 2;
    }
    for (int i = 0; i < simulationCount; i++) {
      MonteCarloNode node = select(root);
      double oldStrength = this.calculateGameStateScore(node.getGameState());
      double newStrength = 0;
      if (!node.getGameState().isGameOver() && node.getGameState().getActivePlayers()
          .contains(this.player)) {
        node = expand(node);
        newStrength = simulate(node.getGameState());
      }
      backpropagate(node, newStrength > oldStrength);
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
    AiBot hardBot = (AiBot) simulationController.getCurrentPlayer();
    DeckController deckController = new DeckController(oneTurn.getDeck());
    MoveProcessor moveProcessor = new MoveProcessor(playerController, simulationController,
        deckController);
    List<Reinforce> reinforcements = new ArrayList<>();
    Queue<CountryPair> attacks = new LinkedList<>();
    Fortify fortify = null;
    if (((Player) hardBot).getCurrentPhase() == GamePhase.REINFORCEMENT_PHASE) {
      reinforcements = simulateReinforcements(moveProcessor, hardBot);
    }
    if (((Player) hardBot).getCurrentPhase() == GamePhase.ATTACK_PHASE) {
      attacks = simulateAttacks(simulationController, moveProcessor, hardBot);
    }
    if (((Player) hardBot).getCurrentPhase() == GamePhase.FORTIFY_PHASE) {
      fortify = simulateFortify(moveProcessor, hardBot);
    }
    MoveTriplet decision = new MoveTriplet(reinforcements, attacks, fortify);
    int playerCount = oneTurn.getActivePlayers().size();
    for (int i = 0; i < playerCount - 1; i++) {
      this.playTurn(simulationController, playerController, moveProcessor);
    }
    return new MonteCarloNode(oneTurn, decision, node);
  }


  /**
   * Simulates the game from the given game state, with all players making moves akin to their skill
   * until a certain stop condition is met.
   *
   * @param game The GameState from which the simulation will begin.
   * @return The strength of the current player once the simulation has ended.
   */
  private double simulate(GameState game) {
    GameState simulation = this.copyGameState(game);
    GameController simulationController = new GameController(simulation);
    PlayerController playerController = new PlayerController();
    playerController.setPlayer(simulationController.getCurrentPlayer());
    DeckController deckController = new DeckController(simulation.getDeck());
    MoveProcessor moveProcessor = new MoveProcessor(playerController, simulationController,
        deckController);
    long endTime = System.currentTimeMillis() + SIMULATION_TIME_LIMIT;
    while (System.currentTimeMillis() < endTime && !simulation.isGameOver()) {
      this.playTurn(simulationController, playerController, moveProcessor);
    }
    return this.calculateGameStateScore(simulation);
  }

  /**
   * Updates the wins and visits of the nodes in the Monte Carlo Tree based on the simulation
   * result.
   *
   * @param node   The leaf node from which backpropagation will start.
   * @param result If making those moves resulted in an improvement of the HardBot's situation.
   */
  private void backpropagate(MonteCarloNode node, boolean result) {
    while (node != null) {
      node.incrementVisits();
      if (result) {
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
    for (MonteCarloNode child : node.getChildren()) { // all children are moves made by the HardBot
      if (child.getVisits() > maxVisits) {
        maxVisits = child.getVisits();
        bestChild = child;
      }
    }
    return bestChild.getMove();
  }

  /**
   * Creates a deep copy of the given game state with all human players replaced by MonteCarloBots.
   *
   * @param gameState The game state to be copied.
   * @return A deep copy of the given game state with all human players replaced by MonteCarloBots.
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
      replacement.setGameState(deepCopy);
      deepCopy.getActivePlayers().add((Player) replacement);

    }
    deepCopy.setCurrentPlayer(deepCopy.getActivePlayers().peek());
    return deepCopy;
  }

  /**
   * Plays one entire turn (reinforce, attack, fortify) of a bot.
   *
   * @param simulationController The GameController of the game state being played.
   * @param playerController     The player controller to control the current player.
   * @param moveProcessor        The Move Processor used to process moves in each phase.
   */
  public void playTurn(GameController simulationController, PlayerController playerController,
      MoveProcessor moveProcessor) {
    AiBot current = (AiBot) simulationController.getCurrentPlayer();
    HandController handController = playerController.getHandController();
    simulateHandIn(handController, moveProcessor);
    simulateReinforcements(moveProcessor, current);
    simulateAttacks(simulationController, moveProcessor, current);

    if (!simulationController.getGameState().isGameOver()) {
      simulateFortify(moveProcessor, current);
    }
  }


  /**
   * Simulates checking to see if cards can be handed in and does so if possible.
   *
   * @param handController The Hand controller used to control the hand of the current player.
   * @param moveProcessor  The Move Processor used to process the hand-in.
   */
  private static void simulateHandIn(HandController handController, MoveProcessor moveProcessor) {
    if (handController.holdsExchangeable()) {
      handController.selectExchangeableCards();
      HandIn handIn = new HandIn(handController.getHand().getSelectedCards());
      moveProcessor.processHandIn(handIn);
    }
  }

  /**
   * Simulates an entire reinforcement phase of a bot player.
   *
   * @param moveProcessor The Move Processor used to process the reinforcements.
   * @param current       The player which is currently active.
   * @return The list of reinforcements chosen by the bot.
   */
  private static List<Reinforce> simulateReinforcements(MoveProcessor moveProcessor,
      AiBot current) {
    List<Reinforce> allReinforcements = current.createAllReinforcements();
    for (Reinforce reinforce : allReinforcements) {
      moveProcessor.processReinforce(reinforce);
    }
    moveProcessor.processEndPhase(new EndPhase(GamePhase.REINFORCEMENT_PHASE));
    return allReinforcements;
  }

  /**
   * Simulates an entire attack phase of a bot player.
   *
   * @param simulationController The game controller of the current game state.
   * @param moveProcessor        The Move Processor which will be used to process the attacks.
   * @param current              The player which is currently active.
   * @return All pairs of countries involved in attacks in order.
   */
  private static Queue<CountryPair> simulateAttacks(GameController simulationController,
      MoveProcessor moveProcessor, AiBot current) {
    Queue<CountryPair> allAttacks = new LinkedList<>();
    do {
      CountryPair attacks = current.createAttack();
      if (attacks == null) {
        break;
      }
      Attack toProcess;
      do {
        toProcess = attacks.createAttack(current.getAttackTroops(attacks.getOutgoing()));
        allAttacks.add(attacks);
        moveProcessor.processAttack(toProcess);
      } while (!toProcess.getHasConquered() && toProcess.getAttackingCountry().getTroops() >= 2);
      if (simulationController.getGameState().isGameOver()) {
        return allAttacks;
      }
      if (toProcess.getHasConquered()) {
        Fortify forcedFortify = attacks.createFortify(toProcess.getTroopNumber());
        moveProcessor.processFortify(forcedFortify); // always possible
        Fortify afterAttack = current.moveAfterAttack(attacks);
        if (afterAttack != null && afterAttack.getTroopsToMove() > 0) {
          moveProcessor.processFortify(afterAttack);
        } else {
          allAttacks.remove(attacks); // we lost, don't try it!
        }
      }
    } while (current.attackAgain());
    moveProcessor.processEndPhase(new EndPhase(GamePhase.ATTACK_PHASE));
    return allAttacks;
  }

  /**
   * Simulates an entire fortify phase of a bot player.
   *
   * @param moveProcessor The Move Processor which will be used to process the attacks.
   * @param current       The player which is currently active.
   * @return The fortify move performed by the bot player.
   */
  private static Fortify simulateFortify(MoveProcessor moveProcessor, AiBot current) {
    Fortify fortify = current.createFortify();
    if (fortify != null) {
      moveProcessor.processFortify(fortify);
    }
    moveProcessor.processEndPhase(new EndPhase(GamePhase.FORTIFY_PHASE));
    return fortify;
  }

  /**
   * Calculates a strength score for the current player based off of a weighted sum of the
   * normalized values of their total strength and their amount of countries and continents.
   *
   * @param simulation The GameState in which the calculation is being done.
   * @return The result of the weighted sum.
   */
  public double calculateGameStateScore(GameState simulation) {
    Player currentPlayer = simulation.getCurrentPlayer();
    double strength = Probabilities.getPlayerStrength(simulation, currentPlayer);
    double countryPercentage = currentPlayer.getCountries().size() / 42.;
    double continentPercentage = currentPlayer.getContinents().size() / 6.;
    return STRENGTH_WEIGHT * strength + COUNTRY_WEIGHT * countryPercentage
        + CONTINENT_WEIGHT * continentPercentage;
  }
}
