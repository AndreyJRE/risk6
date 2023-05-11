package com.unima.risk6.game.ai.tutorial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
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
    assertEquals(8, humanPlayer.getInitialTroops());
    assertEquals(8, tutorialBotPlayer.getInitialTroops());
    assertEquals(GamePhase.NOT_ACTIVE, tutorialBotPlayer.getCurrentPhase());
    assertEquals(GamePhase.CLAIM_PHASE, humanPlayer.getCurrentPhase());
    System.out.println(tutorial.getNextMessage());
  }
}