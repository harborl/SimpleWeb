package io.harborl.simple.web;

import java.io.IOException;

public final class Bootstrap {
  
  public static void main(final String[] args) {

    if (args.length < 3) {
      printUsage();
      System.exit(-1);
    }

    final int port = Integer.valueOf(args[0]);
    final int level = Integer.valueOf(args[1]);
    final String path = args[2];

    final StaticServer server = 
      StaticServer.newBuilder()
        .port(port)
        .concurrentLevel(level)
        .folderPath(path)
        .build();

    // TODO: install shutdown hook

    try {
      info("SimpleWeb server startup ...");
      info("Listening on: " + port);
      info("Folder hosted on: " + path);
      info("-------------------------------");

      // main thread blocks here
      server.start();
    } catch (IOException e) {
      err("Error raised on startup:");
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

  private static void printUsage() {
    System.err.println("cmd <lisening-port> <concurrent-level> <fold-path>");
  }
  
  private static void info(String info) {
    System.out.println(info);
  } 
  
  private static void err(String err) {
    System.err.println(err);
  }

}
