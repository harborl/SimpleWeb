package io.harborl.simple.web;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageContentResponsePolicy implements ContentResponsePolicy  {

  private final Pattern pattern = Pattern.compile("(.+?)\\.(png|jpg|gif|bmp)");
  
  @Override
  public boolean dealWith(File file, HttpResponse response) throws IOException {
    
    final String fileName = file.getName();
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.matches()) {
      final String extend = matcher.group(2);
      final String contentType = "image/" + extend;
      final byte[] data = Util.readBytes(file);
      Util.writeBytesQuitely(response.getOutStream(), data, contentType, null);
      return true;
    }

    return false;
  }

}
