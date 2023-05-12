package com.unima.risk6.game.ai.tutorial;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.json.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Tutorial class is responsible for setting up and managing the game's deterministic tutorial
 * mode. It guides the player through a step-by-step learning experience with the help of messages.
 *
 * @author eameri
 */
public class Tutorial {

  private static final Random RNG = new Random();
  private final Queue<String> messages;
  private final GameState tutorialState;
  private Reinforce currentClaim;
  private Reinforce currentReinforce;
  private Attack currentAttack;
  private Fortify currentFortify;
  private final Queue<Reinforce> humanClaims;
  private final Queue<Reinforce> humanReinforcements;
  private final Queue<Attack> humanAttacks;
  private final Queue<Fortify> humanFortifies;
  private final Set<CountryName> humanCountries;
  private final Set<CountryName> botCountries;
  private final Map<CountryName, Country> countryMap;
  private final List<String> human;
  private final List<AiBot> bot;
  private static final String MESSAGES_FILE = "/com/unima/risk6/json/messages.json";

  /**
   * Initializes the tutorial with the specified username and loads messages with the fileReader.
   *
   * @param username The username of the human player.
   */
  public Tutorial(String username) {
    this.human = Collections.singletonList(username);
    this.bot = Collections.singletonList(new TutorialBot());
    this.humanCountries = this.initializeHumanCountries();
    this.botCountries = this.initializeBotCountries();
    this.tutorialState = this.createTutorial();
    this.countryMap = this.initializeMap();
    this.humanClaims = this.createClaims();
    this.humanReinforcements = this.createReinforcements();
    this.humanAttacks = this.createAttacks();
    this.humanFortifies = this.createFortifies();
    this.messages = this.createMessages();
  }

  private GameState createTutorial() {
    GameState tutorial = GameConfiguration.configureGame(this.human, this.bot);
    // extract methods separated by empty newlines
    PlayerController botController = new PlayerController();
    PlayerController humanController = new PlayerController();
    for (Player p : tutorial.getActivePlayers()) {
      if (p.equals(this.bot.get(0))) {
        botController.setPlayer(p);
      } else {
        humanController.setPlayer(p);
      }
    }

    for (Country country : tutorial.getCountries()) {
      if (!(country.getCountryName().equals(CountryName.INDONESIA) || country.getCountryName()
          .equals(CountryName.NEW_GUINEA))) {
        if (this.humanCountries.contains(country.getCountryName())) {
          humanController.addCountry(country);
        } else if (this.botCountries.contains(country.getCountryName())) {
          botController.addCountry(country);
        } else if (RNG.nextDouble() < 0.5) {
          humanController.addCountry(country);
        } else {
          botController.addCountry(country);
        }
        country.setTroops(1);
      }
    }

    if (tutorial.getCurrentPlayer().equals(this.bot.get(0))) {
      Player botTemp = tutorial.getActivePlayers().poll();
      tutorialState.getActivePlayers().add(botTemp);
      tutorial.setCurrentPlayer(tutorial.getActivePlayers().peek());
    }
    ((Player) bot.get(0)).setCurrentPhase(GamePhase.NOT_ACTIVE);
    tutorial.getCurrentPlayer().setCurrentPhase(GamePhase.CLAIM_PHASE);
    DeckController deckController = new DeckController(tutorial.getDeck());
    deckController.initDeck();
    List<Card> humanCards = humanController.getHandController().getHand().getCards();
    humanCards.add(new Card(CardSymbol.CAVALRY, CountryName.ALASKA, -1));
    humanCards.add(new Card(CardSymbol.CAVALRY, CountryName.KAMCHATKA, -2));
    humanCards.add(new Card(CardSymbol.CAVALRY, CountryName.CONGO, -3));
    tutorial.getActivePlayers().forEach(p -> p.setInitialTroops(9));
    return tutorial;
  }

  private Set<CountryName> initializeHumanCountries() {
    Set<CountryName> humanCountries = new HashSet<>(); // manually take New Guinea
    humanCountries.add(CountryName.WESTERN_AUSTRALIA);
    humanCountries.add(CountryName.EASTERN_AUSTRALIA);
    humanCountries.add(CountryName.CENTRAL_AMERICA);
    humanCountries.add(CountryName.WESTERN_UNITED_STATES);
    humanCountries.add(CountryName.ALASKA);
    humanCountries.add(CountryName.KAMCHATKA);
    humanCountries.add(CountryName.CONGO);
    humanCountries.add(CountryName.VENEZUELA);
    return humanCountries;
  }

  private Set<CountryName> initializeBotCountries() {
    Set<CountryName> botCountries = new HashSet<>(); // manually take indonesia
    botCountries.add(CountryName.PERU);
    botCountries.add(CountryName.BRAZIL);
    botCountries.add(CountryName.ARGENTINA);
    botCountries.add(CountryName.EASTERN_UNITED_STATES);
    return botCountries;
  }

  /**
   * Creates a queue of messages for the tutorial from the given fileReader.
   *
   * @return A queue of tutorial messages.
   */
  public Queue<String> createMessages() {
    InputStream data =
        Tutorial.class.getResourceAsStream(MESSAGES_FILE);
    assert data != null;
    InputStreamReader fileReader = new InputStreamReader(data);
    LinkedList<ArrayList<String>> msgArray = JsonParser.parseJsonFile(fileReader, LinkedList.class);
    return msgArray.stream().map(arr -> String.join(" ", arr))
        .collect(Collectors.toCollection(LinkedList::new));
  }


  /**
   * Creates a queue of reinforcement moves for the human player to perform during the tutorial.
   *
   * @return A queue of tutorial reinforcement moves.
   */
  private Queue<Reinforce> createReinforcements() {
    Queue<Reinforce> reinforcements = new LinkedList<>();
    reinforcements.add(new Reinforce(this.countryMap.get(CountryName.NEW_GUINEA), 3));
    return reinforcements;
  }

  /**
   * Creates a queue of claim phase moves for the human player to perform during the tutorial.
   *
   * @return A queue of tutorial claim phase moves.
   */
  private Queue<Reinforce> createClaims() {
    Queue<Reinforce> claims = new LinkedList<>();
    claims.add(new Reinforce(this.countryMap.get(CountryName.NEW_GUINEA), 1));
    for (int i = 0; i < 3; i++) {
      claims.add(new Reinforce(this.countryMap.get(CountryName.NEW_GUINEA), 1));
    }
    for (int i = 0; i < 5; i++) {
      claims.add(new Reinforce(this.countryMap.get(CountryName.VENEZUELA), 1));
    }
    return claims;
  }

  /**
   * Creates a queue of attack moves for the human player to perform during the tutorial.
   *
   * @return A queue of tutorial attack moves.
   */
  private Queue<Attack> createAttacks() {
    Queue<Attack> attacks = new LinkedList<>();
    attacks.add(new Attack(this.countryMap.get(CountryName.NEW_GUINEA),
        this.countryMap.get(CountryName.INDONESIA), 3));
    return attacks;
  }

  /**
   * Creates a queue of fortify moves for the human player to perform during the tutorial.
   *
   * @return A queue of tutorial fortify moves.
   */
  private Queue<Fortify> createFortifies() {
    Queue<Fortify> fortifies = new LinkedList<>();
    fortifies.add(new Fortify(this.countryMap.get(CountryName.NEW_GUINEA),
        this.countryMap.get(CountryName.INDONESIA), 2));
    fortifies.add(new Fortify(this.countryMap.get(CountryName.VENEZUELA),
        this.countryMap.get(CountryName.CENTRAL_AMERICA), 20));
    return fortifies;
  }

  /**
   * Initializes the map of countries.
   *
   * @return A map of CountryName to Country objects.
   */
  private Map<CountryName, Country> initializeMap() {
    return tutorialState.getCountries().stream()
        .collect(Collectors.toMap(Country::getCountryName, Function.identity()));
  }

  public void updatePlayerClaim() {
    this.currentClaim = this.humanClaims.poll();
  }

  public void updatePlayerReinforce() {
    this.currentReinforce = this.humanReinforcements.poll();
  }

  public void updatePlayerAttack() {
    this.currentAttack = this.humanAttacks.poll();
  }

  public void updatePlayerFortify() {
    this.currentFortify = this.humanFortifies.poll();
  }

  public GameState getTutorialState() {
    return this.tutorialState;
  }

  public AiBot getTutorialBot() {
    return this.bot.get(0);
  }

  public String getNextMessage() {
    return this.messages.poll();
  }

  public Reinforce getCurrentClaim() {
    return currentClaim;
  }

  public Reinforce getCurrentReinforce() {
    return currentReinforce;
  }

  public Attack getCurrentAttack() {
    return currentAttack;
  }

  public Fortify getCurrentFortify() {
    return currentFortify;
  }
}
