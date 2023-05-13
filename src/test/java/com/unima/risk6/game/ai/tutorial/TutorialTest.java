package com.unima.risk6.game.ai.tutorial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.message.StandardMessage;
import com.unima.risk6.network.serialization.Deserializer;
import com.unima.risk6.network.serialization.Serializer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TutorialTest {

  static Tutorial tutorial;
  static GameState tutorialState;
  static AiBot tutorialBot;
  static Player humanPlayer;

  @BeforeAll
  static void beforeAll() {
    tutorial = new Tutorial("Gameplay Gabe");
    tutorialState = tutorial.getTutorialState();
    tutorialBot = tutorial.getTutorialBot();
    humanPlayer = tutorialState.getActivePlayers().stream()
        .filter(p -> p.getUser().equals("Gameplay Gabe")).findFirst().orElse(null);
  }

  @Test
  void initTest() {
    Player tutorialBotPlayer = (Player) tutorialBot;
    assertEquals(tutorialBotPlayer.getUser(), "Johnny Test");
    assertNotNull(humanPlayer);
    assertEquals(3, humanPlayer.getHand().getCards().size());
    assertTrue(humanPlayer.getCountries().size() >= 8);
    assertTrue(humanPlayer.getCountries().size() < 42);
    assertTrue(tutorialBotPlayer.getCountries().size() >= 4);
    assertTrue(tutorialBotPlayer.getCountries().size() < 42);
    assertEquals(2, tutorialState.getActivePlayers().size());
    assertEquals(humanPlayer, tutorialState.getCurrentPlayer());
    assertEquals(9, humanPlayer.getInitialTroops());
    assertEquals(9, tutorialBotPlayer.getInitialTroops());
    assertEquals(GamePhase.NOT_ACTIVE, tutorialBotPlayer.getCurrentPhase());
    assertEquals(GamePhase.CLAIM_PHASE, humanPlayer.getCurrentPhase());
    assertTrue(tutorial.getTutorialState().isChatEnabled());
    assertNotNull(tutorial.getNextMessage());
  }

  @Test
  void serializeTest() {
    GameState empty = GameConfiguration.configureGame(new ArrayList<>(), List.of(tutorialBot));
    String result = Serializer.serialize(new StandardMessage<GameState>(empty));
    GameState copy = (GameState) Deserializer.deserialize(result, empty).getContent();
    TutorialBot botCopy = (TutorialBot) copy.getCurrentPlayer();
    assertTrue(botCopy.getDeterministicClaims().size() > 0);
    assertTrue(botCopy.getDeterministicReinforces().size() > 0);
    assertTrue(botCopy.getDeterministicAttacks().size() > 0);
    assertTrue(botCopy.getDeterministicAfterAttacks().size() > 0);
    assertTrue(botCopy.getDeterministicFortifies().size() > 0);
  }
}