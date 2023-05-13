package com.unima.risk6.network.server;

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
import java.net.InetSocketAddress;

public final class GameServer implements Runnable {

  final int port = 42069;
  final String hostIp;
  static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  public GameServer() {
    this.hostIp = "0.0.0.0";
  }

  public GameServer(String host_ip) {
    this.hostIp = host_ip;
  }

  public void run() {
    System.out.println("Starting Server");
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new GameServerInitializer(channels))
          .localAddress(new InetSocketAddress(hostIp, port));

      Channel ch = b.bind(port).sync().channel();
      ch.closeFuture().sync();
    } catch (Exception e) {
      //TODO Logger
      System.out.println(e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  public String getHostIp() {
    return HOST_IP;
  }
}