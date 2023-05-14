package com.unima.risk6.network.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;


public class GameServerInitializer extends ChannelInitializer<SocketChannel> {

  private static final String WEBSOCKET_PATH = "/game";
  private static ChannelGroup channels;


  private GameLobbyChannels gameLobbyChannels;

  public GameServerInitializer(ChannelGroup channels) {
    GameServerInitializer.channels = channels;
    this.gameLobbyChannels = new GameLobbyChannels();
  }

  @Override
  public void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(createSSLContext().newHandler(ch.alloc()));
    pipeline.addLast(new HttpServerCodec());
    pipeline.addLast(new HttpObjectAggregator(65536));
    pipeline.addLast(new WebSocketServerCompressionHandler());
    pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
    pipeline.addLast(new GameServerFrameHandler(channels, gameLobbyChannels));
  }

  private SslContext createSSLContext() throws Exception {
    KeyStore keystore = KeyStore.getInstance("JKS");
    keystore.load(
        GameServerInitializer.class.getResourceAsStream("/com/unima/risk6/certs/TestKeystore.jks"),
        "T0u8nUjT8TX9vTr2".toCharArray());

    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
        KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keystore, "T0u8nUjT8TX9vTr2".toCharArray());

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

    return SslContextBuilder.forServer(keyManagerFactory).build();
  }

}
