package com.unima.risk6.network.server;

import static com.unima.risk6.network.server.GameServer.channels;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class GameLobbyChannels {

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

  public void addUserToGameLobby(GameLobby gameLobby, Channel channel) {
    channels.remove(channel);
    gameLobby.getUsers()
        .add(users.inverse().get(channel));
    System.out.println(gameChannels.containsKey(gameLobby));
    gameChannels.get(gameLobby).add(channel);
  }

  public void removeUserFromGameLobby(Channel channel,
      GameServerFrameHandler gameServerFrameHandler) {
    //leave gameChannel
    ChannelGroup currentGame = gameChannels.values().stream()
        .filter(x -> x.contains(channel)).findFirst().get();
    currentGame.remove(channel);
    //remove from game lobby
    gameChannels.inverse().get(currentGame).getUsers()
        .remove(users.inverse().get(channel));
    //join serverLobby channel
    channels.add(channel);
    //delete gamelobby if empty or change owner
    if (gameChannels.inverse().get(currentGame).getUsers().size()
        == 0) {

      NetworkConfiguration.getServerLobby().getGameLobbies()
          .remove(NetworkConfiguration.getServerLobby()
              .getGameLobbies().stream().filter(
                  x -> x.getLobbyName().equals(
                      gameChannels.inverse().get(currentGame)
                          .getLobbyName())).findFirst().get());
      gameChannels.inverse().remove(currentGame);
    } else {
      //Change owner
      gameChannels.inverse().get(currentGame).setLobbyOwner(
          gameChannels.inverse().get(currentGame).getUsers()
              .get(0));
      gameServerFrameHandler.sendGameLobby(gameChannels.inverse().get(currentGame));
    }

  }

  public void removeUserFromServerLobby(Channel channel) {
    NetworkConfiguration.getServerLobby().getUsers()
        .remove(users.inverse().get(channel));
  }

  public void createGameLobby(GameLobby gameLobby, Channel channel) {
    NetworkConfiguration.getServerLobby().getGameLobbies()
        .add(gameLobby);
    channels.remove(channel);
    gameChannels.put(gameLobby,
        new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    gameChannels.get(gameLobby).add(channel);
    gameChannels.keySet().forEach(System.out::println);
  }

  public ChannelGroup getChannelsByGameLobby(GameLobby gameLobby) {
    return gameChannels.get(gameLobby);
  }

  public UserDto getUserByChannel(Channel channel) {
    return users.inverse().get(channel);
  }

  public boolean containsUser(UserDto userDto) {
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


}
