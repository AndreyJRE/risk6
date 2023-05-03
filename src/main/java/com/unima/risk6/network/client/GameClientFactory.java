package com.unima.risk6.network.client;

import java.util.HashMap;

public class GameClientFactory {
    public static HashMap<GameClient, Thread> threadMap = new HashMap<>();

    public static GameClient createGameClient(String url) {
        GameClient g = new GameClient(url);
        Thread t = new Thread(g);
        threadMap.put(g, t);
        return g;
    }

    //Important: you must wait until the client is started before sending messages
    public static GameClient createAndStartGameClient(String url) {
        GameClient g = new GameClient(url);
        Thread t = new Thread(g);
        t.start();
        threadMap.put(g, t);
        return g;
    }

    public static void startGameClient(GameClient g) {
        Thread t = threadMap.get(g);
        t.start();
    }
}
