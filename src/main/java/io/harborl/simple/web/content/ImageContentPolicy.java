package io.harborl.simple.web.content;

import io.harborl.simple.web.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImageContentPolicy implements ContentPolicy {

  private final Pattern pattern = Pattern.compile("(.+?)\\.(png|jpg|gif|bmp|svg|ico)", Pattern.CASE_INSENSITIVE);

  @Override
  public boolean dealWith(File file, HttpResponse response) throws IOException {
    
    final String fileName = file.getName();
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.matches()) {

      final String extend = matcher.group(2);
      String contentType;
      if (extend.equalsIgnoreCase("ico")) {
        contentType = "image/x-icon";
      } else if (extend.equalsIgnoreCase("svg")) {
        contentType = "image/svg+xml";
      } else {
        contentType = "image/" + extend;
      }

      response.copyFile(file, contentType, null);
      return true;
    }

    return false;
  }

}
