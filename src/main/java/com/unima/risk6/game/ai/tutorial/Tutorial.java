package com.unima.risk6.game.ai.tutorial;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.json.JsonParser;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Tutorial class is responsible for setting up and managing the game's deterministic tutorial
 * mode. It guides the player through a step-by-step learning experience with the help of messages.
 *
 * @author eameri
 */
public class Tutorial {

  private final Random rng;
  private final Queue<String> messages;
  private final Queue<GameState> gameStateOrder;
  private final Queue<Reinforce> humanReinforcements;
  private final Queue<Attack> humanAttacks;
  private final Queue<Fortify> humanFortifies;
  private final HandIn cardSwap;
  private final List<String> human;
  private final List<AiBot> bot;
  private final Map<CountryName, Country> countryMap;

  /**
   * Initializes the tutorial with the specified username and loads messages with the fileReader.
   *
   * @param username   The username of the human player.
   * @param fileReader The InputStreamReader for the JSON file containing tutorial messages.
   */
  public Tutorial(String username, InputStreamReader fileReader) {
    this.rng = new Random();
    this.human = Collections.singletonList(username);
    this.bot = Collections.singletonList(new TutorialBot("Johnny Test"));
    this.gameStateOrder = this.createGameStates();
    this.cardSwap = this.createCardSwap();
    this.countryMap = this.initializeMap();
    this.gameStateOrder.poll(); // remove init state
    this.humanReinforcements = this.createReinforcements();
    this.humanAttacks = this.createAttacks();
    this.humanFortifies = this.createFortifies();
    this.messages = this.createMessages(fileReader);
  }

  /**
   * Creates a valid HandIn containing three cavalry cards for use in the tutorial.
   *
   * @return The HandIn containing three cavalry cards.
   */
  private HandIn createCardSwap() {
    // TODO: redundant method?
    ArrayList<Card> cards = new ArrayList<>();
    //cards.add(new Card(CardSymbol.CAVALRY, CountryName.ALASKA));
    //cards.add(new Card(CardSymbol.CAVALRY, CountryName.KAMCHATKA));
    //cards.add(new Card(CardSymbol.CAVALRY, CountryName.CONGO));
    return new HandIn(cards);
  }

  /**
   * Initializes the map of countries.
   *
   * @return A map of CountryName to Country objects.
   */
  private Map<CountryName, Country> initializeMap() {
    return gameStateOrder.peek().getCountries().stream()
        .collect(Collectors.toMap(Country::getCountryName, Function.identity()));
  }

  /**
   * Creates a queue of messages for the tutorial from the given fileReader.
   *
   * @param fileReader The InputStreamReader for the JSON file containing tutorial messages.
   * @return A queue of tutorial messages.
   */
  public Queue<String> createMessages(InputStreamReader fileReader) {
    LinkedList<ArrayList<String>> msgArray = JsonParser.parseJsonFile(fileReader, LinkedList.class);
    return msgArray.stream().map(arr -> String.join(" ", arr))
        .collect(Collectors.toCollection(LinkedList::new));
  }

  /**
   * Creates a queue of various game states to be used in the tutorial.
   *
   * @return A queue of tutorial game states.
   */
  public Queue<GameState> createGameStates() {
    Queue<GameState> gameStates = new LinkedList<>();
    gameStates.add(createGameStateZero());
    gameStates.add(createGameStateOne());
    gameStates.add(createGameStateTwo());
    return gameStates;
  }

  /**
   * Creates an empty game state meant to only be used during the initialization of the Tutorial
   * class.
   *
   * @return The temporary game state for initialization.
   */
  public GameState createGameStateZero() {
    return GameConfiguration.configureGame(this.human, this.bot);
  }

  /**
   * The first game state which is at the end of the claim phase. It is prepared such that the
   * initial claim phase as well as one entire turn for both the bot and player can be experienced.
   *
   * @return the first game state of the tutorial mode.
   */
  public GameState createGameStateOne() {
    GameState gameState1 = GameConfiguration.configureGame(this.human, this.bot);
    Player humanPlayer = null;
    Player testBot = null;
    for (int i = 0; i < gameState1.getActivePlayers().size(); i++) {
      Player temp = gameState1.getActivePlayers().poll();
      if (temp.getUser().equals("Johnny Test")) {
        testBot = temp;
      } else {
        humanPlayer = temp;
      }
    }

    for (Country country : gameState1.getCountries()) {
      CountryName countryName = country.getCountryName();
      if (!countryName.equals(CountryName.EASTERN_AUSTRALIA) && !countryName.equals(
          CountryName.BRAZIL)) {
        if (countryName.equals(CountryName.NEW_GUINEA) || countryName.equals(
            CountryName.VENEZUELA)) {
          country.setPlayer(testBot);
        } else if (countryName.equals(CountryName.INDONESIA) || countryName.equals(
            CountryName.PERU)) {
          country.setPlayer(humanPlayer);
        } else {
          country.setPlayer(rng.nextDouble() > 0.5 ? testBot : humanPlayer);
        }
        country.setTroops(1);
      }
    }
    return gameState1;
  }

  /**
   * Creates the second game state with randomized countries and troop counts, only making sure that
   * the human player has a specific hand of cards.
   *
   * @return The second game state of the tutorial mode.
   */
  private GameState createGameStateTwo() {
    GameState gameState2 = GameConfiguration.configureGame(this.human, this.bot);
    this.randomizeGameState(gameState2);
    Player human = gameState2.getActivePlayers().stream()
        .filter(player -> !(player instanceof TutorialBot)).findFirst().orElse(null);
    gameState2.setCurrentPlayer(human);
    if (!gameState2.getActivePlayers().peek().equals(human)) {
      Player tmp = gameState2.getActivePlayers().poll();
      gameState2.getActivePlayers().add(tmp);
    }
    List<Card> cards = human.getHand().getCards();
    cards.add(new Card(CardSymbol.CAVALRY, CountryName.ALASKA, -1));
    cards.add(new Card(CardSymbol.CAVALRY, CountryName.KAMCHATKA, -2));
    cards.add(new Card(CardSymbol.CAVALRY, CountryName.CONGO, -3));
    return gameState2;
  }

  public void updateGameStateCountries(GameState gameState, Country updateCountry, Player newPlayer,
      int troopCount) {
    for (Country country : gameState.getCountries()) {
      if (country.equals(updateCountry)) {
        updateCountry.setPlayer(newPlayer);
        updateCountry.setTroops(troopCount);
        break;
      }
    }
  }

  /**
   * Randomizes the given game state's countries and troop counts.
   *
   * @param gameState The game state to randomize.
   */
  private void randomizeGameState(GameState gameState) {
    Player tutBot = null;
    Player person = null;
    for (Player players : gameState.getActivePlayers()) {
      if (players instanceof TutorialBot) {
        tutBot = players;
      } else {
        person = players;
      }
    }
    for (Country country : gameState.getCountries()) {
      Player toSet = rng.nextDouble() > 0.5 ? tutBot : person;
      country.setPlayer(toSet);
      country.setTroops(rng.nextInt(1, 10));
    }
  }

  /**
   * Creates a queue of reinforcement moves for the human player to perform during the tutorial.
   *
   * @return A queue of tutorial reinforcement moves.
   */
  public Queue<Reinforce> createReinforcements() {
    Queue<Reinforce> reinforcements = new LinkedList<>();
    reinforcements.add(new Reinforce(this.countryMap.get(CountryName.EASTERN_AUSTRALIA), 1));
    reinforcements.add(new Reinforce(this.countryMap.get(CountryName.EASTERN_AUSTRALIA), 3));
    return reinforcements;
  }

  /**
   * Creates a queue of attack moves for the human player to perform during the tutorial.
   *
   * @return A queue of tutorial attack moves.
   */
  public Queue<Attack> createAttacks() {
    Queue<Attack> attacks = new LinkedList<>();
    Country easternAus = this.countryMap.get(CountryName.EASTERN_AUSTRALIA);
    Country newGuinea = this.countryMap.get(CountryName.NEW_GUINEA);
    // TODO: make sure you win
    attacks.add(new Attack(easternAus, newGuinea, 3));
    return attacks;
  }

  /**
   * Creates a queue of fortify moves for the human player to perform during the tutorial.
   *
   * @return A queue of tutorial fortify moves.
   */
  public Queue<Fortify> createFortifies() {
    Queue<Fortify> fortifies = new LinkedList<>();
    fortifies.add(new Fortify(this.countryMap.get(CountryName.EASTERN_AUSTRALIA),
        this.countryMap.get(CountryName.NEW_GUINEA), 1));
    fortifies.add(new Fortify(this.countryMap.get(CountryName.NEW_GUINEA),
        this.countryMap.get(CountryName.INDONESIA), 2));
    return fortifies;
  }
}
