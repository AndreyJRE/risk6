package com.unima.risk6.network.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.network.serialization.Deserializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.CharsetUtil;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameClientHandler extends SimpleChannelInboundHandler<Object> {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameClientHandler.class);

  private final WebSocketClientHandshaker handshaker;
  private ChannelPromise handshakeFuture;

  public GameClientHandler(WebSocketClientHandshaker handshaker) {
    this.handshaker = handshaker;
  }

  public ChannelFuture handshakeFuture() {
    return handshakeFuture;
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    handshakeFuture = ctx.newPromise();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    handshaker.handshake(ctx.channel());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    LOGGER.info("Client disconnected");
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    Channel ch = ctx.channel();
    if (!handshaker.isHandshakeComplete()) {
      try {
        handshaker.finishHandshake(ch, (FullHttpResponse) msg);
        LOGGER.info("Connected successfully to Server!");
        handshakeFuture.setSuccess();
      } catch (WebSocketHandshakeException e) {
        LOGGER.error("Failed to connect");
        handshakeFuture.setFailure(e);
      }
      return;
    }

    if (msg instanceof FullHttpResponse response) {
      throw new IllegalStateException(
          "Unexpected FullHttpResponse (getStatus=" + response.status() +
              ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
    }

    WebSocketFrame frame = (WebSocketFrame) msg;
    if (frame instanceof TextWebSocketFrame textFrame) {
      JsonObject json = null;
      try {
        json = JsonParser.parseString(textFrame.text()).getAsJsonObject();
        //TODO Error Handling
      } catch (Exception e) {
        LOGGER.debug("Not a JSON: " + textFrame.text() + "\n Exception: " + e);

      }
      if (json != null) {
        LOGGER.debug(
            "Client Received Message with ContentType: " + json.get("contentType").getAsString());
        switch (json.get("contentType").getAsString()) {
          case "GAME_STATE" -> {
            LOGGER.debug("Overwrite GameState with new GameState from Server");
            GameState g = (GameState) Deserializer.deserialize(textFrame.text(),
                GameConfiguration.configureGame(new ArrayList<>(), new ArrayList<>())).getContent();
            GameConfiguration.setGameState(g);
          }
          case "CONNECTION" -> {
            switch (json.get("connectionActions").getAsString()) {
              case "ACCEPT_USER_LOBBY" -> {
                LobbyConfiguration.setServerLobby(
                    (ServerLobby) Deserializer.deserialize(textFrame.text()).getContent());
                LobbyConfiguration.getServerLobby().getUsers()
                    .forEach(user -> System.out.println(user.getUsername()));
              }
              case "ACCEPT_USER_GAME" -> {
                LOGGER.debug(
                    "Got first Gamestate: Overwrite GameState with new GameState from Server");
                GameState g = (GameState) Deserializer.deserialize(textFrame.text(),
                        GameConfiguration.configureGame(new ArrayList<>(), new ArrayList<>()))
                    .getContent();
                GameConfiguration.setGameState(g);
                //TODO
              }
              case "DROP_USER_LOBBY" -> {
                //TODO
              }
              case "DROP_USER_GAME" -> {
                //TODO
              }

            }
          }

          default -> LOGGER.debug("The Message received wasnt a gamestate or a connection message");
        }
      }

    } else if (frame instanceof PongWebSocketFrame) {
      System.out.println("Received pong from Server");
    } else if (frame instanceof CloseWebSocketFrame) {
      System.out.println("Received closing from Server");
      ch.close();
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    if (!handshakeFuture.isDone()) {
      handshakeFuture.setFailure(cause);
    }
    ctx.close();
  }
}