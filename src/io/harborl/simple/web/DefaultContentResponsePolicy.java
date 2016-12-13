package io.harborl.simple.web;

import java.io.File;

public class DefaultContentResponsePolicy implements ContentResponsePolicy {

  @Override
  public boolean dealWith(File file, HttpResponse response) {

    final byte[] data = Util.readBytes(file);
    final String contentType = "application/octet-stream";
    final String contentDispository = null;
    Util.writeBytesQuitely(response.getOutStream(), data, contentType, contentDispository);
    
    return true;
  }

}
