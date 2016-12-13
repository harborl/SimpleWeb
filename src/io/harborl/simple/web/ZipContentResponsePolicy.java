package io.harborl.simple.web;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipContentResponsePolicy implements ContentResponsePolicy   {

  private final Pattern pattern = Pattern.compile("(.+?)\\.(zip|gz|tar)");
  
  @Override
  public boolean dealWith(File file, HttpResponse response) {
    
    final String fileName = file.getName();
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.matches()) {
      final String contentType = "application/octet-stream";
      final byte[] data = Util.readBytes(file);
      Util.writeBytesQuitely(response.getOutStream(), data, contentType, 
                             Util.buildContentDisposition(fileName));
      return true;
    }

    return false;
  }

}
