package com.unima.risk6.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public final class GameClient implements Runnable {

  private final String URL;// = System.getProperty("url", "ws://127.0.0.1:8080/game");

  private String msg;
  private boolean sended = true;

  public GameClient(String url) {
    URL = System.getProperty("url", url);
  }

  public void sendMessage(String msg){
    this.msg = msg;
    sended = false;
  }

  public void run() {
    try {
      URI uri = new URI(URL);
      final int port = 8080;

      EventLoopGroup group = new NioEventLoopGroup();
      try {
        final GameClientHandler handler = new GameClientHandler(
            WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true,
                new DefaultHttpHeaders()));

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192),
                    WebSocketClientCompressionHandler.INSTANCE, handler);
              }
            });

        Channel ch = b.connect(uri.getHost(), port).sync().channel();
        handler.handshakeFuture().sync();

        //BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
          //String msg = console.readLine();
          if (msg == null && !sended) {
            break;
          } else if ("bye".equalsIgnoreCase(msg) && !sended) {
            ch.writeAndFlush(new CloseWebSocketFrame());
            ch.closeFuture().sync();
            break;
          } else if ("ping".equalsIgnoreCase(msg) && !sended) {
            WebSocketFrame frame = new PingWebSocketFrame(
                Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}));
            ch.writeAndFlush(frame);
          } else if (!sended){
            sended = true;
            System.out.println("Sending Message: " + msg);
            WebSocketFrame frame = new TextWebSocketFrame(msg);
            ch.writeAndFlush(frame);
          }
        }
      } finally {
        group.shutdownGracefully();
      }
    } catch (Exception e) {
      //TODO Logger
        System.out.println(e);
    }
  }
}
