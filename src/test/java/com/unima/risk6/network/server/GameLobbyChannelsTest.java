package com.unima.risk6.network.server;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.client.GameClient;
import com.unima.risk6.network.configurations.GameClientFactory;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameLobbyChannelsTest {

  GameClient gameClient;

  @BeforeAll
  static void setup() {
    NetworkConfiguration.startGameServer();
  }

  public Channel createChannel(String id) {
    return new Channel() {
      String channelId;
      Channel channel = this;

      @Override
      public ChannelId id() {
        return new ChannelId() {
          @Override
          public String asShortText() {
            return channelId;
          }

          @Override
          public String asLongText() {
            return channelId;
          }

          @Override
          public int compareTo(ChannelId channelId) {
            return 0;
          }
        };
      }

      @Override
      public EventLoop eventLoop() {
        return null;
      }

      @Override
      public Channel parent() {
        return null;
      }

      @Override
      public ChannelConfig config() {
        return null;
      }

      @Override
      public boolean isOpen() {
        return false;
      }

      @Override
      public boolean isRegistered() {
        return false;
      }

      @Override
      public boolean isActive() {
        return false;
      }

      @Override
      public ChannelMetadata metadata() {
        return null;
      }

      @Override
      public SocketAddress localAddress() {
        return null;
      }

      @Override
      public SocketAddress remoteAddress() {
        return null;
      }

      @Override
      public ChannelFuture closeFuture() {
        return new ChannelFuture() {
          @Override
          public Channel channel() {
            return channel;
          }

          @Override
          public ChannelFuture addListener(
              GenericFutureListener<? extends Future<? super Void>> listener) {
            return null;
          }

          @Override
          public ChannelFuture addListeners(
              GenericFutureListener<? extends Future<? super Void>>... listeners) {
            return null;
          }

          @Override
          public ChannelFuture removeListener(
              GenericFutureListener<? extends Future<? super Void>> listener) {
            return null;
          }

          @Override
          public ChannelFuture removeListeners(
              GenericFutureListener<? extends Future<? super Void>>... listeners) {
            return null;
          }

          @Override
          public ChannelFuture sync() throws InterruptedException {
            return null;
          }

          @Override
          public ChannelFuture syncUninterruptibly() {
            return null;
          }

          @Override
          public ChannelFuture await() throws InterruptedException {
            return null;
          }

          @Override
          public ChannelFuture awaitUninterruptibly() {
            return null;
          }

          @Override
          public boolean isVoid() {
            return false;
          }

          @Override
          public boolean isSuccess() {
            return false;
          }

          @Override
          public boolean isCancellable() {
            return false;
          }

          @Override
          public Throwable cause() {
            return null;
          }

          @Override
          public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
          }

          @Override
          public boolean await(long timeoutMillis) throws InterruptedException {
            return false;
          }

          @Override
          public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
            return false;
          }

          @Override
          public boolean awaitUninterruptibly(long timeoutMillis) {
            return false;
          }

          @Override
          public Void getNow() {
            return null;
          }

          @Override
          public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
          }

          @Override
          public boolean isCancelled() {
            return false;
          }

          @Override
          public boolean isDone() {
            return false;
          }

          @Override
          public Void get() throws InterruptedException, ExecutionException {
            return null;
          }

          @Override
          public Void get(long l, TimeUnit timeUnit)
              throws InterruptedException, ExecutionException, TimeoutException {
            return null;
          }
        };
      }

      @Override
      public boolean isWritable() {
        return false;
      }

      @Override
      public long bytesBeforeUnwritable() {
        return 0;
      }

      @Override
      public long bytesBeforeWritable() {
        return 0;
      }

      @Override
      public Unsafe unsafe() {
        return null;
      }

      @Override
      public ChannelPipeline pipeline() {
        return null;
      }

      @Override
      public ByteBufAllocator alloc() {
        return null;
      }

      @Override
      public Channel read() {
        return null;
      }

      @Override
      public Channel flush() {
        return null;
      }

      @Override
      public ChannelFuture bind(SocketAddress localAddress) {
        return null;
      }

      @Override
      public ChannelFuture connect(SocketAddress remoteAddress) {
        return null;
      }

      @Override
      public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return null;
      }

      @Override
      public ChannelFuture disconnect() {
        return null;
      }

      @Override
      public ChannelFuture close() {
        return null;
      }

      @Override
      public ChannelFuture deregister() {
        return null;
      }

      @Override
      public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return null;
      }

      @Override
      public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return null;
      }

      @Override
      public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress,
          ChannelPromise promise) {
        return null;
      }

      @Override
      public ChannelFuture disconnect(ChannelPromise promise) {
        return null;
      }

      @Override
      public ChannelFuture close(ChannelPromise promise) {
        return null;
      }

      @Override
      public ChannelFuture deregister(ChannelPromise promise) {
        return null;
      }

      @Override
      public ChannelFuture write(Object msg) {
        return null;
      }

      @Override
      public ChannelFuture write(Object msg, ChannelPromise promise) {
        return null;
      }

      @Override
      public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return null;
      }

      @Override
      public ChannelFuture writeAndFlush(Object msg) {
        return null;
      }

      @Override
      public ChannelPromise newPromise() {
        return null;
      }

      @Override
      public ChannelProgressivePromise newProgressivePromise() {
        return null;
      }

      @Override
      public ChannelFuture newSucceededFuture() {
        return null;
      }

      @Override
      public ChannelFuture newFailedFuture(Throwable cause) {
        return null;
      }

      @Override
      public ChannelPromise voidPromise() {
        return null;
      }

      @Override
      public <T> Attribute<T> attr(AttributeKey<T> key) {
        return null;
      }

      @Override
      public <T> boolean hasAttr(AttributeKey<T> key) {
        return false;
      }

      @Override
      public int compareTo(Channel channel) {
        return 0;
      }
    };
  }

  @BeforeEach
  void resetAndInitialize() {
    try {
      NetworkConfiguration.getServerLobby().getGameLobbies()
          .removeAll(NetworkConfiguration.getServerLobby().getGameLobbies());
      NetworkConfiguration.getServerLobby().getUsers()
          .removeAll(NetworkConfiguration.getServerLobby().getUsers());
      NetworkConfiguration.startGameServer();
    } catch (Exception e) {
      fail("Should not have thrown any exception");
    }

    gameClient = GameClientFactory.createGameClient("127.0.0.1");
    GameClientFactory.startGameClient(gameClient);
  }

  @Test
  void joinServerLobby() {
    Channel channel = createChannel("123");
    GameLobbyChannels gameLobbyChannels = new GameLobbyChannels();
    UserDto userDto = new UserDto("Name", 2, 10.5, 12, 1, 400);
    gameLobbyChannels.putUsers(userDto, channel);
    assertTrue(gameLobbyChannels.containsUser(userDto) && gameLobbyChannels.getUsers().size() == 1);
  }

  @Test
  void joinAndLeaveServerLobby() {
    Channel channel = createChannel("123");
    GameLobbyChannels gameLobbyChannels = new GameLobbyChannels();
    UserDto userDto = new UserDto("Name", 2, 10.5, 12, 1, 400);
    gameLobbyChannels.putUsers(userDto, channel);
    LobbyConfiguration.setServerLobby(new ServerLobby("lele", "12123"));
    gameLobbyChannels.handleExit(channel,
        new GameServerFrameHandler(new DefaultChannelGroup(new EventExecutor() {
          @Override
          public EventExecutor next() {
            return null;
          }

          @Override
          public EventExecutorGroup parent() {
            return null;
          }

          @Override
          public boolean inEventLoop() {
            return false;
          }

          @Override
          public boolean inEventLoop(Thread thread) {
            return false;
          }

          @Override
          public <V> Promise<V> newPromise() {
            return null;
          }

          @Override
          public <V> ProgressivePromise<V> newProgressivePromise() {
            return null;
          }

          @Override
          public <V> Future<V> newSucceededFuture(V result) {
            return null;
          }

          @Override
          public <V> Future<V> newFailedFuture(Throwable cause) {
            return null;
          }

          @Override
          public boolean isShuttingDown() {
            return false;
          }

          @Override
          public Future<?> shutdownGracefully() {
            return null;
          }

          @Override
          public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
            return null;
          }

          @Override
          public Future<?> terminationFuture() {
            return null;
          }

          @Override
          public void shutdown() {

          }

          @Override
          public List<Runnable> shutdownNow() {
            return null;
          }

          @Override
          public Iterator<EventExecutor> iterator() {
            return null;
          }

          @Override
          public Future<?> submit(Runnable task) {
            return null;
          }

          @Override
          public <T> Future<T> submit(Runnable task, T result) {
            return null;
          }

          @Override
          public <T> Future<T> submit(Callable<T> task) {
            return null;
          }

          @Override
          public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return null;
          }

          @Override
          public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return null;
          }

          @Override
          public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay,
              long period, TimeUnit unit) {
            return null;
          }

          @Override
          public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
              long delay, TimeUnit unit) {
            return null;
          }

          @Override
          public boolean isShutdown() {
            return false;
          }

          @Override
          public boolean isTerminated() {
            return false;
          }

          @Override
          public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
            return false;
          }

          @Override
          public <T> List<java.util.concurrent.Future<T>> invokeAll(
              Collection<? extends Callable<T>> collection)
              throws InterruptedException {
            return null;
          }

          @Override
          public <T> List<java.util.concurrent.Future<T>> invokeAll(
              Collection<? extends Callable<T>> collection, long l,
              TimeUnit timeUnit) throws InterruptedException {
            return null;
          }

          @Override
          public <T> T invokeAny(Collection<? extends Callable<T>> collection)
              throws InterruptedException, ExecutionException {
            return null;
          }

          @Override
          public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l,
              TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
          }

          @Override
          public void execute(Runnable runnable) {

          }
        }), gameLobbyChannels));
    assertTrue(
        !gameLobbyChannels.containsUser(userDto) && gameLobbyChannels.getUsers().size() == 0);
  }

  @Test
  void joinAndLeaveGameLobby() {
    Channel channel = createChannel("123");
    GameLobbyChannels gameLobbyChannels = new GameLobbyChannels();
    UserDto userDto = new UserDto("Name", 2, 10.5, 12, 1, 400);
    gameLobbyChannels.putUsers(userDto, channel);
    LobbyConfiguration.setServerLobby(new ServerLobby("lele", "12123"));

    GameLobby gameLobby = new GameLobby("test", 3, "lele", true, 0, userDto);
    gameLobbyChannels.createGameLobby(gameLobby, channel);
    assertTrue(gameLobbyChannels.containsUser(userDto) && gameLobbyChannels.getUsers().size() == 1);
    assertTrue(gameLobbyChannels.gameChannels.size() == 1);
    gameLobbyChannels.handleExit(channel,
        new GameServerFrameHandler(new DefaultChannelGroup(new EventExecutor() {
          @Override
          public EventExecutor next() {
            return null;
          }

          @Override
          public EventExecutorGroup parent() {
            return null;
          }

          @Override
          public boolean inEventLoop() {
            return false;
          }

          @Override
          public boolean inEventLoop(Thread thread) {
            return false;
          }

          @Override
          public <V> Promise<V> newPromise() {
            return null;
          }

          @Override
          public <V> ProgressivePromise<V> newProgressivePromise() {
            return null;
          }

          @Override
          public <V> Future<V> newSucceededFuture(V result) {
            return null;
          }

          @Override
          public <V> Future<V> newFailedFuture(Throwable cause) {
            return null;
          }

          @Override
          public boolean isShuttingDown() {
            return false;
          }

          @Override
          public Future<?> shutdownGracefully() {
            return null;
          }

          @Override
          public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
            return null;
          }

          @Override
          public Future<?> terminationFuture() {
            return null;
          }

          @Override
          public void shutdown() {

          }

          @Override
          public List<Runnable> shutdownNow() {
            return null;
          }

          @Override
          public Iterator<EventExecutor> iterator() {
            return null;
          }

          @Override
          public Future<?> submit(Runnable task) {
            return null;
          }

          @Override
          public <T> Future<T> submit(Runnable task, T result) {
            return null;
          }

          @Override
          public <T> Future<T> submit(Callable<T> task) {
            return null;
          }

          @Override
          public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return null;
          }

          @Override
          public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return null;
          }

          @Override
          public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay,
              long period, TimeUnit unit) {
            return null;
          }

          @Override
          public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
              long delay, TimeUnit unit) {
            return null;
          }

          @Override
          public boolean isShutdown() {
            return false;
          }

          @Override
          public boolean isTerminated() {
            return false;
          }

          @Override
          public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
            return false;
          }

          @Override
          public <T> List<java.util.concurrent.Future<T>> invokeAll(
              Collection<? extends Callable<T>> collection)
              throws InterruptedException {
            return null;
          }

          @Override
          public <T> List<java.util.concurrent.Future<T>> invokeAll(
              Collection<? extends Callable<T>> collection, long l,
              TimeUnit timeUnit) throws InterruptedException {
            return null;
          }

          @Override
          public <T> T invokeAny(Collection<? extends Callable<T>> collection)
              throws InterruptedException, ExecutionException {
            return null;
          }

          @Override
          public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l,
              TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
          }

          @Override
          public void execute(Runnable runnable) {

          }
        }), gameLobbyChannels));
    assertTrue(
        !gameLobbyChannels.containsUser(userDto) && gameLobbyChannels.getUsers().size() == 0);
    assertTrue(gameLobbyChannels.gameChannels.size() == 0);

  }

  @Test
  void oneUserJoinsServerLobby_OneJoinsGameLobbyAndThenLeaves() {
    GameLobbyChannels gameLobbyChannels = new GameLobbyChannels();

    Channel channel = createChannel("123");
    UserDto userDto = new UserDto("Name", 2, 10.5, 12, 1, 400);
    gameLobbyChannels.putUsers(userDto, channel);

    Channel channel2 = createChannel("456");
    UserDto userDto2 = new UserDto("Name2", 2, 10.5, 12, 1, 400);
    gameLobbyChannels.putUsers(userDto2, channel2);
    LobbyConfiguration.setServerLobby(new ServerLobby("lele", "12123"));

    GameLobby gameLobby = new GameLobby("test", 3, "lele", true, 0, userDto);
    gameLobbyChannels.createGameLobby(gameLobby, channel);
    assertTrue(gameLobbyChannels.containsUser(userDto));
    assertTrue(gameLobbyChannels.containsUser(userDto2));
    assertTrue(gameLobbyChannels.getUsers().size() == 2);
    assertTrue(gameLobbyChannels.gameChannels.size() == 1);

    gameLobbyChannels.handleExit(channel,
        new GameServerFrameHandler(new DefaultChannelGroup(new EventExecutor() {
          @Override
          public EventExecutor next() {
            return null;
          }

          @Override
          public EventExecutorGroup parent() {
            return null;
          }

          @Override
          public boolean inEventLoop() {
            return false;
          }

          @Override
          public boolean inEventLoop(Thread thread) {
            return false;
          }

          @Override
          public <V> Promise<V> newPromise() {
            return null;
          }

          @Override
          public <V> ProgressivePromise<V> newProgressivePromise() {
            return null;
          }

          @Override
          public <V> Future<V> newSucceededFuture(V result) {
            return null;
          }

          @Override
          public <V> Future<V> newFailedFuture(Throwable cause) {
            return null;
          }

          @Override
          public boolean isShuttingDown() {
            return false;
          }

          @Override
          public Future<?> shutdownGracefully() {
            return null;
          }

          @Override
          public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
            return null;
          }

          @Override
          public Future<?> terminationFuture() {
            return null;
          }

          @Override
          public void shutdown() {

          }

          @Override
          public List<Runnable> shutdownNow() {
            return null;
          }

          @Override
          public Iterator<EventExecutor> iterator() {
            return null;
          }

          @Override
          public Future<?> submit(Runnable task) {
            return null;
          }

          @Override
          public <T> Future<T> submit(Runnable task, T result) {
            return null;
          }

          @Override
          public <T> Future<T> submit(Callable<T> task) {
            return null;
          }

          @Override
          public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return null;
          }

          @Override
          public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return null;
          }

          @Override
          public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay,
              long period, TimeUnit unit) {
            return null;
          }

          @Override
          public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
              long delay, TimeUnit unit) {
            return null;
          }

          @Override
          public boolean isShutdown() {
            return false;
          }

          @Override
          public boolean isTerminated() {
            return false;
          }

          @Override
          public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
            return false;
          }

          @Override
          public <T> List<java.util.concurrent.Future<T>> invokeAll(
              Collection<? extends Callable<T>> collection)
              throws InterruptedException {
            return null;
          }

          @Override
          public <T> List<java.util.concurrent.Future<T>> invokeAll(
              Collection<? extends Callable<T>> collection, long l,
              TimeUnit timeUnit) throws InterruptedException {
            return null;
          }

          @Override
          public <T> T invokeAny(Collection<? extends Callable<T>> collection)
              throws InterruptedException, ExecutionException {
            return null;
          }

          @Override
          public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l,
              TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
          }

          @Override
          public void execute(Runnable runnable) {

          }
        }), gameLobbyChannels));
    assertTrue(
        !gameLobbyChannels.containsUser(userDto) && gameLobbyChannels.containsUser(userDto2)
            && gameLobbyChannels.getUsers().size() == 1);
    assertTrue(gameLobbyChannels.gameChannels.size() == 0);

  }


}
