package io.harborl.simple.web;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTextContentResponsePolicy implements ContentResponsePolicy  {
  private final Pattern pattern = Pattern.compile("(.+?)\\.(html|txt|htm)", Pattern.CASE_INSENSITIVE);
  
  @Override
  public boolean dealWith(File file, HttpResponse response) throws IOException {
    
    final String fileName = file.getName();
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.matches()) {
      final String contentType = "text/html";
      final byte[] data = Util.readBytes(file);
      Util.writeBytesResponse(response.getOutStream(), data, contentType, null);
      return true;
    }

    return false;
  }
}
