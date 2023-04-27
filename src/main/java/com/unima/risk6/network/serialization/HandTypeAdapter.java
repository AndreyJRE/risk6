package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Hand;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class HandTypeAdapter implements JsonSerializer<Hand>, JsonDeserializer<Hand> {

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
  @Override
  public Hand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    JsonArray cardsArray = jsonObject.getAsJsonArray("cards");
    ArrayList<Card> cards = new ArrayList<>();
    for (JsonElement cardElement : cardsArray) {
      cards.add(context.deserialize(cardElement, Card.class));
    }

    JsonArray selectedCardsArray = jsonObject.getAsJsonArray("selectedCards");
    ArrayList<Card> selectedCards = new ArrayList<>();
    for (JsonElement selectedCardElement : selectedCardsArray) {
      selectedCards.add(context.deserialize(selectedCardElement, Card.class));
    }

    Hand hand = new Hand();
    //TODO
    //hand.setCards(cards);
    //hand.setSelectedCards(selectedCards);

    return hand;
  }
}