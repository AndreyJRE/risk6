package com.unima.risk6.network.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.ai.tutorial.Tutorial;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Dice;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.HandController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.CountriesUiConfiguration;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import com.unima.risk6.network.message.ChatMessage;
import com.unima.risk6.network.message.ConnectionMessage;
import com.unima.risk6.network.message.StandardMessage;
import com.unima.risk6.network.message.enums.ConnectionActions;
import com.unima.risk6.network.serialization.Deserializer;
import com.unima.risk6.network.serialization.Serializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameServerFrameHandler.class);

  protected static ChannelGroup channels;
  private final GameLobbyChannels gameLobbyChannels;
  private MoveProcessor moveProcessor;

  /**
   * Constructor of GameServerFrameHandler with the given channels and GameLobbyChannels.
   *
   * @param channels          the ChannelGroup of active channels.
   * @param gameLobbyChannels the GameLobbyChannels which manages game lobbies.
   */
  GameServerFrameHandler(ChannelGroup channels, GameLobbyChannels gameLobbyChannels) {
    GameServerFrameHandler.channels = channels;
    this.gameLobbyChannels = gameLobbyChannels;
  }

  /**
   * Returns the same game lobby as given as parameter but ensures that the reference is correct.
   *
   * @param gameLobby   the game lobby to search for.
   * @param serverLobby the server lobby where the game lobby is stored.
   * @return a game lobby with correct references.
   */
  private static GameLobby getServerGameLobby(GameLobby gameLobby, ServerLobby serverLobby) {
    return serverLobby.getGameLobbies().stream()
        .filter(x -> x.getLobbyName().equals(gameLobby.getLobbyName())).findFirst().get();
  }

  /**
   * Called when a client's channel becomes inactive. Logs the occurrence and handles the exit of
   * the client from the game.
   *
   * @param ctx the ChannelHandlerContext tied to the client's channel.
   * @throws Exception if an error occurs during the handling of the client's exit.
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    LOGGER.info("Lost connection to one client.");
    gameLobbyChannels.handleExit(ctx.channel(), this);

  }

  /**
   * Called when a client's channel becomes active. Adds the new channel to the ChannelGroup.
   *
   * @param ctx the ChannelHandlerContext tied to the client's channel.
   * @throws Exception if an error occurs during the addition of the channel to the group.
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    channels.add(ctx.channel());
    LOGGER.debug("Channel: " + ctx.channel().id() + " got active.");
  }

  /**
   * Handles the reading of a WebSocketFrame from a client's channel. The Message gets deserialized
   * and then processed.
   *
   * @param ctx   the ChannelHandlerContext tied to the client's channel.
   * @param frame the WebSocketFrame read from the client's channel.
   * @throws Exception if an error occurs during the handling of the WebSocketFrame.
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

    if (frame instanceof TextWebSocketFrame) {
      String request = ((TextWebSocketFrame) frame).text();
      JsonObject json = null;
      try {
        moveProcessor = gameLobbyChannels.getMoveProcessor(ctx.channel());
      } catch (java.util.NoSuchElementException ignored) {
      }
      try {
        LOGGER.debug("Server: Trying to read message");
        json = JsonParser.parseString(request).getAsJsonObject();
      } catch (Exception e) {
        LOGGER.debug("Not a JSON: " + request + "\n Exception: " + e);
      }
      if (json != null) {
        if (GameConfiguration.isTutorialOver()) {
          gameLobbyChannels.replaceUser(moveProcessor.getGameController().getGameState(),
              gameLobbyChannels.getGameLobbyByChannel(ctx.channel()), "Johnny Test",
              GameConfiguration.getBotDifficulty().toLowerCase());
          GameConfiguration.setTutorialOver(false);
        }
        LOGGER.debug(
            "Server Received Message with ContentType: " + json.get("contentType").getAsString());
        ChannelGroup channelGroup = gameLobbyChannels.getChannelGroupByChannel(ctx.channel());
        switch (json.get("contentType").getAsString()) {
          case "GAME_STATE" -> {
            LOGGER.error("The server should not receive a gamestate");
          }
          case "ATTACK" -> {
            LOGGER.debug("The server received a attack object");
            Attack attack = (Attack) Deserializer.deserialize(request,
                moveProcessor.getGameController().getGameState()).getContent();
            boolean isGameOver = moveProcessor.processAttack(attack);
            sendGamestate(channelGroup);
            moveProcessor.clearLastMoves();
            if (isGameOver) {
              LOGGER.info("Game Over!");
              gameLobbyChannels.handleGameOver(ctx.channel(), this);
            }
          }
          case "REINFORCE" -> {
            LOGGER.debug("The server received a reinforce object");
            Reinforce reinforce = (Reinforce) Deserializer.deserialize(request,
                moveProcessor.getGameController().getGameState()).getContent();
            moveProcessor.processReinforce(reinforce);
            sendGamestate(channelGroup);
            moveProcessor.clearLastMoves();
          }
          case "FORTIFY" -> {
            LOGGER.debug("The server received a fortify object");
            Fortify fortify = (Fortify) Deserializer.deserialize(request,
                moveProcessor.getGameController().getGameState()).getContent();
            moveProcessor.processFortify(fortify);
            sendGamestate(channelGroup);
            moveProcessor.clearLastMoves();
          }
          case "HAND_IN" -> {
            LOGGER.debug("The server received a hand in object");
            HandIn handIn = (HandIn) Deserializer.deserialize(request).getContent();
            moveProcessor.processHandIn(handIn);
            sendGamestate(channelGroup);
            moveProcessor.clearLastMoves();
          }
          case "END_PHASE" -> {
            LOGGER.debug("The server received a end phase object");
            Player currentPlayer = moveProcessor.getGameController().getCurrentPlayer();
            EndPhase endPhase = (EndPhase) Deserializer.deserialize(request).getContent();
            moveProcessor.processEndPhase(endPhase);
            Player currentPlayerAfter = moveProcessor.getGameController().getCurrentPlayer();
            sendGamestate(channelGroup);
            moveProcessor.clearLastMoves();
            if (!currentPlayer.equals(currentPlayerAfter)
                && currentPlayerAfter instanceof AiBot aiBot) {
              processBotMove(aiBot, channelGroup);
            }


          }
          case "CHAT_MESSAGE" -> {
            LOGGER.debug("The server received a chat message object");
            sendChatMessage(ctx.channel(), request);
          }
          case "CONNECTION" -> {
            LOGGER.debug("The server received a connection message");
            ConnectionMessage connectionMessage = null;
            try {
              connectionMessage = (ConnectionMessage) Deserializer.deserializeConnectionMessage(
                  request);
            } catch (Exception e) {
              LOGGER.error("Error at deserializing ConnectionMessage: " + e);
            }
            try {
              switch (connectionMessage.getConnectionActions()) {

                case JOIN_SERVER_LOBBY -> {
                  UserDto userDto = (UserDto) connectionMessage.getContent();
                  NetworkConfiguration.getServerLobby().getUsers()
                      .forEach(x -> LOGGER.debug(x.getUsername() + " is in ServerLobby"));
                  LOGGER.debug("On JOIN_SERVER_LOBBY: " + userDto.getUsername() + " wants to join");

                  if (gameLobbyChannels.containsUser(userDto)) {
                    LOGGER.error("User already in the Lobby");
                    sendDropMessage(ctx.channel(), ConnectionActions.DROP_USER_SERVER_LOBBY,
                        "User already in the Lobby");
                  } else {
                    gameLobbyChannels.putUsers(userDto, ctx.channel());
                    NetworkConfiguration.getServerLobby().getUsers().add(userDto);
                    sendServerLobby(NetworkConfiguration.getServerLobby(),
                        ConnectionActions.ACCEPT_JOIN_SERVER_LOBBY);
                  }
                }
                case JOIN_GAME_LOBBY -> {
                  LOGGER.debug("At JOIN_GAME_LOBBY " + connectionMessage.getContent().getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  GameLobby gameLobbyFromServer = getServerGameLobby(gameLobby,
                      NetworkConfiguration.getServerLobby());

                  if (gameLobbyFromServer.getBots().size() + gameLobbyFromServer.getUsers().size()
                      < gameLobbyFromServer.getMaxPlayers()) {

                    //Add users channel to the ChannelGroup from the gamelobby

                    gameLobbyChannels.addUserToGameLobby(gameLobbyFromServer, ctx.channel());

                    sendGameLobby(gameLobbyFromServer);
                    sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());
                  } else {
                    sendDropMessage(ctx.channel(), ConnectionActions.DROP_USER_GAME_LOBBY,
                        "The GameLobby is full");
                  }
                }
                case CREATE_TUTORIAL_LOBBY -> {
                  LOGGER.debug(
                      "At CREATE_TUTORIAL_GAME_LOBBY" + connectionMessage.getContent().getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  gameLobbyChannels.createGameLobby(gameLobby, ctx.channel());
                  sendCreatedTutorialGameLobby(gameLobby);
                }
                case JOIN_BOT_GAME_LOBBY -> {
                  LOGGER.debug(
                      "At JOIN_BOT_GAME_LOBBY " + connectionMessage.getContent().getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  GameLobby gameLobbyFromServer = getServerGameLobby(gameLobby,
                      NetworkConfiguration.getServerLobby());
                  String bot = gameLobby.getBots().stream()
                      .filter(x -> !gameLobbyFromServer.getBots().contains(x)).findFirst().get();
                  gameLobbyFromServer.getBots().add(bot);
                  ServerLobby serverLobby = NetworkConfiguration.getServerLobby();
                  sendGameLobby(gameLobbyFromServer);
                  sendUpdatedServerLobby(serverLobby);

                }
                case REMOVE_BOT_FROM_LOBBY -> {
                  LOGGER.debug(
                      "At REMOVE_BOT_FROM_LOBBY " + connectionMessage.getContent().getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  GameLobby gameLobbyFromServer = getServerGameLobby(gameLobby,
                      NetworkConfiguration.getServerLobby());
                  String bot = gameLobbyFromServer.getBots().stream()
                      .filter(x -> !gameLobby.getBots().contains(x)).findFirst().get();
                  gameLobbyFromServer.getBots().remove(bot);
                  ServerLobby serverLobby = NetworkConfiguration.getServerLobby();
                  sendGameLobby(gameLobbyFromServer);
                  sendUpdatedServerLobby(serverLobby);
                }
                case START_GAME -> {
                  LOGGER.debug("At START_GAME" + connectionMessage.getContent().getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  GameLobby myServerGameLobby = getServerGameLobby(gameLobby,
                      NetworkConfiguration.getServerLobby());
                  //Send max players to current players
                  int newMaxPlayers =
                      myServerGameLobby.getUsers().size() + myServerGameLobby.getBots().size();
                  myServerGameLobby.setMaxPlayers(newMaxPlayers);
                  sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());
                  moveProcessor = gameLobbyChannels.createMoveProcessor(ctx.channel());
                  processStartGame(myServerGameLobby);

                }
                case LEAVE_SERVER_LOBBY -> {
                  LOGGER.debug("At LEAVE_SERVER_LOBBY");

                  LOGGER.debug("Sizes of ChannelGroup " + channels.size() + " ServerLobby "
                      + NetworkConfiguration.getServerLobby().getUsers().size() + " UsersList "
                      + gameLobbyChannels.getUsers().size());
                  //remove from LobbyObject
                  gameLobbyChannels.removeUserFromServerLobby(ctx.channel());
                  LOGGER.debug("Sizes of ChannelGroup " + channels.size() + " ServerLobby "
                      + NetworkConfiguration.getServerLobby().getUsers().size() + " UsersList "
                      + gameLobbyChannels.getUsers().size());
                  sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());


                }
                case LEAVE_GAME_LOBBY -> {
                  LOGGER.debug("At LEAVE_GAME_LOBBY");
                  //LOGGER.debug("Sizes of ChannelGroup " + channels.size() + " ServerLobby " + NetworkConfiguration.getServerLobby().getUsers().size() + " UsersList " + users.size());
                  gameLobbyChannels.removeUserFromGameLobby(ctx.channel(), this, false);
                  sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());


                }
                case LEAVE_GAME -> {
                  LOGGER.debug("At LEAVE_GAME" + connectionMessage.getContent().getClass());

                }
                case CREATE_GAME_LOBBY -> {
                  LOGGER.debug("At CREATE_GAME_LOBBY" + connectionMessage.getContent().getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  if (NetworkConfiguration.getServerLobby().getGameLobbies().stream()
                      .anyMatch(x -> x.getLobbyName().equals(gameLobby.getLobbyName()))) {
                    //If theres already a lobby with the same name
                    LOGGER.error("There is another lobby with the same name.");
                    sendDropMessage(ctx.channel(), ConnectionActions.DROP_CREATE_GAME_LOBBY,
                        "There is another lobby with the same name.");
                  } else {
                    gameLobbyChannels.createGameLobby(gameLobby, ctx.channel());
                    sendCreatedGameLobby(NetworkConfiguration.getServerLobby(), gameLobby);
                  }

                }
                case START_TUTORIAL -> {
                  LOGGER.debug("At START_TUTORIAL" + connectionMessage.getContent().getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  GameLobby myServerGameLobby = getServerGameLobby(gameLobby,
                      NetworkConfiguration.getServerLobby());
                  moveProcessor = gameLobbyChannels.createMoveProcessor(ctx.channel());
                  processStartTutorial(myServerGameLobby);
                }
                default -> LOGGER.error("Server received a faulty connection message");
              }
            } catch (NullPointerException e) {
              LOGGER.debug(e.toString());
            }
          }
          default -> {
            LOGGER.debug("The Message received wasnt a valid Message\nMessage: " + json);

          }
        }
      }
    } else {
      String message = "unsupported frame type: " + frame.getClass().getName();
      throw new UnsupportedOperationException(message);
    }

  }

  /**
   * Sends the current game state to all the clients in the given ChannelGroup.
   *
   * @param channelGroup the group of clients to send the game state to.
   */
  void sendGamestate(ChannelGroup channelGroup) {
    String message = Serializer.serialize(
        new StandardMessage<GameState>(moveProcessor.getGameController().getGameState()));
    LOGGER.debug(message);
    for (Channel ch : channelGroup) {
      LOGGER.debug("Send new gamestate to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(message));
    }
  }

  /**
   * Sends a specific game state to all the clients in the given ChannelGroup.
   *
   * @param channelGroup the group of clients to send the game state to.
   * @param gameState    the specific game state to send.
   */
  protected void sendGamestate(ChannelGroup channelGroup, GameState gameState) {
    String message = Serializer.serialize(new StandardMessage<GameState>(gameState));
    LOGGER.debug(message);
    for (Channel ch : channelGroup) {
      LOGGER.debug("Send new gamestate to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(message));
    }
  }

  /**
   * Sends the first game state to all the clients in the given GameLobby. Only use it, to start the
   * game!
   *
   * @param gameLobby the lobby of clients to send the first game state to.
   */
  private void sendFirstGamestate(GameLobby gameLobby) {
    String message = Serializer.serialize(
        new ConnectionMessage<>(ConnectionActions.ACCEPT_START_GAME,
            moveProcessor.getGameController().getGameState()));
    LOGGER.debug(message);
    for (Channel ch : gameLobbyChannels.getChannelsByGameLobby(gameLobby)) {
      LOGGER.debug("Send first gamestate to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(message));
    }
  }

  /**
   * Sends the current game lobby state to all the clients in the given GameLobby.
   *
   * @param gameLobby the lobby of clients to send the game lobby state to.
   */
  protected void sendGameLobby(GameLobby gameLobby) {
    String serializedGameLobby = Serializer.serialize(
        new ConnectionMessage<>(ConnectionActions.ACCEPT_JOIN_GAME_LOBBY, gameLobby));
    for (Channel ch : gameLobbyChannels.getChannelsByGameLobby(gameLobby)) {
      LOGGER.debug("Send a game lobby to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(serializedGameLobby));
    }
  }

  /**
   * Sends the updated server lobby to all the clients in the given ServerLobby. Sends the created.
   * GameLobby to the user who created it.
   *
   * @param serverLobby the server lobby to send to the clients.
   * @param gameLobby   the game lobby to send.
   */
  private void sendCreatedGameLobby(ServerLobby serverLobby, GameLobby gameLobby) {
    sendServerLobby(serverLobby, ConnectionActions.ACCEPT_UPDATE_SERVER_LOBBY);
    String serialized1 = Serializer.serialize(
        new ConnectionMessage<>(ConnectionActions.ACCEPT_CREATE_LOBBY, gameLobby));

    for (Channel channel : gameLobbyChannels.getChannelsByGameLobby(gameLobby)) {
      LOGGER.debug("Send game lobby to : " + channel.id());
      channel.writeAndFlush(new TextWebSocketFrame(serialized1));

    }
  }

  /**
   * Sends the created tutorial game lobby to the user who started the Tutorial.
   *
   * @param gameLobby the tutorial game lobby to send.
   */
  private void sendCreatedTutorialGameLobby(GameLobby gameLobby) {
    String serialized1 = Serializer.serialize(
        new ConnectionMessage<>(ConnectionActions.ACCEPT_TUTORIAL_CREATE_LOBBY, gameLobby));
    for (Channel channel : gameLobbyChannels.getChannelsByGameLobby(gameLobby)) {
      LOGGER.debug("Send game lobby to : " + channel.id());
      channel.writeAndFlush(new TextWebSocketFrame(serialized1));

    }
  }

  /**
   * Sends the current state of the server lobby to all connected clients.
   *
   * @param serverLobby       the current state of the server lobby.
   * @param connectionActions the type of action that triggered this update.
   */
  private void sendServerLobby(ServerLobby serverLobby, ConnectionActions connectionActions) {
    String serialized = Serializer.serialize(
        new ConnectionMessage<>(connectionActions, serverLobby));
    for (Channel ch : channels) {
      LOGGER.debug("Send new server lobby to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(serialized));
    }
  }

  /**
   * Sends the updated state of the server lobby to all connected clients.
   *
   * @param serverLobby the updated state of the server lobby.
   */
  protected void sendUpdatedServerLobby(ServerLobby serverLobby) {
    sendServerLobby(serverLobby, ConnectionActions.ACCEPT_UPDATE_SERVER_LOBBY);
  }

  /**
   * Sends a chat message from a specific channel to all channels in the same channel group. The
   * channel group is either the channel group of a running game or of the server lobby.
   *
   * @param channel the channel from which the chat message originates.
   * @param request the original content of the chat message.
   */
  public void sendChatMessage(Channel channel, String request) {
    ChatMessage chatMessage = Deserializer.deserializeChatMessage(request);
    chatMessage.setContent(gameLobbyChannels.getUserByChannel(channel).getUsername() + ": "
        + chatMessage.getContent());
    String message = Serializer.serialize(chatMessage);
    gameLobbyChannels.getChannelGroupByChannel(channel).forEach(ch -> {
      LOGGER.debug("Send chatmessage: " + message + " to channel: " + channel.id());
      ch.writeAndFlush(new TextWebSocketFrame(message));
    });


  }

  /**
   * Sends a message indicating that a players request got dropped.
   *
   * @param channel           the channel to which the drop message is to be sent.
   * @param connectionActions the type of action that triggered this message.
   * @param string            an explanation, why the request got dropped.
   */
  public void sendDropMessage(Channel channel, ConnectionActions connectionActions, String string) {
    channel.writeAndFlush(new TextWebSocketFrame(
        Serializer.serialize(new ConnectionMessage<String>(connectionActions, string))));
  }

  /**
   * Processes the start of a game in the given game lobby.
   *
   * @param gameLobby the game lobby to start the game in.
   */
  public void processStartGame(GameLobby gameLobby) {
    //TODO Bots should not start the game
    List<String> usersList = gameLobby.getUsers().stream().map(UserDto::getUsername).toList();
    GameState gameState = GameConfiguration.configureGame(usersList,
        gameLobby.getBots().stream().map(x -> {
          if (x.contains("Easy")) {
            return new EasyBot(x);
          } else if (x.contains("Medium")) {
            return new MediumBot(x);
          } else {
            return (AiBot) new HardBot(x);
          }
        }).toList());
    gameState.getActivePlayers().stream().filter(x -> x instanceof AiBot)
        .forEach(x -> ((AiBot) x).setGameState(gameState));
    gameState.setChatEnabled(gameLobby.isChatEnabled());
    moveProcessor.setGameController(new GameController(gameState));
    moveProcessor.setDeckController(new DeckController(gameState.getDeck()));
    PlayerController playerController = new PlayerController();
    moveProcessor.setPlayerController(playerController);
    HashMap<Player, Integer> diceRolls = new HashMap<>();
    int queueSize = gameState.getActivePlayers().size();
    GameController gameController = moveProcessor.getGameController();
    for (int i = queueSize; i > 0; i--) {
      Player player = gameController.getGameState().getActivePlayers().poll();
      diceRolls.put(player, Dice.rollDice());
      gameState.getActivePlayers().add(player);
    }
    HashMap<String, Integer> diceRollsString = new HashMap<>();
    diceRolls.keySet().forEach(x -> diceRollsString.put(x.getUser(), diceRolls.get(x)));
    Player activePlayer = gameController.getGameState().getActivePlayers().peek();
    gameController.getGameState().setCurrentPlayer(activePlayer);
    moveProcessor.getPlayerController().setPlayer(activePlayer);
    moveProcessor.getDeckController().initDeck();
    Probabilities.init();
    sendFirstGamestate(gameLobby);
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    String message = Serializer.serialize(
        new StandardMessage<HashMap<String, Integer>>(diceRollsString));
    for (Channel ch : gameLobbyChannels.getChannelsByGameLobby(gameLobby)) {
      LOGGER.debug("Send new diceRolls to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(message));
    }
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    gameController.setNewPlayerOrder(gameController.getNewPlayerOrder(diceRolls));
    playerController.setPlayer(gameController.getCurrentPlayer());
    sendGamestate(gameLobbyChannels.getChannelsByGameLobby(gameLobby));
    moveProcessor.clearLastMoves();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    if (gameController.getCurrentPlayer() instanceof AiBot aiBot) {
      processBotMove(aiBot, gameLobbyChannels.getChannelsByGameLobby(gameLobby));
    }
  }

  /**
   * Processes the start of a tutorial in the given game lobby.
   *
   * @param myServerGameLobby the game lobby to start the game in.
   */
  private void processStartTutorial(GameLobby myServerGameLobby) {
    Tutorial tutorial = new Tutorial(myServerGameLobby.getUsers().get(0).getUsername());
    moveProcessor.setGameController(new GameController(tutorial.getTutorialState()));
    moveProcessor.setDeckController(new DeckController(tutorial.getTutorialState().getDeck()));
    PlayerController playerController = new PlayerController();
    playerController.setPlayer(tutorial.getTutorialState().getCurrentPlayer());
    moveProcessor.setPlayerController(playerController);
    Probabilities.init();
    GameConfiguration.setTutorial(tutorial);
    CountriesUiConfiguration.configureCountries(tutorial.getTutorialState().getCountries());
    GameConfiguration.setGameState(tutorial.getTutorialState());


  }

  /**
   * Processes the move of an AI bot in the game.
   *
   * @param aiBot        the AI bot to process the move for.
   * @param channelGroup the channel group for the game lobby.
   */
  private void processBotMove(AiBot aiBot, ChannelGroup channelGroup) {
    aiBot.setGameState(moveProcessor.getGameController().getGameState());
    Player player = (Player) aiBot;
    moveProcessor.getPlayerController().setPlayer(player);
    try {
      switch (player.getCurrentPhase()) {
        case CLAIM_PHASE -> {
          Thread.sleep(500);
          Reinforce reinforce = aiBot.claimCountry();
          moveProcessor.processReinforce(reinforce);
          sendGamestate(channelGroup);
          moveProcessor.clearLastMoves();
          Thread.sleep(500);
          moveProcessor.processEndPhase(new EndPhase(player.getCurrentPhase()));
          sendGamestate(channelGroup);
          moveProcessor.clearLastMoves();
          Thread.sleep(500);
          Player currentPlayer = moveProcessor.getGameController().getGameState()
              .getCurrentPlayer();
          if (!player.getUser().equals(currentPlayer.getUser())
              && currentPlayer instanceof AiBot aiBot1) {
            processBotMove(aiBot1, channelGroup);
          }
        }
        case REINFORCEMENT_PHASE -> {
          processBotHandIn(channelGroup);
          Thread.sleep(1500);
          processBotReinforcementPhase(aiBot, channelGroup, player);
          Thread.sleep(3000);
          processBotAttackPhase(aiBot, channelGroup, player);
          Thread.sleep(3000);
          processBotFortifyPhase(aiBot, channelGroup, player);
          Thread.sleep(500);
          Player currentPlayer = moveProcessor.getGameController().getGameState()
              .getCurrentPlayer();
          if (!player.getUser().equals(currentPlayer.getUser())
              && currentPlayer instanceof AiBot aiBot1) {
            processBotMove(aiBot1, channelGroup);
          }

        }


      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * Processes a reinforcement phase for the given bot.
   *
   * @param aiBot        the AI bot to process the move for.
   * @param channelGroup the channel group for the game lobby.
   * @param player       parameter aiBot cast to a player.
   */
  void processBotReinforcementPhase(AiBot aiBot, ChannelGroup channelGroup, Player player)
      throws InterruptedException {
    player.setDeployableTroops( // to ensure numbers are right
        moveProcessor.getPlayerController().getPlayer().getDeployableTroops());
    List<Reinforce> reinforces = aiBot.createAllReinforcements();
    reinforces.stream().filter(x -> x.getToAdd() > 0).forEach(x -> {
      moveProcessor.processReinforce(x);
      sendGamestate(channelGroup);
      moveProcessor.clearLastMoves();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    moveProcessor.processEndPhase(new EndPhase(player.getCurrentPhase()));
    sendGamestate(channelGroup);
    moveProcessor.clearLastMoves();
  }

  /**
   * Processes a bot hand in.
   *
   * @param channelGroup the channel group for the game lobby.
   */
  void processBotHandIn(ChannelGroup channelGroup) {
    HandController handController = moveProcessor.getPlayerController().getHandController();
    if (handController.holdsExchangeable()) {
      handController.selectExchangeableCards();
      HandIn hand = new HandIn(handController.getHand().getSelectedCards());
      moveProcessor.processHandIn(hand);
      sendGamestate(channelGroup);
      moveProcessor.clearLastMoves();
    }
  }

  /**
   * Processes a fortify phase for the given bot.
   *
   * @param aiBot        the AI bot to process the move for.
   * @param channelGroup the channel group for the game lobby.
   * @param player       parameter aiBot cast to a player.
   */
  void processBotFortifyPhase(AiBot aiBot, ChannelGroup channelGroup, Player player)
      throws InterruptedException {
    Fortify fortify = aiBot.createFortify();
    if (fortify != null && fortify.getTroopsToMove() > 0) {
      moveProcessor.processFortify(fortify);
      sendGamestate(channelGroup);
      moveProcessor.clearLastMoves();
    }
    Thread.sleep(500);
    moveProcessor.processEndPhase(new EndPhase(player.getCurrentPhase()));
    sendGamestate(channelGroup);
    moveProcessor.clearLastMoves();
  }

  /**
   * Processes an attack phase for the given bot.
   *
   * @param aiBot        the AI bot to process the move for.
   * @param channelGroup the channel group for the game lobby.
   * @param player       parameter aiBot cast to a player.
   */
  void processBotAttackPhase(AiBot aiBot, ChannelGroup channelGroup, Player player)
      throws InterruptedException {
    boolean quitEarly = false;
    do {
      CountryPair attack = aiBot.createAttack();
      if (attack == null) {
        moveProcessor.processEndPhase(new EndPhase(player.getCurrentPhase()));
        sendGamestate(channelGroup);
        moveProcessor.clearLastMoves();
        quitEarly = true;
      } else {
        Attack attack1;
        do {
          attack1 = attack.createAttack(aiBot.getAttackTroops(attack.getOutgoing()));
          moveProcessor.processAttack(attack1);
          sendGamestate(channelGroup);
          moveProcessor.clearLastMoves();
          Thread.sleep(3250);
        } while (!attack1.getHasConquered() && attack1.getAttackingCountry().getTroops() >= 2);
        aiBot.setGameState(moveProcessor.getGameController().getGameState());
        if (moveProcessor.getGameController().getGameState().isGameOver()) {
          break;
        }
        if (attack1.getHasConquered()) {
          Fortify fortify = attack.createFortify(attack1.getTroopNumber());
          moveProcessor.processFortify(fortify);
          sendGamestate(channelGroup);
          Thread.sleep(500);
          moveProcessor.clearLastMoves();
          Fortify fortify1 = aiBot.moveAfterAttack(attack);
          if (fortify1 != null && fortify1.getTroopsToMove() > 0) {
            moveProcessor.processFortify(fortify1);
            sendGamestate(channelGroup);
            moveProcessor.clearLastMoves();
          }
        }
      }
    } while (aiBot.attackAgain());
    if (!quitEarly) {
      moveProcessor.processEndPhase(new EndPhase(player.getCurrentPhase()));
      sendGamestate(channelGroup);
      moveProcessor.clearLastMoves();
    }
  }


}
