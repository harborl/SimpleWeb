package io.harborl.simple.web;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipContentResponsePolicy implements ContentResponsePolicy   {

  private final Pattern pattern = Pattern.compile("(.+?)\\.(zip|gz|tar)", Pattern.CASE_INSENSITIVE);
  
  @Override
  public boolean dealWith(File file, HttpResponse response) throws IOException {
    
    final String fileName = file.getName();
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.matches()) {
      final String contentType = "application/octet-stream";
      Util.copyFileToResponse(response.getOutStream(), file, contentType, 
                              Util.buildContentDisposition(fileName));
      return true;
    }

    return false;
  }

}
