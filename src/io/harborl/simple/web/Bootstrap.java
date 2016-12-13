package io.harborl.simple.web;

import java.io.IOException;

public class Bootstrap {
  
  public static void main(String[] args) {

    if (args.length < 3) {
      printUsage();
      System.exit(-1);
    }

    final int port = Integer.valueOf(args[0]);
    final int level = Integer.valueOf(args[1]);
    final String path = args[2];

    StaticServer server = 
      StaticServer.newBuilder()
        .port(port)
        .concurrentLevel(level)
        .folderPath(path)
        .build();

    // TODO: install shutdown hook
    
    // main thread blocks here
    try {
      server.start();
    } catch (IOException e) {
      System.err.println("Startup phrase network error raised, check following:");
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

  private static void printUsage() {
    System.err.println("cmd <lisening-port> <concurrent-level> <fold-path>");
  }

}
