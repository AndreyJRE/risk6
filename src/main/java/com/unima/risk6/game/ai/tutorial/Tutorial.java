package com.unima.risk6.game.ai.tutorial;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
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

public class Tutorial {

  private final Random RNG;
  private final Queue<String> messages;
  private final Queue<GameState> gameStateOrder;
  private final Queue<Reinforce> humanReinforcements;
  private final Queue<Attack> humanAttacks;
  private final Queue<Fortify> humanFortifies;
  private final List<String> human;
  private final List<AiBot> bot;
  private final Map<CountryName, Country> countryMap;

  public Tutorial(String username, InputStreamReader fileReader) {
    this.RNG = new Random();
    this.human = Collections.singletonList(username);
    this.bot = Collections.singletonList(new TutorialBot("Johnny Test"));
    this.gameStateOrder = this.createGameState();
    this.countryMap = this.initializeMap();
    this.gameStateOrder.poll(); // remove init state
    this.humanReinforcements = this.createReinforcements();
    this.humanAttacks = this.createAttacks();
    this.humanFortifies = this.createFortifies();
    this.messages = this.createMessages(fileReader);
  }

  private Map<CountryName, Country> initializeMap() {
    return gameStateOrder.peek().getCountries().stream().collect(Collectors.toMap(
        Country::getCountryName, Function.identity()));
  }

  public Queue<String> createMessages(InputStreamReader fileReader) {
    LinkedList<ArrayList<String>> msgArray = JsonParser.parseJsonFile(fileReader, LinkedList.class);
    return msgArray.stream().map(arr -> String.join(" ", arr))
        .collect(Collectors.toCollection(LinkedList::new));
  }

  public Queue<GameState> createGameState() {
    Queue<GameState> gameStates = new LinkedList<>();
    gameStates.add(createGameStateZero());
    gameStates.add(createGameStateOne());
    return gameStates;
  }

  public GameState createGameStateZero() {
    return GameConfiguration.configureGame(this.human, this.bot);
  }

  /**
   * The first game state which is at the end of the claim phase. Two countries are left empty to be
   * reinforced during the tutorial, three countries each belong to a specific player for use later,
   * the rest are occupied randomly.
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
        if (countryName.equals(CountryName.NEW_GUINEA) || countryName.equals(CountryName.VENEZUELA)) {
          country.setPlayer(testBot);
        } else if (countryName.equals(CountryName.INDONESIA) || countryName.equals(
            CountryName.PERU)) {
          country.setPlayer(humanPlayer);
        } else {
          country.setPlayer(RNG.nextDouble() > 0.5 ? testBot : humanPlayer);
        }
        country.setTroops(1);
      }
    }
    return gameState1;
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

  private void randomizeGameState(GameState gameState, Player person, Player tutBot) {
    for (Country country : gameState.getCountries()) {
      Player toSet = RNG.nextDouble() > 0.5 ? tutBot : person;
      country.setPlayer(toSet);
      country.setTroops(RNG.nextInt(1, 10));
    }
  }

  public Queue<Reinforce> createReinforcements() {
    Queue<Reinforce> reinforcements = new LinkedList<>();
    GameState initState = this.gameStateOrder.peek();
    reinforcements.add(new Reinforce(this.countryMap.get(CountryName.EASTERN_AUSTRALIA), 1));
    reinforcements.add(new Reinforce(this.countryMap.get(CountryName.EASTERN_AUSTRALIA), 3));
    return reinforcements;
  }

  public Queue<Attack> createAttacks() {
    Queue<Attack> attacks = new LinkedList<>();
    GameState initState = this.gameStateOrder.peek();
    Country easternAus = this.countryMap.get(CountryName.EASTERN_AUSTRALIA);
    Country newGuinea = this.countryMap.get(CountryName.NEW_GUINEA);
    // TODO: make sure you win
    // TODO: moving troops after an attack -> use fortify?
    attacks.add(new Attack(easternAus, newGuinea, 3));
    return attacks;
  }

  public Queue<Fortify> createFortifies() {
    Queue<Fortify> fortifies = new LinkedList<>();
    fortifies.add(new Fortify(this.countryMap.get(CountryName.NEW_GUINEA),
        this.countryMap.get(CountryName.INDONESIA), 2));
    return fortifies;
  }

//  public static void main(String[] args) {
//    File file = new File("src/main/resources/com/unima/risk6/json/messages.json");
//    try {
//      Tutorial tut = new Tutorial("hel", new InputStreamReader(new FileInputStream(file)));
//      System.out.println(tut.messages.poll());
//      System.out.println(tut.messages.poll());
//      System.out.println("done");
//    } catch (Exception ignored) {
//      System.out.println(ignored);
//    }
//  }
}
