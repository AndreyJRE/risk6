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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the game server that handles connections from clients. The server implements Runnable,
 * so it can be started in a separate thread.
 *
 * @author jferch
 */
public final class GameServer implements Runnable {

  final int port = 42069;
  final String hostIp;
  static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  private static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);


  /**
   * Default constructor that initializes the server with the IP address "0.0.0.0". Use it, if you
   * want to allow all ip addresses to connect to your server.
   */
  public GameServer() {
    this.hostIp = "0.0.0.0";
  }

  /**
   * Constructor that initializes the server with a provided IP address. Use 127.0.0.1 for single
   * player modes.
   *
   * @param hostIp the IP address of the host server.
   */
  public GameServer(String hostIp) {
    this.hostIp = hostIp;
  }

  public String getHostIp() {
    return hostIp;
  }

  /**
   * The main loop of the server, which initializes and starts the server. The server will keep
   * running until an exception occurs or the server is shut down.
   */
  public void run() {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new GameServerInitializer(channels))
          .localAddress(new InetSocketAddress(hostIp, port));

      Channel ch = b.bind(port).sync().channel();
      ch.closeFuture().sync();
    } catch (Exception e) {
      LOGGER.debug(e.toString());
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }


  }

}