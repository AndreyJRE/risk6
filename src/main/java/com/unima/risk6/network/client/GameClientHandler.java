package com.unima.risk6.network.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.gui.configurations.CountriesUiConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.network.configurations.NetworkConfiguration;
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
import java.util.HashMap;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In this class incoming messages get handled and processed
 *
 * @author jferch
 */
public class GameClientHandler extends SimpleChannelInboundHandler<Object> {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameClientHandler.class);

  private final WebSocketClientHandshaker handshaker;
  private ChannelPromise handshakeFuture;


  /**
   * Constructs a new GameClientHandler.
   *
   * @param handshaker the handshaker used to handle the WebSocket handshake.
   */
  public GameClientHandler(WebSocketClientHandshaker handshaker) {
    this.handshaker = handshaker;
  }

  /**
   * Returns a ChannelFuture for the WebSocket handshake. This future completes when the handshake
   * completes.
   *
   * @return the handshake future.
   */
  public ChannelFuture handshakeFuture() {
    return handshakeFuture;
  }

  /**
   * Called when a handler is added to a pipeline. This method creates a new promise for the
   * WebSocket handshake.
   *
   * @param ctx the context of the channel handler.
   */
  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    handshakeFuture = ctx.newPromise();
  }

  /**
   * Called when a channel becomes active. This method initiates the WebSocket handshake.
   *
   * @param ctx the context of the channel handler.
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    handshaker.handshake(ctx.channel());
  }

  /**
   * Called when a channel becomes inactive. This method logs that the client has disconnected.
   *
   * @param ctx the context of the channel handler.
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    LOGGER.info("Client disconnected");
  }

  /**
   * Handles the reading of messages from the server. This method performs the handshake if it's not
   * complete. If the handshake is complete, it reads WebSocket frames and handles them
   * accordingly.
   *
   * @param ctx the context of the channel handler.
   * @param msg the received message.
   * @throws Exception if an exception occurs during the processing of the message.
   */
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
          "Unexpected FullHttpResponse (getStatus=" + response.status() + ", content="
              + response.content().toString(CharsetUtil.UTF_8) + ')');
    }

    WebSocketFrame frame = (WebSocketFrame) msg;
    System.out.println("ctx = " + ctx + ", msg = " + msg);
    if (frame instanceof TextWebSocketFrame textFrame) {
      JsonObject json = null;
      try {
        json = JsonParser.parseString(textFrame.text()).getAsJsonObject();
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
            if (g.isGameOver()) {
              Thread.sleep(3500);
              LobbyConfiguration.stopGameClient();
              SoundConfiguration.stopInGameMusic();
              Thread.sleep(150);
              if (NetworkConfiguration.getGameServer().getHostIp().equals("127.0.0.1")) {
                NetworkConfiguration.stopGameServer();
              }
              Thread.sleep(150);
              Platform.runLater(() -> SceneConfiguration.gameOverScene(g));
            }
          }
          case "CHAT_MESSAGE" -> {
            String message = (String) Deserializer.deserializeChatMessage(textFrame.text())
                .getContent();
            LOGGER.debug("Client got a chat message " + message);
            LobbyConfiguration.setLastChatMessage(message);
          }
          case "ORDER" -> {
            HashMap<String, Integer> hashMap2 = (HashMap<String, Integer>) Deserializer.deserialize(
                textFrame.text()).getContent();
            LOGGER.debug("Client got a order message ");
            GameConfiguration.setDiceRolls(hashMap2);

            //TODO
          }
          case "CONNECTION" -> {
            switch (json.get("connectionActions").getAsString()) {
              case "ACCEPT_JOIN_SERVER_LOBBY" -> {
                LOGGER.debug("Got a Lobby, overwrite serverlobby");
                ServerLobby content = (ServerLobby) Deserializer.deserializeConnectionMessage(
                    textFrame.text()).getContent();
                LobbyConfiguration.setServerLobby(content);
                if (SceneConfiguration.getSceneController().getCurrentSceneName()
                    == SceneName.JOIN_ONLINE) {
                  Platform.runLater(SceneConfiguration::joinServerLobbyScene);
                }

              }
              case "ACCEPT_CREATE_LOBBY" -> {
                LOGGER.debug("Got a Lobby, overwrite game lobby");
                LobbyConfiguration.setGameLobby(
                    (GameLobby) Deserializer.deserializeConnectionMessage(textFrame.text())
                        .getContent());

                if (SceneConfiguration.getSceneController().getCurrentSceneName()
                    == SceneName.TITLE) {
                  Platform.runLater(SceneConfiguration::joinSinglePlayerLobby);

                } else {
                  Platform.runLater(SceneConfiguration::joinMultiplayerLobbyScene);
                }
              }
              case "ACCEPT_START_GAME" -> {
                LOGGER.debug(
                    "At ACCEPT_START_GAME: Got first Gamestate: Overwrite GameState with new GameState from Server");
                GameState g = (GameState) Deserializer.deserializeConnectionMessage(
                        textFrame.text(),
                        GameConfiguration.configureGame(new ArrayList<>(), new ArrayList<>()))
                    .getContent();
                GameConfiguration.setGameState(g);
                Probabilities.init();
                CountriesUiConfiguration.configureCountries(g.getCountries());
                Platform.runLater(SceneConfiguration::startGame);
              }
              case "ACCEPT_JOIN_GAME_LOBBY" -> {
                LOGGER.debug("At ACCEPT_JOIN_GAME_LOBBY: Got a Lobby, overwrite game lobby");
                GameLobby gameLobby = (GameLobby) Deserializer.deserializeConnectionMessage(
                    textFrame.text()).getContent();

                LobbyConfiguration.setGameLobby(gameLobby);
                if (SceneConfiguration.getSceneController().getCurrentSceneName()
                    == SceneName.SELECT_LOBBY) {
                  System.out.println("----------------- ");
                  Platform.runLater(SceneConfiguration::joinMultiplayerLobbyScene);
                }
              }
              case "ACCEPT_TUTORIAL_CREATE_LOBBY" -> {
                LOGGER.debug("Got a Lobby, overwrite serverlobby");
                LobbyConfiguration.setGameLobby(
                    (GameLobby) Deserializer.deserializeConnectionMessage(textFrame.text())
                        .getContent());
                Platform.runLater(SceneConfiguration::joinTutorialLobbyScene);
              }
              case "ACCEPT_UPDATE_SERVER_LOBBY" -> {
                LOGGER.debug("Got updated server Lobby, overwrite serverlobby");
                ServerLobby content = (ServerLobby) Deserializer.deserializeConnectionMessage(
                    textFrame.text()).getContent();
                LobbyConfiguration.setServerLobby(content);
              }
              case "DROP_USER_GAME_LOBBY", "DROP_USER_SERVER_LOBBY", "DROP_CREATE_GAME_LOBBY" -> {
                String content = (String) Deserializer.deserializeConnectionMessage(
                    textFrame.text()).getContent();
                LOGGER.error("Error Connecting to game lobby: " + content);
                if (SceneConfiguration.getSceneController().getCurrentSceneName()
                    == SceneName.JOIN_ONLINE) {
                  Platform.runLater(() -> StyleConfiguration.handleUsernameExists(
                      "Error Connecting to game lobby: " + content, "Username", "New username"));

                } else if (SceneConfiguration.getSceneController().getCurrentSceneName()
                    == SceneName.CREATE_LOBBY) {
                  Platform.runLater(() -> StyleConfiguration.handleLobbyExists(
                      content));

                }


              }
              default -> LOGGER.debug("Client received a faulty connection message");


            }
          }

          default -> LOGGER.debug(
              "The Message received wasnt a gamestate, a chat message or a connection message");
        }
      }

    } else if (frame instanceof PongWebSocketFrame) {
      System.out.println("Received pong from Server");
    } else if (frame instanceof CloseWebSocketFrame) {
      System.out.println("Received closing from Server");
      ch.close();
    }
  }


  /**
   * Handles exceptions that occur in the channel pipeline. This method prints the stack trace of
   * the cause, and if the handshake is not done, it sets the handshake future to failure.
   *
   * @param ctx   the context of the channel handler.
   * @param cause the cause of the exception.
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    if (!handshakeFuture.isDone()) {
      handshakeFuture.setFailure(cause);
    }
    ctx.close();
  }
}