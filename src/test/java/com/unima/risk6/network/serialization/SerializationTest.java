package com.unima.risk6.network.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.network.message.StandardMessage;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Tests for object serialization
 *
 * @author jferch
 */

public class SerializationTest {
  @BeforeAll
  static void setUp() {
    try {

    } catch (Exception e) {
    }

  }


  @Test
  void testSerializationWithStatus() {
    assertEquals("{\"statusCode\":200,\"content\":\"tetest\"}", Serializer.serialize(new StandardMessage("tetest", 200)));
  }

  @Test
  void testSerializationWithoutStatus() {
    assertEquals("{\"statusCode\":-1,\"content\":\"tetest\"}", Serializer.serialize(new StandardMessage("tetest")));

  }

  @Test
  void testSerializationAndDeserialization() {
    StandardMessage standard =  new StandardMessage("tetest");
    String json = Serializer.serialize(standard);
    StandardMessage s2 = (StandardMessage) Deserializer.deserialize(json);
    System.out.println(s2.getStatusCode() + (String)s2.getContent());

    //assertEquals(standard, s2);
    assertTrue(standard.equals(s2));

  }
}
