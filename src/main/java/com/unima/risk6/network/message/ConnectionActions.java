package com.unima.risk6.network.message;

public enum ConnectionActions {
  //Server Side
  JOIN_SERVER_LOBBY, JOIN_GAME_LOBBY, LEAVE_SERVER_LOBBY, LEAVE_GAME_LOBBY, LEAVE_GAME, CREATE_GAME_LOBBY, START_GAME,
  //Client Side
  ACCEPT_SERVER_LOBBY, DROP_USER_LOBBY, ACCEPT_USER_GAME, DROP_USER_GAME, ACCEPT_JOIN_LOBBY,
  ACCEPT_CREATE_LOBBY, ACCEPT_JOIN_GAME, ACCEPT_START_GAME
}
