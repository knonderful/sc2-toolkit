package sc2toolkit.app.toolkit.overwolf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An HTTP client that handles one request/response transaction at a time. This
 * implementation tries to re-establish the connection upon sending a request
 * when it has been lost previously. It is up to the caller to set keep-alive
 * HTTP headers, if this is desired.
 * <p>
 * TODO: 
 * - Handle the disconnect case from the server side (in this case an exception
 *   reaches the end of the pipeline, but the HttpClient does not know about
 *   it).
 */
public class HttpClient {

  private static final Logger LOG = Logger.getLogger(HttpClient.class.getName());
  private final RequestResponseTracker rrt;
  private final InetSocketAddress address;
  private final Bootstrap bootstrap;
  private final AtomicBoolean hasBeenStarted;
  private CompletionStage<Channel> channelFuture;

  /**
   * Creates a new instance.
   *
   * @param address The socket address to which connect.
   */
  public HttpClient(InetSocketAddress address) {
    this.address = address;
    this.rrt = new RequestResponseTracker();
    this.bootstrap = createBootstrap(rrt);
    this.hasBeenStarted = new AtomicBoolean(false);
  }

  /**
   * Retrieves the remote address.
   *
   * @return The address.
   */
  public InetSocketAddress getRemoteAddress() {
    return address;
  }

  /**
   * Starts the HTTP client.
   * <p>
   * The {@link HttpClient} can only be started once and has to be started
   * before any messages are sent.
   *
   * @return A {@link CompletionStage} for the operation result.
   */
  public synchronized CompletionStage<Void> start() {
    if (hasBeenStarted.getAndSet(true)) {
      return errorFuture(new IllegalStateException("This instance has already been started."));
    }

    return CompletableFuture.completedFuture(null);
  }

  /**
   * Stops the HTTP client.
   *
   * @return A {@link CompletionStage} for the operation result.
   */
  public synchronized CompletionStage<Void> stop() {
    if (channelFuture == null) {
      shutDownBootstrap();
      return CompletableFuture.completedFuture(null);
    }

    return channelFuture.thenCompose(channel -> {
      CompletableFuture<Channel> channelCloseFuture = wrapFuture(channel.closeFuture());
      shutDownBootstrap();

      return channelCloseFuture.thenAccept(future -> {
        LOG.info(String.format("Shut down %s successfully.", HttpClient.class.getSimpleName()));
      });
    });
  }

  private void shutDownBootstrap() {
    bootstrap.config().group().shutdownGracefully();
  }

  /**
   * Sends an HTTP request.
   *
   * @param request The request.
   * @return The {@link CompletionStage} for the response.
   */
  public synchronized CompletionStage<HttpResponse> send(HttpRequest request) {
    return getChannel().thenCompose(channel -> {
      CompletableFuture<HttpResponse> out = new CompletableFuture<>();

      // Need to set the consumer before we send the message, otherwise we might get the response before and everything is screwed...
      rrt.setResponseConsumer(out::complete);
      wrapFuture(channel.writeAndFlush(request)).whenComplete((chanFuture, cause) -> {
        if (cause != null) {
          LOG.log(Level.WARNING, "Message could not be sent.", cause);
        }

        rrt.setResponseConsumer(null);
      });

      return out;
    });
  }

  private static Bootstrap createBootstrap(RequestResponseTracker rrt) {
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(new NioEventLoopGroup(1))
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer() {
              @Override
              protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpClientCodec());
                p.addLast(new HttpContentDecompressor());
                p.addLast(new HttpObjectAggregator(1048576));
                p.addLast(new HttpResponseHandler(rrt));
              }
            });
    return bootstrap;
  }

  private CompletionStage<Channel> getChannel() {
    return connect();
  }

  private CompletionStage<Channel> connect() {
    if (channelFuture != null) {
      // This means a connection is in progress (either connecting or connected)
      return channelFuture;
    }

    if (!hasBeenStarted.get()) {
      return errorFuture(new IllegalStateException("The client has not yet been started."));
    }

    LOG.log(Level.INFO, String.format("Connecting to %s.", address));

    // Not connected, so establish a new connection
    channelFuture = wrapFuture(bootstrap.connect(address));
    channelFuture.whenComplete(this::handleConnectCompletion);
    return channelFuture;
  }

  private synchronized void handleConnectCompletion(Channel channel, Throwable error) {
    // Note that this method needs to be synchronized, as it is called from a future
    if (error != null) {
      LOG.log(Level.INFO, String.format("Failed to connect to %s.", address), error);
      this.channelFuture = null;
      return;
    }

    LOG.log(Level.INFO, String.format("Connected at %s to %s (%s).", channel.localAddress(), channel.remoteAddress(), channel.isActive()));
  }

  private static <T> CompletionStage<T> errorFuture(Throwable error) {
    CompletableFuture<T> errorFuture = new CompletableFuture<>();
    errorFuture.completeExceptionally(error);
    return errorFuture;
  }

  private static CompletableFuture<Channel> wrapFuture(ChannelFuture channelFuture) {
    CompletableFuture<Channel> out = new CompletableFuture<>();

    channelFuture.addListener(future -> {
      if (future.isSuccess()) {
        out.complete(channelFuture.channel());
        return;
      }

      Throwable cause = future.cause();
      if (cause == null) {
        cause = new Exception("Unknown error.");
      }
      out.completeExceptionally(cause);
    });
    return out;
  }

  private static class RequestResponseTracker {

    private Consumer<HttpResponse> responseConsumer;

    public synchronized void setResponseConsumer(Consumer<HttpResponse> responseConsumer) {
      if (responseConsumer != null && this.responseConsumer != null) {
        throw new IllegalStateException("Already waiting for a response.");
      }
      this.responseConsumer = responseConsumer;
    }

    public synchronized void handleResponse(HttpResponse httpResponse) {
      if (responseConsumer == null) {
        LOG.warning(String.format("Received an unexpected response: %s.", httpResponse));
        return;
      }

      responseConsumer.accept(httpResponse);
      responseConsumer = null;
    }
  }

  private static class HttpResponseHandler extends SimpleChannelInboundHandler<HttpResponse> {

    private final RequestResponseTracker rrt;

    public HttpResponseHandler(RequestResponseTracker rrt) {
      super();
      this.rrt = rrt;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpResponse response) throws Exception {
      LOG.fine(() -> String.format("Received HTTP response: %s.", response));

      rrt.handleResponse(response);
    }
  }
}
