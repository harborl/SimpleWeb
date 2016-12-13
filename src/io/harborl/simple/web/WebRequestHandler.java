package io.harborl.simple.web;

public interface WebRequestHandler {
  
  boolean handle(HttpRequest request, HttpResponse response);

}
