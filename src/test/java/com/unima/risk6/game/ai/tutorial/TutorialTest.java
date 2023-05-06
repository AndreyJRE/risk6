package com.unima.risk6.game.ai.tutorial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TutorialTest {

  static Tutorial tutorial;
  static GameState tutorialState;
  static AiBot tutorialBot;
  static Player humanPlayer;

  @BeforeAll
  static void beforeAll() {
    InputStream data = TutorialTest.class.getResourceAsStream(
        "/com/unima/risk6/json/messages.json");
    assert data != null;
    InputStreamReader dataReader = new InputStreamReader(data);
    tutorial = new Tutorial("Gameplay Gabe", dataReader);
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
    System.out.println(tutorial.getNextMessage());
  }
}