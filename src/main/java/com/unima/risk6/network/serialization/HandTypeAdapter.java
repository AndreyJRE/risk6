package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Hand;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class HandTypeAdapter implements JsonSerializer<Hand> {

  @Override
  public JsonElement serialize(Hand hand, Type typeOfSrc, JsonSerializationContext context) {
    if (hand == null) {
      return null;
    }

    JsonObject jsonObject = new JsonObject();

    JsonArray cardsArray = new JsonArray();
    ArrayList<Card> cards = hand.getCards();
    if (cards != null) {
      for (Card card : cards) {
        cardsArray.add(context.serialize(card, Card.class));
      }
    }
    jsonObject.add("cards", cardsArray);

    JsonArray selectedCardsArray = new JsonArray();
    ArrayList<Card> selectedCards = hand.getSelectedCards();
    if (selectedCards != null) {
      for (Card card : selectedCards) {
        selectedCardsArray.add(context.serialize(card, Card.class));
      }
    }
    jsonObject.add("selectedCards", selectedCardsArray);

    return jsonObject;
  }
}