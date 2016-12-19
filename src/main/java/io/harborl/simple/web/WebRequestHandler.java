package io.harborl.simple.web;

import java.io.IOException;

public interface WebRequestHandler {
  
  boolean handle(HttpRequest request, HttpResponse response) throws IOException;

}
