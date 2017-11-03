package io.harborl.simple.web.handler;

import io.harborl.simple.web.http.HttpRequest;
import io.harborl.simple.web.http.HttpResponse;

import java.io.IOException;

public interface RequestHandler {
  
  boolean handle(HttpRequest request, HttpResponse response) throws IOException;

}
