package io.harborl.simple.web;

import java.io.File;
import java.io.IOException;

public final class DefaultContentResponsePolicy implements ContentResponsePolicy {

  @Override
  public boolean dealWith(File file, HttpResponse response) throws IOException {

    final String contentType = "application/octet-stream";
    final String contentDispository = Util.buildContentDisposition(file.getName());
    response.copyFile(file, contentType, contentDispository);
    
    return true;
  }

}
