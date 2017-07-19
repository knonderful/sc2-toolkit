package sc2toolkit.app.toolkit.overwolf;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.Charset;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;
import sc2toolkit.game.client.model.Player;

/**
 * Default {@link OverwolfAppConnector} implementation.
 */
public class OverwolfAppConnectorImpl implements OverwolfAppConnector {

  private static final Logger LOG = Logger.getLogger(OverwolfAppConnectorImpl.class.getName());
  private final Gson gson = new Gson();
  private final HttpClient httpClient;

  /**
   * Creates a new instance.
   *
   * @param httpClient The {@link HttpClient}.
   */
  OverwolfAppConnectorImpl(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public CompletionStage<Void> startGame(Player opponent) {
    return sendMessage("/startGame", new StartGameMessage(opponent));
  }

  @Override
  public CompletionStage<Void> updateGameTime(double displayTime) {
    return sendMessage("/updateGameTime", new UpdateGameTimeMessage(displayTime));
  }

  @Override
  public CompletionStage<Void> endGame() {
    return sendMessage("/endGame", new EndGameMessage());
  }

  private CompletionStage<Void> sendMessage(String path, Object message) {
    HttpRequest request = createRequest(path, message);
    return httpClient.send(request).thenAccept(response -> {
      if (response.status().codeClass() != HttpStatusClass.SUCCESS) {
        StringBuilder sb = new StringBuilder("Failed to send message to ").append(path).append(": ");
        sb.append(response.status());
        if (response instanceof HttpContent) {
          HttpContent content = (HttpContent) response;
          sb.append(System.lineSeparator());
          sb.append("Content:");
          sb.append(System.lineSeparator());
          sb.append(new String(content.content().copy().array(), Charset.forName("UTF-8")));
        }
        throw new RuntimeException(sb.toString());
      }
    });
  }

  private HttpRequest createRequest(String path, Object message) {
    String messageJson = gson.toJson(message);
    ByteBuf content = Unpooled.wrappedBuffer(messageJson.getBytes());

    /*
     * NOTE: Need to use DefaultFullHttpRequest because otherwise Netty's
     * HttpObjectEncoder's internal state machine will trigger an
     * IllegalStateException on the second HttpRequest that is sent.
     */
    DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path, content);
    HttpHeaders headers = request.headers();
    // This needs to be "localhost" and not "127.0.0.1", since the Overwolf HTTP server expects that.
    headers.add(HttpHeaderNames.HOST, httpClient.getRemoteAddress().getHostString());
    // No content for now; this should be added to the DefaultFullHttpRequest once we start using it.
    headers.add(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
    headers.add(HttpHeaderNames.CONTENT_TYPE, "application/json");
    return request;
  }
}
