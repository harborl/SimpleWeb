package io.harborl.simple.web.content;

import io.harborl.simple.web.http.HttpResponse;
import io.harborl.simple.web.Util;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;

public final class DefaultContentPolicy implements ContentPolicy {

  private final MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

  @Override
  public boolean dealWith(File file, HttpResponse response) throws IOException {

    final String contentType = fileTypeMap.getContentType(file);
    final String contentDispository = Util.buildContentDisposition(file.getName());
    response.copyFile(file, contentType, contentDispository);

    return true;
  }

}
