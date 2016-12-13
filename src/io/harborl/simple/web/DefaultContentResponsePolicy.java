package io.harborl.simple.web;

import java.io.File;

public class DefaultContentResponsePolicy implements ContentResponsePolicy {

  @Override
  public boolean dealWith(File file, HttpResponse response) {
    Util.writeResponseQuitely(response.getOutStream(), 
        "OK", 200, "OK");
    
    return true;
  }

}
