package io.harborl.simple.web;

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

public final class StaticServer {
  
  private final int port;
  private final int concurrentLevel;
  private final String folderPath;
  
  /** 
   * Underlying chain-of-responsibility to handle the web request.
   */
  private final Map<HttpMethod, List<WebRequestHandler>> requestHandlers;
  
  /**
   * Underlying request thread-pool based executor.
   */
  private final ExecutorService executor;
  
  private ServerSocket serverSocket;
  
  // guarded by this
  private boolean started;
  
  // guarded by volatile
  private volatile boolean shutdown;
  
  private StaticServer(Builder builder) {
    this.port = builder.port;
    this.concurrentLevel = builder.concurrentLevel;
    this.folderPath = builder.folderPath;
    
    if (this.concurrentLevel < 1 ||
        this.folderPath == null ||
        this.folderPath.isEmpty() ||
        this.port < 0) {
      throw new IllegalArgumentException();
    }
    
    // Initialize the request handler mapping
    requestHandlers = new HashMap<HttpMethod, List<WebRequestHandler>>();
    {
      requestHandlers.put(
        HttpMethod.GET, 
        Arrays.asList(FileListWebRequestHandler.valueOf(folderPath), 
                      FileContentWebRequestHandler.valueOf(folderPath)));
    }
    
    // 1. bounded thread pool with specified concurrent level
    // 2. synchronous hand-over blocking queue
    // 3. call-runs after over-follow
    executor = 
      new ThreadPoolExecutor(0, concurrentLevel,
                             60L, TimeUnit.SECONDS,
                             new SynchronousQueue<Runnable>(),
                             Executors.defaultThreadFactory(),
                             new ThreadPoolExecutor.CallerRunsPolicy());
  }
  
  public void start() throws IOException {
    boolean isStarted = false;
    synchronized(this) {
      isStarted = this.started;
      this.started = true;
    }

    if (!isStarted) {
      serverSocket = new ServerSocket(this.port);

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
    private final Socket socket;
    
    WebRequestDispatch(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {

      HttpRequest httpRequst = null;
      HttpResponse httpResponse = null;

      try {
        httpResponse = HttpResponse.newOf(socket.getOutputStream());
        httpRequst = HttpRequest.newOf(socket.getInputStream());
        
        System.out.println("> [ " + new Date() + " ] " + httpRequst);
        
        if (requestHandlers.containsKey(httpRequst.getMethod())) {
          List<WebRequestHandler> handlers = requestHandlers.get(httpRequst.getMethod());

          for (WebRequestHandler handler : handlers) {
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

  public void shutdown() {
    shutdown = true;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public final static class Builder {
    
    private int port;
    private int concurrentLevel;
    private String folderPath;
    
    private Builder() { }
    
    public Builder port(int port) {
      this.port = port;
      return this;
    }
    
    public Builder concurrentLevel(int level) {
      this.concurrentLevel = level;
      return this;
    }
    
    public Builder folderPath(String path) {
      this.folderPath = path;
      return this;
    }
    
    public StaticServer build() {
      return new StaticServer(this);
    }
  }
}
