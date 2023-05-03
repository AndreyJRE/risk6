package com.unima.risk6.network;


import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.client.GameClient;
import com.unima.risk6.network.client.GameClientHandler;
import com.unima.risk6.network.message.StandardMessage;
import com.unima.risk6.network.serialization.Deserializer;
import com.unima.risk6.network.serialization.Serializer;
import com.unima.risk6.network.server.GameServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for gamestate sending
 *
 * @author jferch
 */

public class GamestateSendTest {
    private static GameController gamecontroller;
    private static GameState gamestate;
    private static GameClient gameClient;
    private static GameServer gameServer;
    @BeforeAll
    static void setUp() {

        try {
            /*ArrayList<String> users =  new ArrayList<String>(Arrays.asList("Andrey","Max","Fung"));
            ArrayList<AiBot> bots = new ArrayList<AiBot>(Arrays.asList(new EasyBot(),new EasyBot()));
            gamestate = GameConfiguration.configureGame(users, bots);
            gamecontroller = new GameController(gamestate);
             */
            gameClient = new GameClient("ws://localhost:8080/game");
            gameServer = new GameServer();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }


    @Test
    void testSerializationWithStatus() {
        try {
            //System.out.println(gamestate);
            //System.out.println(Serializer.serialize(new StandardMessage(gamestate, 200)));
            Thread server = new Thread(gameServer);
            server.start();
            Thread.sleep(1000);
            Thread client = new Thread(gameClient);
            client.start();
            Thread.sleep(2000);
            gameClient.sendMessage("Penis");
            Thread.sleep(10000);
            //assertEquals("{\"statusCode\":200,\"content\":\"tetest\"}", Serializer.serialize(new StandardMessage("tetest", 200)));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
