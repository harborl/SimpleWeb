package io.harborl.simple.web;

import java.io.OutputStream;

public class HttpResponse {
  
  private final OutputStream outStream;
  
  private HttpResponse(OutputStream outStream) {
    this.outStream = outStream;
  }

  public static HttpResponse newOf(OutputStream outStream) {
    return new HttpResponse(outStream);
  }

  public OutputStream getOutStream() {
    return outStream;
  }

}
