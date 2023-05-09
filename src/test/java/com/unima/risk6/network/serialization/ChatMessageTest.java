package com.unima.risk6.network.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.unima.risk6.network.message.ChatMessage;
import org.junit.jupiter.api.Test;

public class ChatMessageTest {

  @Test
  void testSerialization() {
    ChatMessage chatMessage = new ChatMessage("Hallo");
    assertEquals("{\"statusCode\":-1,\"content\":\"Hallo\",\"contentType\":\"CHAT_MESSAGE\"}",
        Serializer.serialize(chatMessage));
  }

  @Test
  void testSerializationAndDeserialization() {
    ChatMessage chatMessage = new ChatMessage("Hallo");
    String json = Serializer.serialize(chatMessage);
    System.out.println(json);
    ChatMessage chatMessage1 = Deserializer.deserializeChatMessage(json);
    System.out.println(chatMessage1.getContent());
    assertEquals(chatMessage.getContent(), chatMessage1.getContent());
  }
}
