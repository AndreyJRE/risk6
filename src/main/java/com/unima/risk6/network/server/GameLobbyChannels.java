package com.unima.risk6.network.server;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.CLAIM_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.NOT_ACTIVE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;
import static com.unima.risk6.network.server.GameServer.channels;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains maps, that map users to netty channels and netty channel groups to
 * moveProcessors and gameChannels. The provided methods manipulate these maps and ensure, that the
 * game is initialized in the right way, that the server gets cleaned up, when the game is over,
 * that exiting users are replaced by bots, etc
 *
 * @author jferch
 */

public class GameLobbyChannels {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameLobbyChannels.class);

  final BiMap<GameLobby, ChannelGroup> gameChannels = HashBiMap.create();
  final BiMap<MoveProcessor, ChannelGroup> moveProcessors = HashBiMap.create();
  final BiMap<UserDto, Channel> users = HashBiMap.create();

  public GameLobbyChannels() {
  }

  /**
   * Return the initialized probability array object, strictly for testing purposes.
   *
   * @param channel the channel you want to be searched for
   * @return a ChannelGroup of a GameLobby or if not found of the ServerLobby
   */
  ChannelGroup getChannelGroupByChannel(Channel channel) {
    return gameChannels.values().stream()
        .filter(x -> x.contains(channel)).findFirst()
        .orElse(channels);
  }

  /**
   * Associates a user with a channel.
   *
   * @param userDto the user to associate.
   * @param channel the channel to associate.
   */
  void putUsers(UserDto userDto, Channel channel) {
    users.put(userDto, channel);
  }

  /**
   * Remove a user from the game lobby.
   *
   * @param channel                the channel associated with the user.
   * @param gameServerFrameHandler the GameServerFrameHandler needed to send the updated GameLobby
   *                               to the other users
   * @param isDead                 a boolean indicating whether the users channe is inactive or
   *                               not.
   */
  void removeUserFromGameLobby(Channel channel,
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
    if (gameLobby.getUsers().size() == 0) {

      NetworkConfiguration.getServerLobby().getGameLobbies()
          .remove(NetworkConfiguration.getServerLobby()
              .getGameLobbies().stream().filter(
                  x -> x.getLobbyName().equals(
                      gameLobby.getLobbyName())).findFirst().get());
      gameChannels.remove(gameLobby);
      //delete moveprocessor if the game is running
      if (moveProcessors.values().stream().anyMatch(x -> x.contains(channel))) {
        moveProcessors.inverse().remove(getChannelGroupByChannel(channel));
      }
    } else {
      //Change owner
      gameLobby.setLobbyOwner(
          gameLobby.getUsers()
              .get(0));
      gameServerFrameHandler.sendGameLobby(gameLobby);
    }

  }

  /**
   * Remove a user from the server lobby.
   *
   * @param channel the channel associated with the user.
   */
  void removeUserFromServerLobby(Channel channel) {
    LOGGER.debug("Remove from server lobby: " + users.inverse().get(channel));
    NetworkConfiguration.getServerLobby().getUsers()
        .remove(users.inverse().get(channel));
    users.inverse().remove(channel);
    channels.remove(channel);
  }

  /**
   * Create a game lobby and associate it with a channel.
   *
   * @param gameLobby the GameLobby to put into the gameChannels.
   * @param channel   the channel to associate with the game lobby.
   */
  void createGameLobby(GameLobby gameLobby, Channel channel) {
    NetworkConfiguration.getServerLobby().getGameLobbies()
        .add(gameLobby);
    gameChannels.put(gameLobby,
        new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    addUserToGameLobby(gameLobby, channel);
  }

  /**
   * Add a user to the game lobby.
   *
   * @param gameLobby the GameLobby to add the user to.
   * @param channel   the channel associated with the user.
   */
  void addUserToGameLobby(GameLobby gameLobby, Channel channel) {
    System.out.println(channel.id() + " added to gamelobby");
    channels.remove(channel);
    if (!gameLobby.getUsers().contains(users.inverse().get(channel))) {
      gameLobby.getUsers()
          .add(users.inverse().get(channel));
    }
    gameChannels.get(gameLobby).add(channel);
  }

  /**
   * Get the ChannelGroup associated with a game lobby.
   *
   * @param gameLobby the GameLobby to get the ChannelGroup for.
   * @return ChannelGroup associated with the GameLobby.
   */
  ChannelGroup getChannelsByGameLobby(GameLobby gameLobby) {
    return gameChannels.get(gameLobby);
  }

  /**
   * Get the GameLobby associated with a channel.
   *
   * @param channel the channel to get the GameLobby for.
   * @return GameLobby associated with the Channel.
   */
  GameLobby getGameLobbyByChannel(Channel channel) {
    return gameChannels.inverse()
        .get(gameChannels.values().stream().filter(x -> x.contains(channel)).findFirst().get());
  }

  /**
   * Get the user associated with the given channel.
   *
   * @param channel the channel associated with the user.
   * @return UserDto object that represents the user associated with the channel.
   */
  UserDto getUserByChannel(Channel channel) {
    return users.inverse().get(channel);
  }

  /**
   * Check if the given user exists in the system.
   *
   * @param userDto user object to check.
   * @return boolean indicating whether the user exists or not.
   */
  boolean containsUser(UserDto userDto) {
    LOGGER.debug("Searching for user " + userDto.getUsername());
    users.keySet().stream().forEach(x -> System.out.println(x.getUsername()));
    return users.keySet().stream().anyMatch(x -> x.equals(userDto));
  }

  /**
   * Get all users currently in the system.
   *
   * @return BiMap of UserDto and Channels representing all users.
   */
  BiMap<UserDto, Channel> getUsers() {
    return users;
  }

  /**
   * Get the MoveProcessor associated with the given channel.
   *
   * @param channel the channel associated with the MoveProcessor.
   * @return MoveProcessor object associated with the channel.
   */
  MoveProcessor getMoveProcessor(Channel channel) {
    return moveProcessors.inverse()
        .get(moveProcessors.values().stream().filter(x -> x.contains(channel)).findFirst().get());
  }

  /**
   * Create a MoveProcessor and associate it with a channel.
   *
   * @param channel the channel to associate with the MoveProcessor.
   * @return MoveProcessor object that was created and associated with the channel.
   */
  MoveProcessor createMoveProcessor(Channel channel) {
    ChannelGroup channelGroup = gameChannels.values().stream().filter(x -> x.contains(channel))
        .findFirst().get();
    moveProcessors.put(new MoveProcessor(), channelGroup);
    return moveProcessors.inverse().get(channelGroup);
  }

  /**
   * Replace a user with a bot in the game.
   *
   * @param gameState  the current state of the game.
   * @param gameLobby  the game lobby the user is in.
   * @param playerName the name of the player to be replaced.
   * @param botType    the type of bot to replace the player with.
   */

  void replaceUser(GameState gameState, GameLobby gameLobby, String playerName,
      String botType) {
    Player bot = new Player("I should not exist");
    for (int i = 0; i < gameState.getActivePlayers().size(); i++) {
      Player player = gameState.getActivePlayers().poll();
      if (player.getUser().equals(playerName)) {
        //Player found
        switch (botType) {
          case "easy" -> bot = new EasyBot(player);
          case "medium" -> bot = new MediumBot(player);
          case "hard" -> bot = new HardBot(player);

        }
        for (Country country : bot.getCountries()) {
          country.setPlayer(bot);
        }
        gameState.getActivePlayers().add(bot);
      } else {
        gameState.getActivePlayers().add(player);
      }
    }

    gameLobby.getBots().add(bot.getUser());
  }

  /**
   * Handles the case when a user exits the game.
   *
   * @param channel the channel of the user that left.
   * @param gsh     the Game Server Frame Handler for sending updates to clients.
   */
  void handleExit(Channel channel, GameServerFrameHandler gsh) {

    LOGGER.debug("Before handle Exit : moveProcessors Size: " + moveProcessors.size()
        + " gameChannels Size: " + gameChannels.size() + "Users size: " + users.size()
        + " GameLobbies in ServerLobby: "
        + NetworkConfiguration.getServerLobby().getGameLobbies().size() + " Users in Serverlobby: "
        + NetworkConfiguration.getServerLobby().getUsers().size());

    System.out.println(gameChannels.size());
    System.out.println(getUserByChannel(channel).getUsername() + " left");

    if (gameChannels.keySet().stream()
        .anyMatch(x -> x.getUsers().contains(getUserByChannel(channel)))) {
      GameLobby gameLobby = gameChannels.keySet().stream()
          .filter(x -> x.getUsers().contains(getUserByChannel(channel))).findFirst().get();
      MediumBot mediumBot;

      if (moveProcessors.containsValue(gameChannels.get(gameChannels.keySet().stream()
          .filter(x -> x.getUsers().contains(getUserByChannel(channel))).findFirst().get()))) {
        //In Running Game
        LOGGER.debug("In Running game");
        ChannelGroup channelGroup = gameChannels.get(gameChannels.keySet().stream()
            .filter(x -> x.getUsers().contains(getUserByChannel(channel))).findFirst().get());
        GameController gameController = moveProcessors.inverse().get(channelGroup)
            .getGameController();
        GameState gameState = gameController.getGameState();
        if (gameState.getCurrentPlayer().getUser()
            .equals(getUserByChannel(channel).getUsername())) {
          //player is current player
          LOGGER.debug("Current player left");

          Player player = gameState.getCurrentPlayer();
          mediumBot = new MediumBot(player);
          for (Country country : mediumBot.getCountries()) {
            country.setPlayer(mediumBot);
          }
          MoveProcessor moveProcessor = moveProcessors.inverse().get(channelGroup);

          gameState.getActivePlayers().poll();
          gameState.getActivePlayers().add(mediumBot);
          int size = gameState.getActivePlayers().size();
          for (int i = 0; i < size - 1; i++) {
            gameState.getActivePlayers().add(gameState.getActivePlayers().poll());
          }
          gameState.setCurrentPlayer(gameState.getActivePlayers().peek());

          if (gameState.getActivePlayers().stream().allMatch(n -> n instanceof AiBot)) {
            gameState.setGameOver(true);
            gameState.getActivePlayers().forEach(n -> n.setCurrentPhase(NOT_ACTIVE));
          } else {
            processBotMove(mediumBot, channelGroup, gsh, moveProcessor);
          }

          gsh.sendGamestate(channelGroup, gameState);


        } else {
          LOGGER.debug("A player left");
          replaceUser(gameState, gameLobby, getUserByChannel(channel).getUsername(), "medium");
        }
      } else {
        //In Gamelobby
        System.out.println("In Gamelobby");


      }
      removeUserFromGameLobby(channel, gsh, true);
      removeUserFromServerLobby(channel);
      gsh.sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());
    } else {
      System.out.println("else");
      removeUserFromServerLobby(channel);
      gsh.sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());
    }

    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    LOGGER.debug("After handle Exit : moveProcessors Size: " + moveProcessors.size()
        + " gameChannels Size: " + gameChannels.size() + "Users size: " + users.size()
        + " GameLobbies in ServerLobby: "
        + NetworkConfiguration.getServerLobby().getGameLobbies().size()
        + " Users in Serverlobby: "
        + NetworkConfiguration.getServerLobby().getUsers().size());

  }

  /**
   * Processes the move for an AI bot.
   *
   * @param aiBot         the AI bot that is making a move.
   * @param channelGroup  the group of channels that the bot is associated with.
   * @param gsh           the Game Server Frame Handler for processing the specific bot move, like
   *                      attack.
   * @param moveProcessor the Move Processor for processing bot moves.
   */
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

  /**
   * Handles the case when the game is over.
   *
   * @param channel the channel where the game over event originated.
   * @param gsh     the Game Server Frame Handler for sending updates to clients.
   */
  void handleGameOver(Channel channel, GameServerFrameHandler gsh) {
    LOGGER.debug("Before handle GameOver : moveProcessors Size: " + moveProcessors.size()
        + " gameChannels Size: " + gameChannels.size() + "Users size: " + users.size()
        + " GameLobbies in ServerLobby: "
        + NetworkConfiguration.getServerLobby().getGameLobbies().size() + " Channels Size: "
        + GameServerFrameHandler.channels.size());
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
        + " gameChannels Size: " + gameChannels.size() + "Users size: " + users.size()
        + " GameLobbies in ServerLobby: "
        + NetworkConfiguration.getServerLobby().getGameLobbies().size() + " Channels Size: "
        + GameServerFrameHandler.channels.size());
    gsh.sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());
  }


}
