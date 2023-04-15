package com.unima.risk6.network.serialization;

import com.google.gson.Gson;
import com.unima.risk6.network.message.Message;
import com.unima.risk6.network.message.StandardMessage;


public class Deserializer {
  public static Message deserialize(String json){
    return new Gson().fromJson(json, StandardMessage.class);
  }
}
