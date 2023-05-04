package com.unima.risk6.network.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;


public class GameServerInitializer extends ChannelInitializer<SocketChannel> {

  private static final String WEBSOCKET_PATH = "/game";
  private static ChannelGroup channels;

  private MoveProcessor moveProcessor;

  public GameServerInitializer(ChannelGroup channels, MoveProcessor moveProcessor) {
    GameServerInitializer.channels = channels;
    this.moveProcessor = moveProcessor;
  }

  @Override
  public void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new HttpServerCodec());
    pipeline.addLast(new HttpObjectAggregator(65536));
    pipeline.addLast(new WebSocketServerCompressionHandler());
    pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
    pipeline.addLast(new GameServerFrameHandler(channels, moveProcessor));
  }

}
