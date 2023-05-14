package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Hand;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A JsonSerializer and JsonDeserializer implementation for {@link com.unima.risk6.game.models.Hand}
 * objects. This class defines how Hand objects are converted to and from their JSON
 * representations.
 *
 * @author jferch
 */
public class HandTypeAdapter implements JsonSerializer<Hand>, JsonDeserializer<Hand> {

  /**
   * Serializes a Hand object to its corresponding JSON representation.
   *
   * @param hand The Hand object to be serialized.
   * @param typeOfSrc The actual generic type of the source object.
   * @param context The context for serialization.
   * @return A JsonElement corresponding to the specified Hand.
   */
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

  /**
   * Deserializes a JsonElement into a Hand object.
   *
   * @param json    The JsonElement being deserialized.
   * @param typeOfT The type of the Object to deserialize to.
   * @param context The context for deserialization.
   * @return A Hand object corresponding to the specified JsonElement.
   * @throws JsonParseException if json is not in the expected format of Hand.
   */
  @Override
  public Hand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
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
    hand.getCards().addAll(cards);
    hand.getSelectedCards().addAll(selectedCards);

    return hand;
  }
}