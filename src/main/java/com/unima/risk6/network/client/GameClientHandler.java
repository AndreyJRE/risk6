package com.unima.risk6.network.client;

import com.google.gson.*;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.network.serialization.Deserializer;
import com.unima.risk6.network.serialization.Serializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameClientHandler extends SimpleChannelInboundHandler<Object> {

    private final static Logger LOGGER = LoggerFactory.getLogger(GameClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public GameClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                System.out.println("Connected Successfully!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                System.out.println("Failed to connect");
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse response) {
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame textFrame) {
            JsonObject json = null;
            try {
                json = JsonParser.parseString(textFrame.text()).getAsJsonObject();
                //TODO Error Handling
            } catch (Exception e) {
                LOGGER.debug("Not a JSON: " + textFrame.text() + "\n Exception: " + e);
                System.out.println("Not a JSON: " + textFrame.text());

            }
            if (json != null) {
                System.out.println(json.get("contentType").getAsString());
                switch (json.get("contentType").getAsString()) {
                    case "GAMESTATE":
                        GameState g = (GameState) Deserializer.deserialize(textFrame.text()).getContent();
                        GameConfiguration.setGameState(g);
                        break;
                    default:
                        LOGGER.debug("The Message received wasnt a gamestate");
                        break;
                }
            }

        } else if (frame instanceof PongWebSocketFrame) {
            System.out.println("Received pong from Server");
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("Received closing from Server");
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}