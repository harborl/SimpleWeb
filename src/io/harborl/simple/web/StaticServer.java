package io.harborl.simple.web;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StaticServer {
  
  private final int port;
  private final int concurrentLevel;
  private final String folderPath;
  
  /** 
   * Underlying chain-of-responsibility to handle the web request.
   * */
  private final List<WebRequestHandler> requestHandlers;
  
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
    
    requestHandlers = new ArrayList<WebRequestHandler>();
    requestHandlers.add(FileListWebRequestHandler.valueOf(folderPath));
    requestHandlers.add(FileContentWebRequestHandler.valueOf(folderPath));

    // 1. bounded thread pool with specified concurrent level
    // 2. synchronous hand-over blocking queue
    // 3. call-runs after over-follow
    executor = new ThreadPoolExecutor(0, concurrentLevel,
        60L, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>(),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
  }
  
  public void start() throws IOException {
    boolean isStarted = false;
    synchronized(this) {
      isStarted = this.started;
      this.started = true;
    }
    
    if (!isStarted) {
      serverSocket = new ServerSocket(this.port);
      for (;!shutdown;) {
        executor.execute(
            new WebRequestDispatch(
                serverSocket.accept()
              )
          );
      }
    }
  }
  
  class WebRequestDispatch implements Runnable {
    private final Socket socket;
    
    WebRequestDispatch(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      
      try {
        HttpRequest httpRequst = HttpRequest.newOf(socket.getInputStream());
        HttpResponse httpResponse = HttpResponse.newOf(socket.getOutputStream());
        
        System.out.println("> " + httpRequst);
        
        boolean hasConsumed = false;
        for (WebRequestHandler handler : requestHandlers) {
          hasConsumed = handler.handle(httpRequst, httpResponse);
          if (hasConsumed) {
            return;
          }
        }

        Util.writeResponseQuitely(socket.getOutputStream(), 
                                  "no handler found", 400, "Not Found");
      } catch (Throwable th) {
        try {
          Util.writeErrorQuitely(socket.getOutputStream(), th);
        } catch (IOException ignored) { }
      } finally {
        try {
          // doesn't support keep alive
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
  
  public static class Builder {
    
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
