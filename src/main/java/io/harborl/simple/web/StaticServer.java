package io.harborl.simple.web;

import io.harborl.simple.web.handler.FileRequestHandler;
import io.harborl.simple.web.handler.FolderRequestHandler;
import io.harborl.simple.web.handler.RequestHandler;
import io.harborl.simple.web.http.HttpMethod;
import io.harborl.simple.web.http.HttpRequest;
import io.harborl.simple.web.http.HttpResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class StaticServer {

  private final int port;

  /** 
   * Underlying chain-of-responsibility to handle the web request.
   */
  private final Map<HttpMethod, List<RequestHandler>> handlerChains;

  /**
   * Underlying request thread-pool based executor.
   */
  private final ExecutorService executor;

  // guarded by this
  private boolean started;

  // guarded by volatile
  private volatile boolean shutdown;

  private StaticServer(Builder builder) {
    this.port = builder.port;
    int concurrentLevel = builder.concurrentLevel;
    String folderPath = builder.folderPath;

    if (concurrentLevel < 1 ||
        folderPath == null ||
        folderPath.isEmpty() ||
        this.port < 0) {
      throw new IllegalArgumentException();
    }

    // Initialize the request handler mapping
    handlerChains = new HashMap<>();
    {
      handlerChains.put(HttpMethod.GET,
        Arrays.asList(FolderRequestHandler.valueOf(folderPath),
                      FileRequestHandler.valueOf(folderPath))
      );
    }

    // 1. bounded thread pool with specified concurrent level
    // 2. synchronous hand-over blocking queue
    // 3. call-runs after over-follow
    executor = 
      new ThreadPoolExecutor(0, concurrentLevel,
                             60L, TimeUnit.SECONDS,
                             new SynchronousQueue<>(),
                             Executors.defaultThreadFactory(),
                             new ThreadPoolExecutor.CallerRunsPolicy());
  }

  void start() throws IOException {
    boolean isStarted;
    synchronized(this) {
      isStarted = this.started;
      this.started = true;
    }

    if (!isStarted) {
      ServerSocket serverSocket = new ServerSocket(this.port);

      for ( ;!shutdown; ) {
        executor.execute(
            new WebRequestDispatch(
                serverSocket.accept()
              )
          );
      }
    }
  }
  
  final class WebRequestDispatch implements Runnable {
    final Socket socket;

    WebRequestDispatch(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {

      HttpRequest httpRequst;
      HttpResponse httpResponse = null;

      try {
        httpResponse = HttpResponse.newOf(socket.getOutputStream());
        httpRequst = HttpRequest.newOf(socket.getInputStream());

        System.out.println("> [ " + new Date() + " ] " + httpRequst);

        if (handlerChains.containsKey(httpRequst.getMethod())) {
          List<RequestHandler> chain = handlerChains.get(httpRequst.getMethod());

          for (RequestHandler handler : chain) {
            if (handler.handle(httpRequst, httpResponse)) {
              return;
            }
          }
        }

        httpResponse.writeText("no handler found\n", 400, "Bad Request");
      } catch (Throwable th) {
        // We suppress all of exceptions and just to try to report the error to client,
        // which is a trade-off for simplifying the practice.
        try {
          if (httpResponse != null) 
            httpResponse.writeError(th);
        } catch (IOException ignored) { }
      } finally {
        try {
          // doesn't support keep alive.
          // just close the socket quietly.
          socket.close();
        } catch (IOException ignored) { }
      }

    }
  }

  void shutdown() {
    shutdown = true;
    executor.shutdown();
  }

  static Builder newBuilder() {
    return new Builder();
  }

  final static class Builder {

    private int port;
    private int concurrentLevel;
    private String folderPath;
    
    private Builder() { }
    
    Builder port(int port) {
      this.port = port;
      return this;
    }

    Builder concurrentLevel(int level) {
      this.concurrentLevel = level;
      return this;
    }

    Builder folderPath(String path) {
      this.folderPath = path;
      return this;
    }

    StaticServer build() {
      return new StaticServer(this);
    }
  }
}
