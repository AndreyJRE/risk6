package com.unima.risk6.network.server;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Deck;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;

public final class GameServer implements Runnable{

    int PORT = 8080;
    ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //TODO
    MoveProcessor moveProcessor = new MoveProcessor(new PlayerController(),new GameController(GameConfiguration.configureGame(new ArrayList<>(), new ArrayList<>())),new DeckController(new Deck()));



    public void run() {
        System.out.println("Starting Server");
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new GameServerInitializer(channels,moveProcessor,Integer.toString(PORT)));

            Channel ch = b.bind(PORT).sync().channel();
            ch.closeFuture().sync();
        } catch(Exception e){
            //TODO Logger
            System.out.println(e);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}