package sc2toolkit.app.toolkit.overwolf;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.Charset;
import java.util.concurrent.CompletionStage;
import sc2toolkit.game.client.model.Player;

/**
 * Default {@link OverwolfAppConnector} implementation.
 */
public class OverwolfAppConnectorImpl implements OverwolfAppConnector {

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
  public CompletionStage<Void> startGame(float displayTime, Player opponent) {
    HttpRequest request = createRequest("/startGame");
    return httpClient.send(request).thenAccept(response -> {
      if (response.status().codeClass() != HttpStatusClass.SUCCESS) {
        StringBuilder sb = new StringBuilder("Failed: ");
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

  @Override
  public CompletionStage<Void> updateGameTime(float displayTime) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public CompletionStage<Void> endGame() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  private HttpRequest createRequest(String path) {
    DefaultHttpHeaders headers = new DefaultHttpHeaders();
    headers.add(HttpHeaderNames.HOST, httpClient.getRemoteAddress().getHostString());
    headers.add(HttpHeaderNames.CONTENT_LENGTH, 0);
    return new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path, headers);
  }
}
