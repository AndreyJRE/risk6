package com.unima.risk6.network.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.Lobby;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import com.unima.risk6.network.message.ConnectionActions;
import com.unima.risk6.network.message.ConnectionMessage;
import com.unima.risk6.network.message.StandardMessage;
import com.unima.risk6.network.serialization.Deserializer;
import com.unima.risk6.network.serialization.Serializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameServerFrameHandler.class);

  private static ChannelGroup channels;
  private static BiMap<Lobby, ChannelGroup> gameChannels = HashBiMap.create();

  private static BiMap<UserDto, Channel> users = HashBiMap.create();
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
            Queue<Move> moves = moveProcessor.getGameController().getGameState().getLastMoves();
            sendGamestate();
          }
          case "CONNECTION" -> {
            LOGGER.debug("The server received a connection message");
            //TODO Connection problem
            ConnectionMessage connectionMessage = null;
            try {
              connectionMessage = (ConnectionMessage) Deserializer.deserialize(
                  request);
            } catch (Exception e) {
              LOGGER.error("Error at deserializing ConnectionMessage: " + e);
            }
            try {
              switch (connectionMessage.getConnectionActions()) {

                case JOIN_SERVER_LOBBY -> {
                  //TODO Make it work properly
                  LOGGER.debug("At JOIN_SERVER_LOBBY" + connectionMessage.getContent().getClass());
                  UserDto userDto = (UserDto) connectionMessage.getContent();
                  if (users.containsKey(userDto)) {
                    LOGGER.error("User already in the Lobby");
                  } else {
                    users.put(userDto, ctx.channel());
                    NetworkConfiguration.getServerLobby().getUsers().add(userDto);
                  }
                  sendServerLobby(NetworkConfiguration.getServerLobby());
                }
                case JOIN_GAME_LOBBY -> {
                  LOGGER.debug("At JOIN_GAME_LOBBY" + connectionMessage.getContent().getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  //TODO Muss der Nutzer aus der ServerLobby entfernt werden? Letzter Stand Nein
                  gameChannels.get(NetworkConfiguration.getServerLobby()).remove(ctx.channel());
                  GameLobby gameLobbyFromServer = NetworkConfiguration.getServerLobby()
                      .getGameLobbies().stream()
                      .filter(x -> x.getLobbyName().equals(gameLobby.getLobbyName())).findFirst()
                      .get();
                  //Add users channel to the ChannelGroup from the gamelobby
                  gameLobbyFromServer.getUsers().add(users.inverse().get(ctx.channel()));
                  gameChannels.get(gameLobbyFromServer).add(ctx.channel());
                  sendGameLobby(gameLobbyFromServer);
                }
                case START_GAME -> {
                  LOGGER.debug("At START_GAME" + connectionMessage.getContent().getClass());

                }
                case LEAVE_SERVER_LOBBY -> {
                  LOGGER.debug("At LEAVE_SERVER_LOBBY" + connectionMessage.getContent().getClass());
                }
                case LEAVE_GAME_LOBBY -> {
                  LOGGER.debug("At LEAVE_GAME_LOBBY" + connectionMessage.getContent().getClass());

                }
                case LEAVE_GAME -> {
                  LOGGER.debug("At LEAVE_GAME" + connectionMessage.getContent().getClass());

                }
                case CREATE_GAME_LOBBY -> {
                  LOGGER.debug("At CREATE_GAME_LOBBY" + connectionMessage.getContent().getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  NetworkConfiguration.getServerLobby().getGameLobbies().add(gameLobby);
                  sendServerLobby(NetworkConfiguration.getServerLobby());

                }
                default -> LOGGER.error("Server received a faulty connection message");
              }
            } catch (NullPointerException ignored) {
            }
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
    String message = Serializer.serialize(
        new StandardMessage(moveProcessor.getGameController().getGameState()));
    LOGGER.debug(message);
    for (Channel ch : channels) {
      LOGGER.debug("Send new gamestate to: " + ch.id());
      ch.writeAndFlush(
          new TextWebSocketFrame(message));
    }
  }

  private void sendServerLobby(ServerLobby serverLobby) {
    for (Channel ch : channels) {
      LOGGER.debug("Send new server lobby to: " + ch.id());
      ch.writeAndFlush(
          new TextWebSocketFrame(Serializer.serialize(
              new ConnectionMessage<ServerLobby>(ConnectionActions.ACCEPT_USER_LOBBY,
                  serverLobby))));
    }
  }

  private void sendGameLobby(GameLobby gameLobby) {
    for (Channel ch : gameChannels.get(gameLobby)) {
      LOGGER.debug("Send new server lobby to: " + ch.id());
      ch.writeAndFlush(
          new TextWebSocketFrame(Serializer.serialize(
              new ConnectionMessage<GameLobby>(ConnectionActions.ACCEPT_USER_LOBBY,
                  gameLobby))));
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    channels.add(ctx.channel());
    //TODO Manage the different games and the server lobby, multiple channelGroups could be the solution
    ctx.channel().attr(AttributeKey.newInstance("name")).set("Test");
  }

}
