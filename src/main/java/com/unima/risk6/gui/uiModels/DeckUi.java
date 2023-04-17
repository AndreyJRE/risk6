package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Deck;
import javafx.scene.layout.Pane;

public class DeckUi extends Pane {

  private Deck deck;


  public DeckUi(Deck deck) {
    super();
    this.deck = deck;
  }

}
