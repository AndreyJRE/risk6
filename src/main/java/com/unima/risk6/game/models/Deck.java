package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import java.util.ArrayList;
import java.util.Collections;

public class Deck {

  private ArrayList<Card> deckCards;



  public Deck(){
    deckCards= new ArrayList<Card>();
    CardSymbol.CANNON.getCountries().forEach((country)->deckCards.add(new Card(CardSymbol.CANNON,country )));
    CardSymbol.INFANTRY.getCountries().forEach((country)->deckCards.add(new Card(CardSymbol.INFANTRY,country )));
    CardSymbol.CAVALRY.getCountries().forEach((country)->deckCards.add(new Card(CardSymbol.CAVALRY,country )));
    this.shuffle();

  }

  public void shuffle(){
    Collections.shuffle(deckCards);

  }


}
