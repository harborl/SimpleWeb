package io.harborl.simple.web;

import java.io.File;
import java.io.IOException;

public class DefaultContentResponsePolicy implements ContentResponsePolicy {

  @Override
  public boolean dealWith(File file, HttpResponse response) throws IOException {

    final byte[] data = Util.readBytes(file);
    final String contentType = "application/octet-stream";
    final String contentDispository = Util.buildContentDisposition(file.getName());
    Util.writeBytesResponse(response.getOutStream(), data, contentType, contentDispository);
    
    return true;
  }

}
