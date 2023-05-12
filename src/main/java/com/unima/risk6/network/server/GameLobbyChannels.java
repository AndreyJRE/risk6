package com.unima.risk6.network.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.unima.risk6.network.server.GameServer.channels;

public class GameLobbyChannels {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameServerFrameHandler.class);

  private final BiMap<GameLobby, ChannelGroup> gameChannels = HashBiMap.create();
  private final BiMap<MoveProcessor, ChannelGroup> moveProcessors = HashBiMap.create();
  private final BiMap<UserDto, Channel> users = HashBiMap.create();

  public GameLobbyChannels() {
  }

  public ChannelGroup getChannelGroupByChannel(Channel channel) {
    return gameChannels.values().stream()
            .filter(x -> x.contains(channel)).findFirst()
            .orElse(channels);
  }

  public void putUsers(UserDto userDto, Channel channel) {
    users.put(userDto, channel);
  }


  public void removeUserFromGameLobby(Channel channel, GameServerFrameHandler gameServerFrameHandler, boolean isDead) {
    System.out.println(channel.id() + " left");
    UserDto deadUser = getUserByChannel(channel);
    //leave gameChannel
    GameLobby gameLobby = gameChannels.keySet().stream()
            .filter(x -> x.getUsers().contains(deadUser)).findFirst().get();

    //remove from game lobby
    gameLobby.getUsers().remove(users.inverse().get(channel));
    if (!isDead) {
      //join serverLobby channel
      channels.add(channel);
    }
    //delete gamelobby if empty or change owner
    if (gameLobby.getUsers().size()
            == 0) {

      NetworkConfiguration.getServerLobby().getGameLobbies()
              .remove(NetworkConfiguration.getServerLobby()
                      .getGameLobbies().stream().filter(
                              x -> x.getLobbyName().equals(
                                      gameLobby.getLobbyName())).findFirst().get());
      gameChannels.remove(gameLobby);
    } else {
      //Change owner
      gameLobby.setLobbyOwner(
              gameLobby.getUsers()
                      .get(0));
      gameServerFrameHandler.sendGameLobby(gameLobby);
    }
    if (isDead) {
      users.inverse().remove(channel);
    }

  }

  public void removeUserFromServerLobby(Channel channel) {
    LOGGER.debug("Remove from server lobby: " + users.inverse().get(channel));
    NetworkConfiguration.getServerLobby().getUsers()
            .remove(users.inverse().get(channel));
    NetworkConfiguration.getServerLobby().getUsers().forEach(x -> System.out.println(x.getUsername() + " is in ServerLobby"));
    users.inverse().remove(channel);
    channels.remove(channel);
  }

  public void createGameLobby(GameLobby gameLobby, Channel channel) {
    NetworkConfiguration.getServerLobby().getGameLobbies()
            .add(gameLobby);
    gameChannels.put(gameLobby,
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    addUserToGameLobby(gameLobby, channel);
  }

  public void addUserToGameLobby(GameLobby gameLobby, Channel channel) {
    System.out.println(channel.id() + " added to gamelobby");
    channels.remove(channel);
    if (!gameLobby.getUsers().contains(users.inverse().get(channel))) {
      gameLobby.getUsers()
              .add(users.inverse().get(channel));
    }

    gameChannels.get(gameLobby).add(channel);
  }

  public ChannelGroup getChannelsByGameLobby(GameLobby gameLobby) {
    return gameChannels.get(gameLobby);
  }

  public UserDto getUserByChannel(Channel channel) {
    return users.inverse().get(channel);
  }

  public boolean containsUser(UserDto userDto) {
    LOGGER.debug("Searching for user " + userDto.getUsername());
    users.keySet().stream().forEach(x -> System.out.println(x.getUsername()));
    return users.keySet().stream().anyMatch(x -> x.equals(userDto));
  }

  public BiMap<UserDto, Channel> getUsers() {
    return users;
  }

  public MoveProcessor getMoveProcessor(Channel channel) {
    return moveProcessors.inverse()
            .get(moveProcessors.values().stream().filter(x -> x.contains(channel)).findFirst().get());
  }

  public MoveProcessor createMoveProcessor(Channel channel) {
    ChannelGroup channelGroup = gameChannels.values().stream().filter(x -> x.contains(channel))
            .findFirst().get();
    moveProcessors.put(new MoveProcessor(), channelGroup);
    return moveProcessors.inverse().get(channelGroup);
  }

  public void handleExit(Channel channel, GameServerFrameHandler gsh) {
    System.out.println(gameChannels.size());
    System.out.println(users.inverse().get(channel).getUsername() + " left");

    if (gameChannels.keySet().stream().anyMatch(x -> x.getUsers().contains(getUserByChannel(channel)))) {
      if (moveProcessors.values().contains(getChannelGroupByChannel(channel))) {
        //In Running Game
        System.out.println("In Running game");
        GameState gameState = moveProcessors.inverse().get(getChannelGroupByChannel(channel)).getGameController().getGameState();
        //TODO passt der Vergleich
        if (gameState.getCurrentPlayer().equals(users.inverse().get(channel))) {
          //player is current player
          //TODO blocken, wenn nutzer entfernt wird
        }

      } else {
        //In Gamelobby
        System.out.println("In Gamelobby");
        removeUserFromGameLobby(channel, gsh, true);
        gsh.sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());

      }
    } else {
      System.out.println("else");
      removeUserFromServerLobby(channel);
      gsh.sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());
    }

  }


}
