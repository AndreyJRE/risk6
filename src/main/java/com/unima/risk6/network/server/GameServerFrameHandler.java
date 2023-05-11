package com.unima.risk6.network.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
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
  private static final BiMap<GameLobby, ChannelGroup> gameChannels = HashBiMap.create();
  private static final BiMap<UserDto, Channel> users = HashBiMap.create();

  protected static ChannelGroup channels;
  private GameLobbyChannels gameLobbyChannels;
  private MoveProcessor moveProcessor;

  GameServerFrameHandler(ChannelGroup channels, MoveProcessor moveProcessor,
      GameLobbyChannels gameLobbyChannels) {
    GameServerFrameHandler.channels = channels;
    this.moveProcessor = moveProcessor;
    this.gameLobbyChannels = gameLobbyChannels;
  }

  private static GameLobby getServerGameLobby(GameLobby gameLobby, ServerLobby serverLobby) {
    GameLobby gameLobbyFromServer = serverLobby.getGameLobbies().stream()
        .filter(x -> x.getLobbyName().equals(gameLobby.getLobbyName())).findFirst().get();
    return gameLobbyFromServer;
  }

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

  private void processBotReinforcementPhase(AiBot aiBot, ChannelGroup channelGroup, Player player)
      throws InterruptedException {
    processBotHandIn(channelGroup);
    Thread.sleep(1000);
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

  private void processBotHandIn(ChannelGroup channelGroup) {
    HandController handController = moveProcessor.getPlayerController().getHandController();
    if (handController.holdsExchangeable()) {
      handController.selectExchangeableCards();
      HandIn hand = new HandIn(handController.getHand().getSelectedCards());
      moveProcessor.processHandIn(hand);
      sendGamestate(channelGroup);
      moveProcessor.clearLastMoves();
    }
  }

  private void processBotFortifyPhase(AiBot aiBot, ChannelGroup channelGroup, Player player)
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

  private void processBotAttackPhase(AiBot aiBot, ChannelGroup channelGroup, Player player)
      throws InterruptedException {
    do {
      CountryPair attack = aiBot.createAttack();
      if (attack == null) {
        moveProcessor.processEndPhase(new EndPhase(player.getCurrentPhase()));
        sendGamestate(channelGroup);
        moveProcessor.clearLastMoves();
      } else {
        Attack attack1;
        do {
          attack1 = attack.createAttack(aiBot.getAttackTroops(attack.getOutgoing()));
          moveProcessor.processAttack(attack1);
          sendGamestate(channelGroup);
          moveProcessor.clearLastMoves();
          Thread.sleep(3500);
        } while (!attack1.getHasConquered()
            && attack1.getAttackingCountry().getTroops() >= 2);
        aiBot.setGameState(moveProcessor.getGameController().getGameState());

        if (moveProcessor.getGameController().getGameState().isGameOver()) {
          sendGameOver(channelGroup);
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
    moveProcessor.processEndPhase(new EndPhase(player.getCurrentPhase()));
    sendGamestate(channelGroup);
    moveProcessor.clearLastMoves();
  }

  private void sendGameOver(ChannelGroup channelGroup) {
    //TODO
  }

  private void sendUpdatedServerLobby(ServerLobby serverLobby) {
    String serialized = Serializer.serialize(
        new ConnectionMessage<>(ConnectionActions.ACCEPT_UPDATE_SERVER_LOBBY, serverLobby));
    for (Channel ch : channels) {
      LOGGER.debug("Send updated server lobby to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(serialized));
    }
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
            "Server Received Message with ContentType: " + json.get("contentType")
                .getAsString());
        /*old: ChannelGroup channelGroup = gameChannels.values().stream()
            .filter(x -> x.contains(ctx.channel())).findFirst().orElse(channels);*/
        ChannelGroup channelGroup = gameLobbyChannels.getChannelGroupByChannel(ctx.channel());
        switch (json.get("contentType").getAsString()) {
          case "GAME_STATE" -> {
            LOGGER.error("The server should not receive a gamestate");
          }
          case "ATTACK" -> {
            LOGGER.debug("The server received a attack object");
            Attack attack = (Attack) Deserializer.deserialize(request,
                moveProcessor.getGameController().getGameState()).getContent();
            moveProcessor.processAttack(attack);
            sendGamestate(channelGroup);
            moveProcessor.clearLastMoves();
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
            EndPhase endPhase = (EndPhase) Deserializer.deserialize(request)
                .getContent();
            moveProcessor.processEndPhase(endPhase);
            Player currentPlayerAfter = moveProcessor.getGameController()
                .getCurrentPlayer();
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
                  System.out.println(connectionMessage.getContent().getClass());
                  UserDto userDto = (UserDto) connectionMessage.getContent();

                  //old: if (users.containsKey(userDto)) {
                  if (gameLobbyChannels.containsUser(userDto)) {
                    LOGGER.error("User already in the Lobby");
                  } else {
                    //old: users.put(userDto, ctx.channel());
                    gameLobbyChannels.putUsers(userDto, ctx.channel());
                    NetworkConfiguration.getServerLobby().getUsers()
                        .add(userDto);
                  }
                  sendServerLobby(NetworkConfiguration.getServerLobby());
                }
                case JOIN_GAME_LOBBY -> {
                  LOGGER.debug(
                      "At JOIN_GAME_LOBBY " + connectionMessage.getContent()
                          .getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  GameLobby gameLobbyFromServer = getServerGameLobby(gameLobby,
                      NetworkConfiguration.getServerLobby());

                  if (gameLobbyFromServer.getBots().size() + gameLobbyFromServer.getUsers().size()
                      < gameLobbyFromServer.getMaxPlayers()) {

                    //Add users channel to the ChannelGroup from the gamelobby

                    /*old
                    channels.remove(ctx.channel());
                    gameLobbyFromServer.getUsers()
                        .add(users.inverse().get(ctx.channel()));
                    gameChannels.get(gameLobbyFromServer).add(ctx.channel());

                     */
                    gameLobbyChannels.addUserToGameLobby(gameLobbyFromServer, ctx.channel());

                    sendGameLobby(gameLobbyFromServer);
                    sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());
                  } else {
                    sendDropMessage(ctx.channel(), ConnectionActions.DROP_USER_GAME_LOBBY,
                        "The GameLobby is full");
                  }
                }
                case JOIN_BOT_GAME_LOBBY -> {
                  LOGGER.debug(
                      "At JOIN_BOT_GAME_LOBBY " + connectionMessage.getContent()
                          .getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  GameLobby gameLobbyFromServer = getServerGameLobby(gameLobby,
                      NetworkConfiguration.getServerLobby());
                  String bot = gameLobby.getBots().stream()
                      .filter(x -> !gameLobbyFromServer.getBots().contains(x))
                      .findFirst().get();
                  gameLobbyFromServer.getBots().add(bot);
                  ServerLobby serverLobby = NetworkConfiguration.getServerLobby();
                  sendGameLobby(gameLobbyFromServer);
                  sendUpdatedServerLobby(serverLobby);

                }
                case START_GAME -> {
                  //TODO MOVE_CONTROLLER
                  LOGGER.debug("At START_GAME" + connectionMessage.getContent()
                      .getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  GameLobby myServerGameLobby = getServerGameLobby(gameLobby,
                      NetworkConfiguration.getServerLobby());
                  //Send max players to current players
                  int newMaxPlayers =
                      myServerGameLobby.getUsers().size() + myServerGameLobby.getBots().size();
                  myServerGameLobby.setMaxPlayers(newMaxPlayers);
                  sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());

                  processStartGame(myServerGameLobby);

                }
                case LEAVE_SERVER_LOBBY -> {
                  LOGGER.debug(
                      "At LEAVE_SERVER_LOBBY");

                  LOGGER.debug(
                      "Sizes of ChannelGroup " + channels.size() + " ServerLobby "
                          + NetworkConfiguration.getServerLobby().getUsers()
                          .size() + " UsersList "
                          + gameLobbyChannels.getUsers().size());
                  //remove from server lobby channelGroup,
                  channels.remove(ctx.channel());
                  //remove from LobbyObject
                  /*old
                  NetworkConfiguration.getServerLobby().getUsers()
                      .remove(users.inverse().get(ctx.channel()));

                   */
                  gameLobbyChannels.removeUserFromServerLobby(ctx.channel());
                  //and users list
                  //Should not be neccesarry: users.inverse().remove(ctx.channel());
                  LOGGER.debug(
                      "Sizes of ChannelGroup " + channels.size() + " ServerLobby "
                          + NetworkConfiguration.getServerLobby().getUsers()
                          .size() + " UsersList "
                          + gameLobbyChannels.getUsers().size());
                  sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());


                }
                case LEAVE_GAME_LOBBY -> {
                  LOGGER.debug("At LEAVE_GAME_LOBBY");
                  //LOGGER.debug("Sizes of ChannelGroup " + channels.size() + " ServerLobby " + NetworkConfiguration.getServerLobby().getUsers().size() + " UsersList " + users.size());
                  //TODO LEAVE gameChannel, remove from gamelobby, add to server lobby join channels delete gameLobby if empty;
                  //leave gameChannel
                  /* old:
                  ChannelGroup currentGame = gameChannels.values().stream()
                      .filter(x -> x.contains(ctx.channel())).findFirst().get();
                  currentGame.remove(ctx.channel());

                  //remove from game lobby
                  gameChannels.inverse().get(currentGame).getUsers()
                      .remove(users.inverse().get(ctx.channel()));
                  //join serverLobby channel
                  channels.add(ctx.channel());


                  //LOGGER.debug("Sizes of ChannelGroup " + channels.size() + " ServerLobby " + NetworkConfiguration.getServerLobby().getUsers().size() + " UsersList " + users.size());



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
                    sendGameLobby(gameChannels.inverse().get(currentGame));
                  }
                  sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());

                   */
                  gameLobbyChannels.removeUserFromGameLobby(ctx.channel(), this);
                  sendUpdatedServerLobby(NetworkConfiguration.getServerLobby());


                }
                case LEAVE_GAME -> {
                  LOGGER.debug("At LEAVE_GAME" + connectionMessage.getContent()
                      .getClass());

                }
                case CREATE_GAME_LOBBY -> {
                  LOGGER.debug(
                      "At CREATE_GAME_LOBBY" + connectionMessage.getContent()
                          .getClass());
                  GameLobby gameLobby = (GameLobby) connectionMessage.getContent();
                  /* old
                  NetworkConfiguration.getServerLobby().getGameLobbies()
                      .add(gameLobby);
                  channels.remove(ctx.channel());
                  gameChannels.put(gameLobby,
                      new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
                  gameChannels.get(gameLobby).add(ctx.channel());
                  gameChannels.keySet().forEach(System.out::println);

                   */
                  gameLobbyChannels.createGameLobby(gameLobby, ctx.channel());

                  sendCreatedGameLobby(NetworkConfiguration.getServerLobby(),
                      gameLobby);

                }
                default -> LOGGER.error("Server received a faulty connection message");
              }
            } catch (NullPointerException ignored) {
              //TODO
            }
          }
          default -> {
            LOGGER.debug(
                "The Message received wasnt a valid Message\nMessage: " + json);

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

  private void sendGamestate(ChannelGroup channelGroup) {
    System.out.println("Serilize" + " Test");
    System.out.println(moveProcessor.getDeckController().getDeck().getDeckCards());
    String message = Serializer.serialize(
        new StandardMessage(moveProcessor.getGameController().getGameState()));
    LOGGER.debug(message);
    for (Channel ch : channelGroup) {
      LOGGER.debug("Send new gamestate to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(message));
    }
  }

  private void sendCreatedGameLobby(ServerLobby serverLobby, GameLobby gameLobby) {
    String serialized = Serializer.serialize(
        new ConnectionMessage<>(ConnectionActions.ACCEPT_UPDATE_SERVER_LOBBY, serverLobby));
    for (Channel ch : channels) {
      LOGGER.debug("Send new server lobby to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(serialized));
    }
    String serialized1 = Serializer.serialize(
        new ConnectionMessage<>(ConnectionActions.ACCEPT_CREATE_LOBBY, gameLobby));
    /*OLD
    for (Channel channel : gameChannels.get(gameLobby)) {
      LOGGER.debug("Send game lobby to : " + channel.id());
      channel.writeAndFlush(new TextWebSocketFrame(serialized1));

    }

     */
    for (Channel channel : gameLobbyChannels.getChannelsByGameLobby(gameLobby)) {
      LOGGER.debug("Send game lobby to : " + channel.id());
      channel.writeAndFlush(new TextWebSocketFrame(serialized1));

    }
  }

  private void sendServerLobby(ServerLobby serverLobby) {
    for (Channel ch : channels) {
      LOGGER.debug("Send new server lobby to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(Serializer.serialize(
          new ConnectionMessage<ServerLobby>(ConnectionActions.ACCEPT_JOIN_SERVER_LOBBY,
              serverLobby))));
    }
  }

  private void sendFirstGamestate(GameLobby gameLobby) {
    String message = Serializer.serialize(
        new ConnectionMessage<>(ConnectionActions.ACCEPT_START_GAME,
            moveProcessor.getGameController().getGameState()));
    LOGGER.debug(message);
    /*Old
    for (Channel ch : gameChannels.get(gameLobby)) {
      LOGGER.debug("Send new gamestate to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(message));
    }
     */
    for (Channel ch : gameLobbyChannels.getChannelsByGameLobby(gameLobby)) {
      LOGGER.debug("Send new gamestate to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(message));
    }
  }

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
    System.out.println(gameState.getActivePlayers());
    gameState.getActivePlayers().stream().filter(x -> x instanceof AiBot)
        .forEach(x -> ((AiBot) x).setGameState(gameState));
    gameState.setChatEnabled(gameLobby.isChatEnabled());
    gameState.setPhaseTime(gameLobby.getPhaseTime());
    moveProcessor.setGameController(new GameController(gameState));
    moveProcessor.setDeckController(new DeckController(gameState.getDeck()));
    PlayerController playerController = new PlayerController();
    moveProcessor.setPlayerController(playerController);
    HashMap<Player, Integer> diceRolls = new HashMap<>();
    for (int i = gameState.getActivePlayers().size(); i > 0; i--) {
      diceRolls.put(
          moveProcessor.getGameController().getGameState().getActivePlayers().poll(), i);
    }
    moveProcessor.getGameController()
        .setNewPlayerOrder(moveProcessor.getGameController().getNewPlayerOrder(diceRolls));
    Player activePlayer = moveProcessor.getGameController().getGameState().getActivePlayers()
        .peek();
    moveProcessor.getGameController().getGameState().setCurrentPlayer(activePlayer);
    moveProcessor.getPlayerController().setPlayer(activePlayer);
    moveProcessor.getDeckController().initDeck();
    Probabilities.init();
    sendFirstGamestate(gameLobby);
    moveProcessor.clearLastMoves();
  }

  protected void sendGameLobby(GameLobby gameLobby) {
    String serializedGameLobby = Serializer.serialize(
        new ConnectionMessage<>(ConnectionActions.ACCEPT_JOIN_GAME_LOBBY, gameLobby));
    //System.out.println(gameChannels.get(gameLobby));
    /*old
    for (Channel ch : gameChannels.get(gameLobby)) {
      LOGGER.debug("Send a game lobby to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(serializedGameLobby));
    }

     */
    for (Channel ch : gameLobbyChannels.getChannelsByGameLobby(gameLobby)) {
      LOGGER.debug("Send a game lobby to: " + ch.id());
      ch.writeAndFlush(new TextWebSocketFrame(serializedGameLobby));
    }
  }

  public void sendDropMessage(Channel channel, ConnectionActions connectionActions, String string) {
    channel.writeAndFlush(new TextWebSocketFrame(
        Serializer.serialize(new ConnectionMessage<String>(connectionActions, string))));
  }

  public void sendChatMessage(Channel channel, String request) {
    ChatMessage chatMessage = Deserializer.deserializeChatMessage(request);
    //old: chatMessage.setContent(users.inverse().get(channel).getUsername() + ": " + chatMessage.getContent());
    chatMessage.setContent(gameLobbyChannels.getUserByChannel(channel).getUsername() + ": "
        + chatMessage.getContent());
    /*old
    gameChannels.values()
        .stream().filter(x -> x.contains(channel)).findFirst().orElse(channels)
        //.stream().filter(x -> !x.equals(channel))

     */
    gameLobbyChannels.getChannelGroupByChannel(channel)
        .forEach(ch ->
        {
          String message = Serializer.serialize(chatMessage);
          LOGGER.debug("Send chatmessage: " + message + " to channel: " + channel.id());
          ch.writeAndFlush(new TextWebSocketFrame(message));
        });


  }

  //TODO Handle verbindungsabbruch with channelInactive
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    channels.add(ctx.channel());
    //TODO Manage the different games and the server lobby, multiple channelGroups could be the solution
    // ctx.channel().attr(AttributeKey.newInstance("name")).set("Test");
  }


}
