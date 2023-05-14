package com.unima.risk6.network.client;

import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.network.message.Message;
import com.unima.risk6.network.serialization.Serializer;
import io.netty.bootstrap.Bootstrap;
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
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContextBuilder;
import java.net.URI;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The GameClient class handles the client-side logic for a game, including sending messages,
 * leaving the game, and maintaining a connection to the server.
 *
 * @author jferch
 */
public final class GameClient implements Runnable {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameClient.class);

  private final String url;

  private volatile Channel ch;

  /**
   * Constructs a new GameClient with the specified URL.
   *
   * @param url The URL of the server to connect to.
   */
  public GameClient(String url) {
    this.url = System.getProperty("url", url);
  }

  /**
   * Sends a message to the server.
   *
   * @param message The message to send.
   */
  public void sendMessage(Message message) {
    String json = Serializer.serialize(message);
    WebSocketFrame frame = new TextWebSocketFrame(json);
    ch.writeAndFlush(frame);
    LOGGER.debug("Sent Message: " + json);
  }

  /**
   * Leaves the game and closes the connection to the server.
   */
  public void leaveGame() {
    ch.close();
    LobbyConfiguration.stopGameClient();
  }

  /**
   * Starts the game client.
   */
  public void run() {
    try {
      URI uri = new URI(url);
      final int port = 42069;

      EventLoopGroup group = new NioEventLoopGroup();
      try {
        final GameClientHandler handler = new GameClientHandler(
            WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true,
                new DefaultHttpHeaders()));

        Bootstrap b = new Bootstrap();
        b.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                KeyStore truststore = KeyStore.getInstance("JKS");
                truststore.load(
                    GameClient.class.getResourceAsStream("/com/unima/risk6/certs/Keystore.jks"),
                    "T0u8nUjT8TX9vTr2".toCharArray());
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(truststore);

                p.addLast(SslContextBuilder.forClient().trustManager(trustManagerFactory).build()
                    .newHandler(ch.alloc()));
                p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192),
                    WebSocketClientCompressionHandler.INSTANCE, handler);
              }
            });

        ch = b.connect(uri.getHost(), port).sync().channel();
        handler.handshakeFuture().sync();

        while (true) {
          Thread.sleep(10000);
        }
      } finally {
        group.shutdownGracefully();
      }
    } catch (Exception e) {
      //TODO Logger
      System.out.println(e);
    }
  }

  /**
   * Returns the channel associated with this client.
   *
   * @return the channel.
   */
  public Channel getCh() {
    return ch;
  }
}