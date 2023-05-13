package com.unima.risk6.network.server;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.CLAIM_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;
import static com.unima.risk6.network.server.GameServer.channels;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


  public void removeUserFromGameLobby(Channel channel,
      GameServerFrameHandler gameServerFrameHandler, boolean isDead) {
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
    NetworkConfiguration.getServerLobby().getUsers()
        .forEach(x -> System.out.println(x.getUsername() + " is in ServerLobby"));
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
    System.out.println(getUserByChannel(channel).getUsername() + " left");

    if (gameChannels.keySet().stream()
        .anyMatch(x -> x.getUsers().contains(getUserByChannel(channel)))) {

      if (moveProcessors.containsValue(gameChannels.get(gameChannels.keySet().stream()
          .filter(x -> x.getUsers().contains(getUserByChannel(channel))).findFirst().get()))) {
        //In Running Game
        LOGGER.debug("In Running game");
        ChannelGroup channelGroup = gameChannels.get(gameChannels.keySet().stream()
            .filter(x -> x.getUsers().contains(getUserByChannel(channel))).findFirst().get());
        GameController gameController = moveProcessors.inverse().get(channelGroup)
            .getGameController();
        GameState gameState = gameController.getGameState();
        MediumBot mediumBot;
        if (gameState.getCurrentPlayer().getUser()
            .equals(getUserByChannel(channel).getUsername())) {
          //player is current player
          LOGGER.debug("Current player left");

          Player player = gameState.getCurrentPlayer();
          GamePhase gamePhase = player.getCurrentPhase();
          mediumBot = new MediumBot(player);
          for (Country country : mediumBot.getCountries()) {
            country.setPlayer((Player) mediumBot);
          }
          MoveProcessor moveProcessor = moveProcessors.inverse().get(channelGroup);

          gameState.getActivePlayers().poll();
          gameState.getActivePlayers().add(mediumBot);
          int size = gameState.getActivePlayers().size();
          for (int i = 0; i < size - 1; i++) {
            gameState.getActivePlayers().add(gameState.getActivePlayers().poll());
          }
          gameState.setCurrentPlayer(gameState.getActivePlayers().peek());

          processBotMove(mediumBot, channelGroup, gsh, moveProcessor);
          gsh.sendGamestate(channelGroup, gameState);


        } else {
          LOGGER.debug("A player left");
          for (int i = 0; i < gameState.getActivePlayers().size(); i++) {
            Player player = gameState.getActivePlayers().poll();
            if (player.getUser().equals(getUserByChannel(channel).getUsername())) {
              //Player found
              mediumBot = new MediumBot(player);
              for (Country country : mediumBot.getCountries()) {
                country.setPlayer((Player) mediumBot);
              }
              gameState.getActivePlayers().add(mediumBot);
            } else {
              gameState.getActivePlayers().add(player);
            }
          }

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

  void processBotMove(AiBot aiBot, ChannelGroup channelGroup, GameServerFrameHandler gsh,
      MoveProcessor moveProcessor) {
    aiBot.setGameState(moveProcessor.getGameController().getGameState());
    Player player = (Player) aiBot;
    moveProcessor.getPlayerController().setPlayer(player);
    try {

      if (player.getCurrentPhase().equals(CLAIM_PHASE)) {
        Thread.sleep(500);
        Reinforce reinforce = aiBot.claimCountry();
        moveProcessor.processReinforce(reinforce);
        gsh.sendGamestate(channelGroup);
        moveProcessor.clearLastMoves();
        Thread.sleep(500);
        moveProcessor.processEndPhase(new EndPhase(player.getCurrentPhase()));
        gsh.sendGamestate(channelGroup);
        moveProcessor.clearLastMoves();
        Thread.sleep(500);
        Player currentPlayer = moveProcessor.getGameController().getGameState()
            .getCurrentPlayer();
        if (!player.getUser().equals(currentPlayer.getUser())
            && currentPlayer instanceof AiBot aiBot1) {
          processBotMove(aiBot1, channelGroup, gsh, moveProcessor);
        }
      }
      if (player.getCurrentPhase().equals(REINFORCEMENT_PHASE)) {
        gsh.processBotHandIn(channelGroup);
        Thread.sleep(1500);
        gsh.processBotReinforcementPhase(aiBot, channelGroup, player);
        Thread.sleep(3000);
      }
      if (player.getCurrentPhase().equals(ATTACK_PHASE)) {
        gsh.processBotAttackPhase(aiBot, channelGroup, player);
        Thread.sleep(3000);
      }
      if (player.getCurrentPhase().equals(FORTIFY_PHASE)) {
        gsh.processBotFortifyPhase(aiBot, channelGroup, player);
        Thread.sleep(500);
        Player currentPlayer = moveProcessor.getGameController().getGameState()
            .getCurrentPlayer();
        if (!player.getUser().equals(currentPlayer.getUser())
            && currentPlayer instanceof AiBot aiBot1) {
          processBotMove(aiBot1, channelGroup, gsh, moveProcessor);
        }

      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

  void handleGameOver(Channel channel, GameServerFrameHandler gsh) {
    LOGGER.debug("Before handle GameOver : moveProcessors Size: " + moveProcessors.size()
        + " gameChannels Size: " + gameChannels.size() + " GameLobbys in ServerLobby: "
        + NetworkConfiguration.getServerLobby().getGameLobbies().size() + " Channels Size: "
        + channels.size());
    ChannelGroup channelGroup = getChannelGroupByChannel(channel);
    GameLobby gameLobby = gameChannels.inverse().get(channelGroup);

    NetworkConfiguration.getServerLobby().getGameLobbies()
        .remove(NetworkConfiguration.getServerLobby()
            .getGameLobbies().stream().filter(
                x -> x.getLobbyName().equals(
                    gameLobby.getLobbyName())).findFirst().get());

    moveProcessors.inverse().remove(channelGroup);
    gameChannels.inverse().remove(channelGroup);

    channelGroup.forEach(this::removeUserFromServerLobby);
    channelGroup.forEach(ChannelOutboundInvoker::close);
    LOGGER.debug("After handle GameOver : moveProcessors Size: " + moveProcessors.size()
        + " gameChannels Size: " + gameChannels.size() + " GameLobbys in ServerLobby: "
        + NetworkConfiguration.getServerLobby().getGameLobbies().size() + " Channels Size: "
        + channels.size());
    gsh.sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());
  }


}
