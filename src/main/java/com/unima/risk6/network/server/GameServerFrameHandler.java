package com.unima.risk6.network.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.network.message.StandardMessage;
import com.unima.risk6.network.serialization.Deserializer;
import com.unima.risk6.network.serialization.Serializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameServerFrameHandler.class);

  private static ChannelGroup channels;
  private MoveProcessor moveProcessor;

  GameServerFrameHandler(ChannelGroup channels, MoveProcessor moveProcessor) {
    GameServerFrameHandler.channels = channels;
    this.moveProcessor = moveProcessor;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

    if (frame instanceof TextWebSocketFrame) {
      String request = ((TextWebSocketFrame) frame).text();
      JsonObject json = null;
      try {
        LOGGER.debug("Server: Trying to read message");
        json = JsonParser.parseString(request).getAsJsonObject();
        //TODO Error Handling
      } catch (Exception e) {
        LOGGER.debug("Not a JSON: " + request + "\n Exception: " + e);
      }
      if (json != null) {
        LOGGER.debug(
            "Server Received Message with ContentType: " + json.get("contentType").getAsString());
        switch (json.get("contentType").getAsString()) {
          case "GAME_STATE" -> {
            LOGGER.error("The server should not receive a gamestate");
          }
          case "ATTACK" -> {
            LOGGER.debug("The server received a attack object");
            Attack attack = (Attack) Deserializer.deserialize(request,
                moveProcessor.getGameController().getGameState()).getContent();
            moveProcessor.processAttack(attack);
            sendGamestate();
          }
          case "REINFORCE" -> {
            LOGGER.debug("The server received a reinforce object");
            Reinforce reinforce = (Reinforce) Deserializer.deserialize(request,
                moveProcessor.getGameController().getGameState()).getContent();
            moveProcessor.processReinforce(reinforce);
            sendGamestate();
          }
          case "FORTIFY" -> {
            LOGGER.debug("The server received a fortify object");
            Fortify fortify = (Fortify) Deserializer.deserialize(request,
                moveProcessor.getGameController().getGameState()).getContent();
            moveProcessor.processFortify(fortify);
            sendGamestate();
          }
          case "HAND_IN" -> {
            LOGGER.debug("The server received a hand in object");
            HandIn handIn = (HandIn) Deserializer.deserialize(request).getContent();
            moveProcessor.processHandIn(handIn);
            sendGamestate();
          }
          case "END_PHASE" -> {
            LOGGER.debug("The server received a end phase object");
            EndPhase endPhase = (EndPhase) Deserializer.deserialize(request).getContent();
            moveProcessor.processEndPhase(endPhase);
            sendGamestate();
          }
          default -> {
            LOGGER.debug("The Message received wasnt a valid Message\nMessage: " + json);

          }
        }
      }
      //System.out.println(channels.size());
      //ctx.channel().writeAndFlush(new TextWebSocketFrame(request));
    } else {
      String message = "unsupported frame type: " + frame.getClass().getName();
      throw new UnsupportedOperationException(message);
    }
  }

  private void sendGamestate() {
    for (Channel ch : channels) {
      LOGGER.debug("Send new gamestate");
      ch.writeAndFlush(
          new TextWebSocketFrame(Serializer.serialize(
              new StandardMessage(moveProcessor.getGameController().getGameState()))));
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    channels.add(ctx.channel());
    //ctx.channel().attr("name").set("Test");
  }

}
