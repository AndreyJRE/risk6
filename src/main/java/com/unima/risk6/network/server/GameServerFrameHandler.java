package com.unima.risk6.network.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class GameServerFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

  private static ChannelGroup channels;

  GameServerFrameHandler(ChannelGroup channels) {
    GameServerFrameHandler.channels = channels;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

    if (frame instanceof TextWebSocketFrame) {
      String request = ((TextWebSocketFrame) frame).text();
      for (Channel ch : channels) {
        ch.writeAndFlush(
            new TextWebSocketFrame(/*ctx.channel().attr("name") + " has written: " + */request));
      }
      //System.out.println(channels.size());
      //ctx.channel().writeAndFlush(new TextWebSocketFrame(request));
    } else {
      String message = "unsupported frame type: " + frame.getClass().getName();
      throw new UnsupportedOperationException(message);
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    channels.add(ctx.channel());
    //ctx.channel().attr("name").set("Test");
  }

}
